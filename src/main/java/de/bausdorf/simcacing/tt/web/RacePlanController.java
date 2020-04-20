package de.bausdorf.simcacing.tt.web;

import de.bausdorf.simcacing.tt.planning.RacePlanRepository;
import de.bausdorf.simcacing.tt.planning.model.RacePlan;
import de.bausdorf.simcacing.tt.planning.model.RacePlanParameters;
import de.bausdorf.simcacing.tt.stock.CarRepository;
import de.bausdorf.simcacing.tt.stock.DriverRepository;
import de.bausdorf.simcacing.tt.stock.TeamRepository;
import de.bausdorf.simcacing.tt.stock.TrackRepository;
import de.bausdorf.simcacing.tt.stock.model.IRacingCar;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.stock.model.IRacingTeam;
import de.bausdorf.simcacing.tt.stock.model.IRacingTrack;
import de.bausdorf.simcacing.tt.util.TimeTools;
import de.bausdorf.simcacing.tt.web.model.PlanParametersView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
        model.addAttribute(RACEPLAN, new PlanParametersView());
        model.addAttribute("plans", planRepository.findByTeamIds(getMyTeams().stream()
                .map(IRacingTeam::getId)
                .collect(Collectors.toList()))
        );
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
            addError("Car ID " + planView.getCarId() + " not found", model);
            hasErrors = true;
        }
        if( !track.isPresent() ) {
            addError("Track ID " + planView.getTrackId() + " not found", model);
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
            model.addAttribute(RACEPLAN, new PlanParametersView());
            return NEWRACEPLAN_VIEW;
        }
        if( mode.isPresent() ) {
            model.addAttribute(VIEW_MODE, mode.get());
        } else {
            model.addAttribute(VIEW_MODE, "time");
        }
        String racePlanId = (planView != null && planView.getId() != null) ? planView.getId() : planId.get();
        Optional<RacePlanParameters> racePlanParameters = planRepository.findById(racePlanId);
        if (!racePlanParameters.isPresent()) {
            addError("Race plan id " + planView.getId() + " not found", model);
            model.addAttribute(RACEPLAN, new PlanParametersView());
            return NEWRACEPLAN_VIEW;
        }
        RacePlan racePlan = RacePlan.createRacePlanTemplate(racePlanParameters.get());
        racePlanParameters.get().setStints(racePlan.getCurrentRacePlan());

        model.addAttribute(AUTHORIZED_DRIVERS, getAuthorizedDrivers(racePlanParameters.get().getTeamId()));
        model.addAttribute(SELECTED_PLAN, racePlanParameters.get());
        return PLANNING_VIEW;
    }

    @PostMapping("/planning")
    public String updatePlanning(@ModelAttribute RacePlanParameters viewPlanParameters, @RequestParam(VIEW_MODE) Optional<String> mode, Model model) {
        Optional<RacePlanParameters> repoPlanParameters = planRepository.findById(viewPlanParameters.getId());
        if (!repoPlanParameters.isPresent()) {
            addError("Race plan id " + viewPlanParameters.getId() + " not found", model);
            return NEWRACEPLAN_VIEW;
        }
        if( mode.isPresent() ) {
            model.addAttribute(VIEW_MODE, mode.get());
        } else {
            model.addAttribute(VIEW_MODE, "time");
        }

        repoPlanParameters.get().updateData(viewPlanParameters);
        repoPlanParameters.get().updateDrivers(driverRepository);
        RacePlan racePlan = RacePlan.createRacePlanTemplate(repoPlanParameters.get());
        repoPlanParameters.get().setStints(racePlan.getCurrentRacePlan());

        planRepository.save(repoPlanParameters.get());

        model.addAttribute(AUTHORIZED_DRIVERS, getAuthorizedDrivers(repoPlanParameters.get().getTeamId()));
        model.addAttribute(SELECTED_PLAN, repoPlanParameters.get());
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
}
