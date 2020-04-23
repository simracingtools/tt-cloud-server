package de.bausdorf.simcacing.tt.web;

import de.bausdorf.simcacing.tt.planning.RacePlanRepository;
import de.bausdorf.simcacing.tt.planning.model.Estimation;
import de.bausdorf.simcacing.tt.planning.model.RacePlan;
import de.bausdorf.simcacing.tt.planning.model.RacePlanParameters;
import de.bausdorf.simcacing.tt.planning.model.Roster;
import de.bausdorf.simcacing.tt.planning.model.ScheduleEntry;
import de.bausdorf.simcacing.tt.planning.model.Stint;
import de.bausdorf.simcacing.tt.stock.CarRepository;
import de.bausdorf.simcacing.tt.stock.DriverRepository;
import de.bausdorf.simcacing.tt.stock.TeamRepository;
import de.bausdorf.simcacing.tt.stock.TrackRepository;
import de.bausdorf.simcacing.tt.stock.model.IRacingCar;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.stock.model.IRacingTeam;
import de.bausdorf.simcacing.tt.stock.model.IRacingTrack;
import de.bausdorf.simcacing.tt.util.TimeTools;
import de.bausdorf.simcacing.tt.web.model.DriverEstimationView;
import de.bausdorf.simcacing.tt.web.model.DriverScheduleView;
import de.bausdorf.simcacing.tt.web.model.EstimationView;
import de.bausdorf.simcacing.tt.web.model.NewEstimationEntryView;
import de.bausdorf.simcacing.tt.web.model.NewScheduleEntryView;
import de.bausdorf.simcacing.tt.web.model.PlanParametersView;
import de.bausdorf.simcacing.tt.web.model.PlanningViewModeType;
import de.bausdorf.simcacing.tt.web.model.ScheduleView;
import de.bausdorf.simcacing.tt.web.model.StintDriverView;
import de.bausdorf.simcacing.tt.web.model.TeamEstimationView;
import de.bausdorf.simcacing.tt.web.model.TeamScheduleView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class RacePlanController extends BaseController {

    public static final String SELECTED_PLAN = "selectedPlan";
    public static final String NEWRACEPLAN_VIEW = "newraceplan";
    public static final String PLANNING_VIEW = "planning";
    public static final String RACEPLAN = "raceplan";
    public static final String AUTHORIZED_DRIVERS = "authorizedDrivers";
    public static final String VIEW_MODE = "viewMode";
    public static final String NOT_FOUND = " not found";
    public static final String TEAM_SCHEDULE = "teamSchedule";
    public static final String NEW_SCHEDULE_ENTRY = "newScheduleEntry";
    public static final String STINT_DRIVERS = "stintDriverView";

    private static final String[] DRIVER_COLORS = {
            "background-color: rgb(235,100,100);color: rgb(249,246,246);",
            "background-color: rgb(100,235,100);",
            "background-color: rgb(59,59,255);color: rgb(249,246,246);",
            "background-color: rgb(230,100,220);color: rgb(249,246,246);",
            "background-color: rgb(255,182,141);",
            "background-color: rgb(225,217,29);",
            "background-color: rgb(29,225,213);",
            "background-color: rgb(29,225,178);",
            "background-color: rgb(116,119,118);",
            "background-color: rgb(181,204,196);",
    };
    public static final String TEAM_ESTIMATIONS = "teamEstimations";
    public static final String NEW_ESTIMATION_ENTRY = "newEstimationEntry";

    TrackRepository trackRepository;
    CarRepository carRepository;
    TeamRepository teamRepository;
    RacePlanRepository planRepository;

    public RacePlanController(@Autowired TrackRepository trackRepository,
                              @Autowired CarRepository carRepository,
                              @Autowired TeamRepository teamRepository,
                              @Autowired DriverRepository driverRepository,
                              @Autowired RacePlanRepository racePlanRepository) {
        this.trackRepository = trackRepository;
        this.carRepository = carRepository;
        this.teamRepository = teamRepository;
        this.driverRepository = driverRepository;
        this.planRepository = racePlanRepository;
    }

    @GetMapping("/newraceplan")
    public String viewNewRacePlan(Model model) {
        prepareNewRacePlanView(model);
        return NEWRACEPLAN_VIEW;
    }

    @PostMapping("/newraceplan")
    public String createNewRacePlan(@ModelAttribute PlanParametersView planView, Model model) {
        if( planView.getId() != null ) {
            return doPlanning(planView, Optional.of("time"), Optional.empty(), model);
        }

        Optional<IRacingCar> car = carRepository.findByName(planView.getCarId());
        Optional<IRacingTrack> track = trackRepository.findById(planView.getTrackId());
        boolean hasErrors = false;
        if( !car.isPresent() ) {
            addError("Car ID " + planView.getCarId() + NOT_FOUND, model);
            hasErrors = true;
        }
        if( !track.isPresent() ) {
            addError("Track ID " + planView.getTrackId() + NOT_FOUND, model);
            hasErrors = true;
        }
        if (hasErrors) {
            return NEWRACEPLAN_VIEW;
        }

        String planId = UUID.randomUUID().toString();
        planRepository.save(RacePlanParameters.builder()
                .id(planId)
                .carId(planView.getCarId())
                .trackId(planView.getTrackId())
                .teamId(planView.getTeamId())
                .sessionStartTime(LocalDateTime.parse(planView.getStartTime(), DateTimeFormatter.ofPattern("HH:mm")))
                .raceDuration(TimeTools.durationFromPattern(planView.getRaceDuration(), "HH:mm"))
                .name(planView.getPlanName())
                .avgPitStopTime(Duration.ofMinutes(1))
                .avgLapTime(TimeTools.durationFromString(track.get().getNominalLapTime()))
                .maxCarFuel(car.get().getMaxFuel())
                .avgFuelPerLap(0.0D)
                .build());

        planView.setId(planId);
        model.addAttribute(SELECTED_PLAN, planView);

        return PLANNING_VIEW;
    }

    @GetMapping("/planning")
    public String doPlanning(@ModelAttribute(SELECTED_PLAN) PlanParametersView planView,
            @RequestParam(VIEW_MODE) Optional<String> mode,
            @RequestParam("planId") Optional<String> planId,
            Model model) {
        if((planView == null || planView.getId() == null) && !planId.isPresent()) {
            prepareNewRacePlanView(model);
            return NEWRACEPLAN_VIEW;
        }
        prepareViewMode(mode, model);

        RacePlanParameters racePlanParameters = loadRacePlan(planView, planId, model);
        if( racePlanParameters == null ) {
            return NEWRACEPLAN_VIEW;
        }

        RacePlan racePlan = RacePlan.createRacePlanTemplate(racePlanParameters);
        racePlanParameters.setStints(racePlan.getCurrentRacePlan());

        prepareModel(racePlanParameters, model);
        return PLANNING_VIEW;
    }

    @PostMapping("/planning")
    public String updatePlanning(@ModelAttribute(SELECTED_PLAN) RacePlanParameters viewPlanParameters,
            @ModelAttribute(TEAM_SCHEDULE) TeamScheduleView teamScheduleView,
            @RequestParam(VIEW_MODE) Optional<String> mode,
            Model model) {
        RacePlanParameters repoPlanParameters = loadRacePlan(viewPlanParameters.getId(), model);
        if (repoPlanParameters == null) {
            return NEWRACEPLAN_VIEW;
        }
        prepareViewMode(mode, model);

        repoPlanParameters.updateData(viewPlanParameters);
        repoPlanParameters.updateDrivers(driverRepository);
        updateDriverSchedule(repoPlanParameters, teamScheduleView);
        RacePlan racePlan = RacePlan.createRacePlanTemplate(repoPlanParameters);
        repoPlanParameters.setStints(racePlan.getCurrentRacePlan());

        planRepository.save(repoPlanParameters);

        prepareModel(repoPlanParameters, model);
        return PLANNING_VIEW;
    }

    @PostMapping("/updateSchedule")
    public String updateSchedule(@ModelAttribute(TEAM_SCHEDULE) TeamScheduleView teamScheduleView,
            @RequestParam(VIEW_MODE) Optional<String> mode,
            Model model) {
        RacePlanParameters repoPlanParameters = loadRacePlan(teamScheduleView.getPlanId(), model);
        if (repoPlanParameters == null) {
            return NEWRACEPLAN_VIEW;
        }
        prepareViewMode(mode, model);

        updateDriverSchedule(repoPlanParameters, teamScheduleView);
        RacePlan racePlan = RacePlan.createRacePlanTemplate(repoPlanParameters);
        repoPlanParameters.setStints(racePlan.getCurrentRacePlan());

        planRepository.save(repoPlanParameters);

        prepareModel(repoPlanParameters, model);
        return PLANNING_VIEW;
    }

    @PostMapping("/updateEstimations")
    public String updateEstimations(@ModelAttribute(TEAM_ESTIMATIONS) TeamEstimationView teamEstimationView,
            @RequestParam(VIEW_MODE) Optional<String> mode,
            Model model) {
        RacePlanParameters repoPlanParameters = loadRacePlan(teamEstimationView.getPlanId(), model);
        if (repoPlanParameters == null ) {
            return NEWRACEPLAN_VIEW;
        }
        prepareViewMode(mode, model);

        updateDriverEstimations(repoPlanParameters, teamEstimationView);
        RacePlan racePlan = RacePlan.createRacePlanTemplate(repoPlanParameters);
        repoPlanParameters.setStints(racePlan.getCurrentRacePlan());

        planRepository.save(repoPlanParameters);

        prepareModel(repoPlanParameters, model);
        return PLANNING_VIEW;
    }

    @GetMapping("/deleteScheduleEntry")
    public String deleteScheduleEntry(@RequestParam("driverId") String driverId,
            @RequestParam("viewMode") Optional<String> viewMode,
            @RequestParam("timeslot") String timeslot,
            @RequestParam("planId") String planId,
            Model model) {
        if (planId == null || planId.isEmpty()) {
            prepareNewRacePlanView(model);
            return NEWRACEPLAN_VIEW;
        }
        RacePlanParameters planParameters = loadRacePlan(planId, model);
        if (planParameters == null) {
            return NEWRACEPLAN_VIEW;
        }
        prepareViewMode(viewMode, model);

        List<ScheduleEntry> driverSchedule = planParameters.getRoster().getDriverAvailability().get(driverId);
        ScheduleEntry toDelete = null;
        for (ScheduleEntry entry : driverSchedule) {
            if (entry.getFromTime().equals(LocalTime.parse(timeslot))) {
                toDelete = entry;
            }
        }
        if (toDelete != null) {
            planParameters.getRoster().getDriverAvailability().get(driverId).remove(toDelete);
        }

        planRepository.save(planParameters);

        prepareModel(planParameters, model);
        return PLANNING_VIEW;
    }

    @GetMapping("/deleteEstimationEntry")
    public String deleteEstimationEntry(@RequestParam("driverId") String driverId,
            @RequestParam("viewMode") Optional<String> viewMode,
            @RequestParam("timeslot") String timeslot,
            @RequestParam("planId") String planId,
            Model model) {
        if (planId == null || planId.isEmpty()) {
            prepareNewRacePlanView(model);
            return NEWRACEPLAN_VIEW;
        }
        RacePlanParameters planParameters = loadRacePlan(planId, model);
        if (planParameters == null) {
            return NEWRACEPLAN_VIEW;
        }
        prepareViewMode(viewMode, model);

        List<Estimation> driverEstimation = planParameters.getRoster().getDriverEstimations().get(driverId);
        Estimation toDelete = null;
        for (Estimation entry : driverEstimation) {
            if (entry.getTodTime().equals(LocalTime.parse(timeslot))) {
                toDelete = entry;
            }
        }
        if (toDelete != null) {
            planParameters.getRoster().getDriverEstimations().get(driverId).remove(toDelete);
        }

        planRepository.save(planParameters);

        prepareModel(planParameters, model);
        return PLANNING_VIEW;
    }

    @PostMapping("/addScheduleEntry")
    public String addScheduleEntry(@RequestParam(VIEW_MODE) Optional<String> viewMode,
            @ModelAttribute(NEW_SCHEDULE_ENTRY) NewScheduleEntryView newScheduleEntryView,
            Model model) {
        prepareViewMode(viewMode, model);
        RacePlanParameters planParameters = loadRacePlan(newScheduleEntryView.getPlanId(), model);
        if (planParameters == null) {
            return NEWRACEPLAN_VIEW;
        }
        Optional<IRacingDriver> driver = driverRepository.findById(newScheduleEntryView.getDriverId());
        if (driver.isPresent()) {
            LocalDateTime sessionStartTime = planParameters.getSessionStartTime();
            LocalDateTime dateTime;
            LocalDate raceDate = sessionStartTime.toLocalDate();
            if (newScheduleEntryView.getTimeFrom().isAfter(sessionStartTime.toLocalTime())
                    || newScheduleEntryView.getTimeFrom().equals(sessionStartTime.toLocalTime())) {
                dateTime = LocalDateTime.of(raceDate, newScheduleEntryView.getTimeFrom());
            } else {
                dateTime = LocalDateTime.of(raceDate.plusDays(1), newScheduleEntryView.getTimeFrom());
            }
            planParameters.getRoster().addScheduleEntry(ScheduleEntry.builder()
                    .driver(driver.get())
                    .status(newScheduleEntryView.getStatus())
                    .from(dateTime)
                    .build());

            planRepository.save(planParameters);
        }

        prepareModel(planParameters, model);
        return PLANNING_VIEW;
    }

    @PostMapping("/addEstimationEntry")
    public String addEstimationEntry(@RequestParam(VIEW_MODE) Optional<String> viewMode,
            @ModelAttribute(NEW_ESTIMATION_ENTRY) NewEstimationEntryView newEstimationEntryView,
            Model model) {
        prepareViewMode(viewMode, model);
        RacePlanParameters planParameters = loadRacePlan(newEstimationEntryView.getPlanId(), model);
        if (planParameters == null) {
            return NEWRACEPLAN_VIEW;
        }
        Optional<IRacingDriver> driver = driverRepository.findById(newEstimationEntryView.getDriverId());
        if (driver.isPresent()) {
            LocalDateTime todStartTime = planParameters.getTodStartTime();
            LocalDateTime dateTime;
            LocalDate raceDate = todStartTime.toLocalDate();
            if (newEstimationEntryView.getTimeFrom().isAfter(todStartTime.toLocalTime())
                    || newEstimationEntryView.getTimeFrom().equals(todStartTime.toLocalTime())) {
                dateTime = LocalDateTime.of(raceDate, newEstimationEntryView.getTimeFrom());
            } else {
                dateTime = LocalDateTime.of(raceDate.plusDays(1), newEstimationEntryView.getTimeFrom());
            }
            planParameters.getRoster().addEstimation(Estimation.builder()
                    .driver(driver.get())
                    .todFrom(dateTime)
                    .avgFuelPerLap(newEstimationEntryView.getAvgFuelPerLap())
                    .avgLapTime(newEstimationEntryView.getAvgLapTime())
                    .build());

            RacePlan racePlan = RacePlan.createRacePlanTemplate(planParameters);
            planParameters.setStints(racePlan.getCurrentRacePlan());

            planRepository.save(planParameters);
        }

        prepareModel(planParameters, model);
        return PLANNING_VIEW;
    }

    @PostMapping("/updateStintDrivers")
    public String updateStintDrivers(@RequestParam(VIEW_MODE) Optional<String> viewMode,
            @ModelAttribute(STINT_DRIVERS) StintDriverView driverView,
            Model model) {
        prepareViewMode(viewMode, model);
        RacePlanParameters planParameters = loadRacePlan(driverView.getPlanId(), model);
        if (planParameters == null) {
            return NEWRACEPLAN_VIEW;
        }
        for (int i = 0; i < planParameters.getStints().size(); i++) {
            planParameters.getStints().get(i).setDriverName(driverView.getStintDrivers().get(i));
        }
        planRepository.save(planParameters);

        prepareModel(planParameters, model);
        return PLANNING_VIEW;
    }

    @ModelAttribute("teams")
    public List<IRacingTeam> getMyTeams() {
        List<IRacingTeam> teams = teamRepository.findByOwnerId(currentUser().getIRacingId());
        teams.addAll(teamRepository.findByAuthorizedDrivers(currentUser().getIRacingId()).stream()
                .filter(s -> !teams.contains(s)).collect(Collectors.toList())
        );
        return teams;
    }

    @ModelAttribute("tracks")
    List<IRacingTrack> getAllTracks() {
        return trackRepository.loadAll(true);
    }

    @ModelAttribute("cars")
    List<IRacingCar> getAllCars() {
        return carRepository.loadAll(true);
    }

    List<IRacingDriver> getAuthorizedDrivers(String teamId) {
        Optional<IRacingTeam> team =  teamRepository.findById(teamId);
        List<IRacingDriver> authorizedDrivers = new ArrayList<>();
        if (team.isPresent()) {
            for (String driverId : team.get().getAuthorizedDriverIds()) {
                Optional<IRacingDriver> driver = driverRepository.findById(driverId);
                if (driver.isPresent()) {
                    authorizedDrivers.add(driver.get());
                }
            }
        }
        return authorizedDrivers;
    }

    private TeamScheduleView createTeamScheduleView(RacePlanParameters planParameters) {
        Map<String, List<ScheduleEntry>> availabilityMap = planParameters.getRoster().getDriverAvailability();
        TeamScheduleView teamScheduleView = new TeamScheduleView(planParameters.getId());

        for (List<ScheduleEntry> driverSchedule : availabilityMap.values()) {
            if (driverSchedule != null && !driverSchedule.isEmpty()) {
                DriverScheduleView driverScheduleView = new DriverScheduleView();
                for (ScheduleEntry scheduleEntry : driverSchedule) {
                    driverScheduleView.setDriverId(scheduleEntry.getDriver().getId());
                    driverScheduleView.setDriverName(scheduleEntry.getDriverName());
                    driverScheduleView.setValidated(scheduleEntry.getDriver().isValidated());
                    driverScheduleView.getScheduleEntries().add(ScheduleView.builder()
                            .validFrom(scheduleEntry.getFrom())
                            .status(scheduleEntry.getStatus())
                            .build());
                }
                teamScheduleView.getTeamSchedule().add(driverScheduleView);
            }
        }
        return teamScheduleView;
    }

    private TeamEstimationView createTeamEstimationView(RacePlanParameters planParameters) {
        Map<String, List<Estimation>> estimationMap = planParameters.getRoster().getDriverEstimations();
        TeamEstimationView teamEstimationView = new TeamEstimationView(planParameters.getId());

        for (List<Estimation> driverEstimation : estimationMap.values()) {
            if (driverEstimation != null && !driverEstimation.isEmpty()) {
                DriverEstimationView driverEstimationView = new DriverEstimationView();
                for (Estimation estimation : driverEstimation) {
                    driverEstimationView.setDriverId(estimation.getDriver().getId());
                    driverEstimationView.setDriverName(estimation.getDriverName());
                    driverEstimationView.setValidated(estimation.getDriver().isValidated());
                    driverEstimationView.getEstimationEntries().add(EstimationView.builder()
                            .validFrom(estimation.getTodFrom())
                            .avgLapTime(estimation.getAvgLapTime())
                            .avgFuelPerLap(estimation.getAvgFuelPerLap())
                            .build());
                }
                teamEstimationView.getTeamEstimations().add(driverEstimationView);
            }
        }
        return teamEstimationView;
    }

    private void updateDriverSchedule(RacePlanParameters planParameters, TeamScheduleView teamScheduleView) {
        if (teamScheduleView == null) {
            return;
        }
        Roster roster = planParameters.getRoster();
        for (DriverScheduleView driverScheduleView : teamScheduleView.getTeamSchedule()) {
            roster.getDriverAvailability().get(driverScheduleView.getDriverId()).clear();
            IRacingDriver driver = IRacingDriver.builder()
                    .id(driverScheduleView.getDriverId())
                    .name(driverScheduleView.getDriverName())
                    .validated(driverScheduleView.isValidated())
                    .build();
            for (ScheduleView scheduleView : driverScheduleView.getScheduleEntries()) {
                roster.addScheduleEntry(ScheduleEntry.builder()
                        .driver(driver)
                        .status(scheduleView.getStatus())
                        .from(scheduleView.getValidFrom())
                        .build());
            }
        }
    }

    private void updateDriverEstimations(RacePlanParameters planParameters, TeamEstimationView teamEstimationView) {
        if (teamEstimationView == null) {
            return;
        }
        Roster roster = planParameters.getRoster();
        for (DriverEstimationView driverEstimationView : teamEstimationView.getTeamEstimations()) {
            roster.getDriverEstimations().get(driverEstimationView.getDriverId()).clear();
            IRacingDriver driver = IRacingDriver.builder()
                    .id(driverEstimationView.getDriverId())
                    .name(driverEstimationView.getDriverName())
                    .validated(driverEstimationView.isValidated())
                    .build();
            for (EstimationView estimationView : driverEstimationView.getEstimationEntries()) {
                roster.addEstimation(Estimation.builder()
                        .driver(driver)
                        .todFrom(estimationView.getValidFrom())
                        .avgLapTime(estimationView.getAvgLapTime())
                        .avgFuelPerLap(estimationView.getAvgFuelPerLap())
                        .build());
            }
        }
    }

    private StintDriverView createStintDriverView(RacePlanParameters planParameters) {
        StintDriverView driverView = new StintDriverView(
                planParameters.getId(),
                planParameters.getStints().stream()
                        .map(Stint::getDriverName)
                        .collect(Collectors.toList())
        );
        int colorIndex = 0;
        Map<String, String> driverColors = new HashMap<>();
        for (IRacingDriver driver : planParameters.getAllDrivers()) {
            driverColors.put(driver.getName(), DRIVER_COLORS[colorIndex++ % 9]);
        }
        for(String name : driverView.getStintDrivers()) {
            driverView.getStyles().add(driverColors.get(name));
        }
        return driverView;
    }

    private RacePlanParameters loadRacePlan(String planId, Model model) {
        Optional<RacePlanParameters> racePlanParameters = planRepository.findById(planId);
        if (!racePlanParameters.isPresent()) {
            model.addAttribute(RACEPLAN, new PlanParametersView());
            return null;
        }
        return racePlanParameters.get();
    }

    private RacePlanParameters loadRacePlan(PlanParametersView planView, Optional<String> planId, Model model) {
        String racePlanId;
        if(planView != null && planView.getId() != null) {
            racePlanId = planView.getId();
        } else {
            racePlanId = planId.orElse(null);
        }
        if( racePlanId == null ) {
            addError("Unable to identify race plan - no id", model);
            prepareNewRacePlanView(model);
            return null;
        }
        return loadRacePlan(racePlanId, model);
    }

    private void prepareNewRacePlanView(Model model) {
        model.addAttribute(RACEPLAN, new PlanParametersView());
        model.addAttribute("plans", planRepository.findByTeamIds(getMyTeams().stream()
                .map(IRacingTeam::getId)
                .collect(Collectors.toList()))
        );
    }

    private PlanningViewModeType prepareViewMode(Optional<String> mode, Model model) {
        if( mode.isPresent() ) {
            model.addAttribute(VIEW_MODE, mode.get());
        } else {
            model.addAttribute(VIEW_MODE, "TIME");
        }
        return PlanningViewModeType.valueOf((String)model.getAttribute(VIEW_MODE));
    }

    private void prepareModel(RacePlanParameters racePlanParameters, Model model) {
        model.addAttribute(AUTHORIZED_DRIVERS, getAuthorizedDrivers(racePlanParameters.getTeamId()));
        model.addAttribute(SELECTED_PLAN, racePlanParameters);
        final PlanningViewModeType modelViewMode = viewModeFromModel(model);
        if (PlanningViewModeType.time == modelViewMode || PlanningViewModeType.variation == modelViewMode) {
            model.addAttribute(STINT_DRIVERS, createStintDriverView(racePlanParameters));
        }
        if (PlanningViewModeType.schedule == modelViewMode) {
            model.addAttribute(TEAM_SCHEDULE, createTeamScheduleView(racePlanParameters));
            model.addAttribute(NEW_SCHEDULE_ENTRY, new NewScheduleEntryView(racePlanParameters.getId()));
        }
        if (PlanningViewModeType.variation == modelViewMode) {
            model.addAttribute(TEAM_ESTIMATIONS, createTeamEstimationView(racePlanParameters));
            model.addAttribute(NEW_ESTIMATION_ENTRY, new NewEstimationEntryView(racePlanParameters.getId()));
        }
    }

    private PlanningViewModeType viewModeFromModel(Model model) {
        return PlanningViewModeType.valueOf((String)model.getAttribute(VIEW_MODE));
    }
}
