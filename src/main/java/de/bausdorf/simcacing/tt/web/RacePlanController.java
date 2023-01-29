package de.bausdorf.simcacing.tt.web;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 bausdorf engineering
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import de.bausdorf.simcacing.tt.planning.PlanParameterRepository;
import de.bausdorf.simcacing.tt.planning.persistence.Estimation;
import de.bausdorf.simcacing.tt.planning.RacePlan;
import de.bausdorf.simcacing.tt.planning.persistence.PlanParameters;
import de.bausdorf.simcacing.tt.planning.persistence.Roster;
import de.bausdorf.simcacing.tt.planning.persistence.ScheduleEntry;
import de.bausdorf.simcacing.tt.stock.*;
import de.bausdorf.simcacing.tt.stock.model.*;
import de.bausdorf.simcacing.tt.util.TimeTools;
import de.bausdorf.simcacing.tt.util.UnitConverter;
import de.bausdorf.simcacing.tt.web.model.planning.DriverEstimationView;
import de.bausdorf.simcacing.tt.web.model.planning.DriverScheduleView;
import de.bausdorf.simcacing.tt.web.model.planning.EstimationView;
import de.bausdorf.simcacing.tt.web.model.planning.NewEstimationEntryView;
import de.bausdorf.simcacing.tt.web.model.planning.NewScheduleEntryView;
import de.bausdorf.simcacing.tt.web.model.planning.PlanDescriptionView;
import de.bausdorf.simcacing.tt.web.model.planning.PlanParametersView;
import de.bausdorf.simcacing.tt.web.model.planning.PlanningViewModeType;
import de.bausdorf.simcacing.tt.web.model.planning.ScheduleView;
import de.bausdorf.simcacing.tt.web.model.planning.TeamEstimationView;
import de.bausdorf.simcacing.tt.web.model.planning.TeamScheduleView;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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

	public static final String TEAM_ESTIMATIONS = "teamEstimations";
	public static final String NEW_ESTIMATION_ENTRY = "newEstimationEntry";
	public static final String PLAN_ID = "planId";

	private final StockDataRepository stockDataRepository;
	final TeamRepository teamRepository;
	final PlanParameterRepository planRepository;
	final DriverStatsRepository statsRepository;

	public RacePlanController(@Autowired StockDataRepository stockDataRepository,
			@Autowired TeamRepository teamRepository,
			@Autowired DriverRepository driverRepository,
			@Autowired PlanParameterRepository racePlanRepository,
			@Autowired DriverStatsRepository statsRepository) {
		this.stockDataRepository = stockDataRepository;
		this.teamRepository = teamRepository;
		this.driverRepository = driverRepository;
		this.planRepository = racePlanRepository;
		this.statsRepository = statsRepository;
	}

	@GetMapping("/newraceplan")
	@Secured({ "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
	public String viewNewRacePlan(Model model) {
		prepareNewRacePlanView(model);
		return NEWRACEPLAN_VIEW;
	}

	@PostMapping("/newraceplan")
	@Secured({ "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
	@Transactional
	public String createNewRacePlan(@ModelAttribute PlanParametersView planView, Model model) {
		if (planView.getId() != null) {
			return doPlanning(planView, Optional.of("time"), Optional.empty(), model);
		}

		Optional<IRacingCar> car = stockDataRepository.findCarById(planView.getCarId().toString());
		Optional<IRacingTrack> track = stockDataRepository.findTrackById(planView.getTrackId().toString());
		boolean hasErrors = false;
		if (car.isEmpty()) {
			addError("Car ID " + planView.getCarId() + NOT_FOUND, model);
			hasErrors = true;
		}
		if (track.isEmpty()) {
			addError("Track ID " + planView.getTrackId() + NOT_FOUND, model);
			hasErrors = true;
		}
		if (hasErrors) {
			return ScheduleController.EVENTSCHEDULE_VIEW;
		}

		String planId = UUID.randomUUID().toString();
		PlanParameters planParameters = PlanParameters.builder()
				.id(planId)
				.carId(planView.getCarId())
				.trackId(planView.getTrackId())
				.teamId(planView.getTeamId())
				.sessionStartDateTime(OffsetDateTime.of(planView.getStartTime(), ZoneId.of(currentUser().getZoneIdName()).getRules().getOffset(planView.getStartTime())))
				.todStartTime(planView.getTodStartTime() == null ? planView.getStartTime() : planView.getTodStartTime())
				.raceDuration(planView.getRaceDuration())
				.name(planView.getPlanName())
				.avgPitLaneTime(Duration.ofMinutes(1))
				.avgLapTime(TimeTools.durationFromString(track.get().getNominalLapTime()))
				.maxCarFuel(car.get().getMaxFuel())
				.avgFuelPerLap(0.0D)
				.greenFlagOffsetTime(Duration.ZERO)
				.stints(new ArrayList<>())
				.roster(new Roster())
				.build();

		planRepository.save(planParameters);

		model.addAttribute(VIEW_MODE, PlanningViewModeType.time.name());
		prepareModel(planParameters, model);
		return PLANNING_VIEW;
	}

	@GetMapping("/planning")
	@Secured({ "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
	@Transactional
	public String doPlanning(@ModelAttribute(SELECTED_PLAN) PlanParametersView planView,
			@RequestParam(VIEW_MODE) Optional<String> mode,
			@RequestParam(PLAN_ID) Optional<String> planId,
			Model model) {
		if ((planView == null || planView.getId() == null) && planId.isEmpty()) {
			prepareNewRacePlanView(model);
			return ScheduleController.EVENTSCHEDULE_VIEW;
		}
		prepareViewMode(mode, model);

		PlanParameters racePlanParameters = loadRacePlan(planView, planId, model);
		if (racePlanParameters == null) {
			return ScheduleController.EVENTSCHEDULE_VIEW;
		}

		RacePlan racePlan = RacePlan.createRacePlanTemplate(racePlanParameters);
		racePlanParameters.updateStints(racePlan.getCurrentRacePlan());
		racePlanParameters.shiftTimezone(currentUser().getTimezone());

		prepareModel(racePlanParameters, model);
		return PLANNING_VIEW;
	}

	@PostMapping("/planning")
	@Secured({ "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
	@Transactional
	public String updatePlanning(@ModelAttribute(SELECTED_PLAN) PlanParametersView viewPlanParameters,
								 @ModelAttribute(TEAM_SCHEDULE) TeamScheduleView teamScheduleView,
								 @RequestParam(VIEW_MODE) Optional<String> mode,
								 Model model) {
		PlanParameters repoPlanParameters = loadRacePlan(viewPlanParameters.getId(), model);
		if (repoPlanParameters == null) {
			return ScheduleController.EVENTSCHEDULE_VIEW;
		}

		viewPlanParameters.updateEntity(repoPlanParameters);
		if (viewPlanParameters.getAllDriverIds() != null) {
			repoPlanParameters.getRoster().updateDrivers(
					driverRepository.findAllByIdIn(viewPlanParameters.getAllDriverIds().stream()
							.map(Object::toString).collect(Collectors.toList())
					),
					repoPlanParameters.getSessionStartDateTime()
			);
		}
		updateDriverSchedule(repoPlanParameters, teamScheduleView);
		RacePlan racePlan = RacePlan.createRacePlanTemplate(repoPlanParameters);
		repoPlanParameters.updateStints(racePlan.getCurrentRacePlan());
		planRepository.save(repoPlanParameters);

		return redirectBuilder(PLANNING_VIEW)
				.withParameter(VIEW_MODE, mode)
				.withParameter(PLAN_ID, viewPlanParameters.getId())
				.build(model);
	}

	@GetMapping("/deleteplan")
	@Secured({ "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
	public String deleteRacePlan(@RequestParam(PLAN_ID) String planId, Model model) {
		try {
			planRepository.deleteById(planId);
		} catch (Exception e) {
			addError(e.getMessage(), model);
			log.error(e.getMessage(), e);
		}
		prepareNewRacePlanView(model);

		return redirectBuilder(ScheduleController.EVENTSCHEDULE_VIEW).build(model);
	}

	@PostMapping("/updateSchedule")
	@Secured({ "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
	@Transactional
	public String updateSchedule(@ModelAttribute(TEAM_SCHEDULE) TeamScheduleView teamScheduleView,
			@RequestParam(VIEW_MODE) Optional<String> mode,
			Model model) {
		PlanParameters repoPlanParameters = loadRacePlan(teamScheduleView.getPlanId(), model);
		if (repoPlanParameters == null) {
			return ScheduleController.EVENTSCHEDULE_VIEW;
		}

		updateDriverSchedule(repoPlanParameters, teamScheduleView);
		RacePlan racePlan = RacePlan.createRacePlanTemplate(repoPlanParameters);
		repoPlanParameters.updateStints(racePlan.getCurrentRacePlan());
		planRepository.save(repoPlanParameters);

		return redirectBuilder(PLANNING_VIEW)
				.withParameter(VIEW_MODE, mode)
				.withParameter(PLAN_ID, teamScheduleView.getPlanId())
				.build(model);
	}

	@PostMapping("/updateEstimations")
	@Secured({ "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
	public String updateEstimations(@ModelAttribute(TEAM_ESTIMATIONS) TeamEstimationView teamEstimationView,
			@RequestParam(VIEW_MODE) Optional<String> mode,
			Model model) {
		PlanParameters repoPlanParameters = loadRacePlan(teamEstimationView.getPlanId(), model);
		if (repoPlanParameters == null) {
			return ScheduleController.EVENTSCHEDULE_VIEW;
		}

		updateDriverEstimations(repoPlanParameters, teamEstimationView);
		RacePlan racePlan = RacePlan.createRacePlanTemplate(repoPlanParameters);
		repoPlanParameters.updateStints(racePlan.getCurrentRacePlan());
		planRepository.save(repoPlanParameters);

		return redirectBuilder(PLANNING_VIEW)
				.withParameter(VIEW_MODE, mode)
				.withParameter(PLAN_ID, teamEstimationView.getPlanId())
				.build(model);
	}

	@GetMapping("/deleteScheduleEntry")
	@Secured({ "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
	public String deleteScheduleEntry(@RequestParam("driverId") String driverId,
			@RequestParam("viewMode") Optional<String> viewMode,
			@RequestParam("timeslot") String timeslot,
			@RequestParam(PLAN_ID) String planId,
			Model model) {
		if (planId == null || planId.isEmpty()) {
			prepareNewRacePlanView(model);
			return ScheduleController.EVENTSCHEDULE_VIEW;
		}
		PlanParameters planParameters = loadRacePlan(planId, model);
		if (planParameters == null) {
			return ScheduleController.EVENTSCHEDULE_VIEW;
		}

		List<ScheduleEntry> driverSchedule = planParameters.getRoster().getDriverAvailability().stream()
				.filter(e -> driverId.equals(e.getDriver().getId()))
				.collect(Collectors.toList());
		ScheduleEntry toDelete = null;
		for (ScheduleEntry entry : driverSchedule) {
			OffsetDateTime fromTime = entry.getFromTime();
			OffsetDateTime zonedTimeslot = OffsetDateTime.of(fromTime.toLocalDate(), LocalTime.parse(timeslot),
					currentUser().getTimezone().getRules().getOffset(LocalDateTime.of(fromTime.toLocalDate(), LocalTime.parse(timeslot))));
			if (entry.getFromTime().equals(zonedTimeslot)) {
				toDelete = entry;
			}
		}
		if (toDelete != null) {
			planParameters.getRoster().getDriverAvailability().remove(toDelete);
		}
		planRepository.save(planParameters);

		return redirectBuilder(PLANNING_VIEW)
				.withParameter(VIEW_MODE, viewMode)
				.withParameter(PLAN_ID, planId)
				.build(model);
	}

	@GetMapping("/deleteEstimationEntry")
	@Secured({ "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
	public String deleteEstimationEntry(@RequestParam("driverId") String driverId,
			@RequestParam("viewMode") Optional<String> viewMode,
			@RequestParam("timeslot") String timeslot,
			@RequestParam(PLAN_ID) String planId,
			Model model) {
		if (planId == null || planId.isEmpty()) {
			prepareNewRacePlanView(model);
			return ScheduleController.EVENTSCHEDULE_VIEW;
		}
		PlanParameters planParameters = loadRacePlan(planId, model);
		if (planParameters == null) {
			return ScheduleController.EVENTSCHEDULE_VIEW;
		}

		List<Estimation> driverEstimation = planParameters.getRoster().getDriverEstimations().stream()
				.filter(e -> driverId.equals(e.getDriver().getId()))
				.collect(Collectors.toList());
		Estimation toDelete = null;
		for (Estimation entry : driverEstimation) {
			if (entry.getTodFrom().toLocalTime().equals(LocalTime.parse(timeslot))) {
				toDelete = entry;
			}
		}
		if (toDelete != null) {
			planParameters.getRoster().getDriverEstimations().remove(toDelete);
		}

		planRepository.save(planParameters);

		return redirectBuilder(PLANNING_VIEW)
				.withParameter(VIEW_MODE, viewMode)
				.withParameter(PLAN_ID, planId)
				.build(model);
	}

	@PostMapping("/addScheduleEntry")
	@Secured({ "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
	public String addScheduleEntry(@RequestParam(VIEW_MODE) Optional<String> viewMode,
			@ModelAttribute(NEW_SCHEDULE_ENTRY) NewScheduleEntryView newScheduleEntryView,
			Model model) {
		PlanParameters planParameters = loadRacePlan(newScheduleEntryView.getPlanId(), model);
		if (planParameters == null) {
			return ScheduleController.EVENTSCHEDULE_VIEW;
		}
		Optional<IRacingDriver> driver = driverRepository.findById(newScheduleEntryView.getDriverId());
		if (driver.isPresent()) {
			OffsetDateTime sessionStartTime = planParameters.getSessionStartDateTime();
			OffsetDateTime dateTime;
			LocalDate raceDate = sessionStartTime.toLocalDate();
			if (newScheduleEntryView.getTimeFrom().isAfter(sessionStartTime.toLocalTime())
					|| newScheduleEntryView.getTimeFrom().equals(sessionStartTime.toLocalTime())) {
				dateTime = OffsetDateTime.of(raceDate, newScheduleEntryView.getTimeFrom(),
						currentUser().getTimezone().getRules().getOffset(LocalDateTime.of(raceDate, newScheduleEntryView.getTimeFrom())));
			} else {
				dateTime = OffsetDateTime.of(raceDate.plusDays(1), newScheduleEntryView.getTimeFrom(),
						currentUser().getTimezone().getRules().getOffset(LocalDateTime.of(raceDate.plusDays(1), newScheduleEntryView.getTimeFrom())));
			}
			planParameters.getRoster().getDriverAvailability().add(ScheduleEntry.builder()
					.driver(driver.get())
					.status(newScheduleEntryView.getStatus())
					.fromTime(dateTime)
					.build());

			planRepository.save(planParameters);
		}

		return redirectBuilder(PLANNING_VIEW)
				.withParameter(VIEW_MODE, viewMode)
				.withParameter(PLAN_ID, newScheduleEntryView.getPlanId())
				.build(model);
	}

	@PostMapping("/addEstimationEntry")
	@Secured({ "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
	public String addEstimationEntry(@RequestParam(VIEW_MODE) Optional<String> viewMode,
			@ModelAttribute(NEW_ESTIMATION_ENTRY) NewEstimationEntryView newEstimationEntryView,
			Model model) {
		PlanParameters planParameters = loadRacePlan(newEstimationEntryView.getPlanId(), model);
		if (planParameters == null) {
			return ScheduleController.EVENTSCHEDULE_VIEW;
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
			planParameters.getRoster().getDriverEstimations().add(Estimation.builder()
					.driver(driver.get())
					.todFrom(dateTime)
					.avgFuelPerLap(newEstimationEntryView.getAvgFuelPerLap())
					.avgLapTime(newEstimationEntryView.getAvgLapTime())
					.build());

			RacePlan racePlan = RacePlan.createRacePlanTemplate(planParameters);
			planParameters.updateStints(racePlan.getCurrentRacePlan());

			planRepository.save(planParameters);
		}

		return redirectBuilder(PLANNING_VIEW)
				.withParameter(VIEW_MODE, viewMode)
				.withParameter(PLAN_ID, newEstimationEntryView.getPlanId())
				.build(model);
	}

	@GetMapping("/checkdriverstats")
	public String checkDriverStats(@RequestParam String driverId, @RequestParam String planId, Model model) {

		prepareViewMode(Optional.of(PlanningViewModeType.variation.name()), model);

		if (planId == null || planId.isEmpty()) {
			addError("No plan id given", model);
			return ScheduleController.EVENTSCHEDULE_VIEW;
		}
		Optional<PlanParameters> planParameters = planRepository.findById(planId);
		if (planParameters.isPresent()) {
			prepareModel(planParameters.get(), model);
			DriverStatsPk statsPk = DriverStatsPk.builder()
					.driverId(driverId)
					.trackId(Long.toString(planParameters.get().getTrackId()))
					.carId(Long.toString(planParameters.get().getCarId()))
					.build();
			Optional<DriverStats> driverStats = statsRepository.findById(statsPk);
			if (driverStats.isPresent()) {
				StatsEntry entry = driverStats.get().getFastestEntry();
				NewEstimationEntryView newEstimationEntryView = (NewEstimationEntryView) model.getAttribute(NEW_ESTIMATION_ENTRY);
				if (entry != null && newEstimationEntryView != null) {
					newEstimationEntryView.setDriverId(driverId);
					newEstimationEntryView.setAvgFuelPerLap(UnitConverter.round(entry.getAvgFuelPerLap(), 2));
					newEstimationEntryView.setAvgLapTime(entry.getAvgLapTime());
					newEstimationEntryView.setTimeFrom(entry.getTodStart());
				}
			}
		}
		return PLANNING_VIEW;
	}

	@ModelAttribute("teams")
	public List<IRacingTeam> getMyTeams() {
		return getMyTeams(teamRepository);
	}

	@ModelAttribute("tracks")
	List<IRacingTrack> getAllTracks() {
		return stockDataRepository.loadAllTracks();
	}

	@ModelAttribute("cars")
	List<IRacingCar> getAllCars() {
		return stockDataRepository.loadAllCars();
	}

	List<IRacingDriver> getAuthorizedDrivers(Long teamId) {
		Optional<IRacingTeam> team = teamRepository.findById(teamId.toString());
		List<IRacingDriver> authorizedDrivers = new ArrayList<>();
		team.ifPresent(t -> authorizedDrivers.addAll(driverRepository.findAllByIdIn(t.getAuthorizedDriverIds()).stream()
				.sorted(Comparator.comparing(IRacingDriver::getName))
				.collect(Collectors.toList())
		));
		return authorizedDrivers;
	}

	private TeamScheduleView createTeamScheduleView(PlanParameters planParameters) {
		List<ScheduleEntry> availability = planParameters.getRoster().getDriverAvailability();
		TeamScheduleView teamScheduleView = new TeamScheduleView(planParameters.getId());

		availability.forEach(teamScheduleView::addDriverScheduleView);

		return teamScheduleView;
	}

	private TeamEstimationView createTeamEstimationView(PlanParameters planParameters) {
		List<Estimation> estimations = planParameters.getRoster().getDriverEstimations();
		TeamEstimationView teamEstimationView = new TeamEstimationView(planParameters.getId());

		for (Estimation estimation : estimations) {
			DriverEstimationView driverEstimationView = new DriverEstimationView();
			driverEstimationView.setDriverId(estimation.getDriver().getId());
			driverEstimationView.setDriverName(estimation.getDriver().getName());
			driverEstimationView.setValidated(estimation.getDriver().isValidated());
			driverEstimationView.getEstimationEntries().add(EstimationView.builder()
					.validFrom(estimation.getTodFrom())
					.avgLapTime(estimation.getAvgLapTime())
					.avgFuelPerLap(estimation.getAvgFuelPerLap())
					.build());
			teamEstimationView.getTeamEstimations().add(driverEstimationView);
		}
		return teamEstimationView;
	}

	private void updateDriverSchedule(PlanParameters planParameters, TeamScheduleView teamScheduleView) {
		if (teamScheduleView == null || teamScheduleView.getPlanId() == null) {
			return;
		}
		Roster roster = planParameters.getRoster();

		roster.getDriverAvailability().clear();
		for (DriverScheduleView driverScheduleView : teamScheduleView.getTeamSchedule()) {
			IRacingDriver driver = IRacingDriver.builder()
					.id(driverScheduleView.getDriverId())
					.name(driverScheduleView.getDriverName())
					.validated(driverScheduleView.isValidated())
					.build();
			for (ScheduleView scheduleView : driverScheduleView.getScheduleEntries()) {
				roster.getDriverAvailability().add(ScheduleEntry.builder()
						.driver(driver)
						.status(scheduleView.getStatus())
						.fromTime(OffsetDateTime.of(scheduleView.getValidFrom(), currentUser().getTimezone().getRules().getOffset(scheduleView.getValidFrom())))
						.build());
			}
		}
	}

	private void updateDriverEstimations(PlanParameters planParameters, TeamEstimationView teamEstimationView) {
		if (teamEstimationView == null) {
			return;
		}
		Roster roster = planParameters.getRoster();
		for (DriverEstimationView driverEstimationView : teamEstimationView.getTeamEstimations()) {
			roster.getDriverEstimations().clear();
			IRacingDriver driver = IRacingDriver.builder()
					.id(driverEstimationView.getDriverId())
					.name(driverEstimationView.getDriverName())
					.validated(driverEstimationView.isValidated())
					.build();
			for (EstimationView estimationView : driverEstimationView.getEstimationEntries()) {
				roster.getDriverEstimations().add(Estimation.builder()
						.driver(driver)
						.todFrom(estimationView.getValidFrom())
						.avgLapTime(estimationView.getAvgLapTime())
						.avgFuelPerLap(estimationView.getAvgFuelPerLap())
						.build());
			}
		}
	}

	private PlanParameters loadRacePlan(String planId, Model model) {
		Optional<PlanParameters> racePlanParameters = planRepository.findById(planId);
		if (racePlanParameters.isEmpty()) {
			model.addAttribute(RACEPLAN, new PlanParametersView());
			return null;
		}
		return racePlanParameters.get();
	}

	private PlanParameters loadRacePlan(PlanParametersView planView, Optional<String> planId, Model model) {
		String racePlanId;
		if (planView != null && planView.getId() != null) {
			racePlanId = planView.getId();
		} else {
			racePlanId = planId.orElse(null);
		}
		if (racePlanId == null) {
			addError("Unable to identify race plan - no id", model);
			prepareNewRacePlanView(model);
			return null;
		}
		return loadRacePlan(racePlanId, model);
	}

	private void prepareNewRacePlanView(Model model) {
		model.addAttribute(RACEPLAN, new PlanParametersView());
		model.addAttribute("plans", planRepository.findAllByTeamIdIn(getMyTeams().stream()
				.map(t -> Long.parseLong(t.getId()))
				.collect(Collectors.toList())).stream()
				.map(s -> PlanDescriptionView.builder()
						.id(s.getId())
						.name(s.getName())
						.team(teamRepository.findById(Long.toString(s.getTeamId())).orElse(IRacingTeam.builder()
								.name("Team not found")
								.build()).getName()
						)
						.build()
				)
				.collect(Collectors.toList())
		);
	}

	private void prepareViewMode(Optional<String> mode, Model model) {
		if (mode.isPresent()) {
			model.addAttribute(VIEW_MODE, mode.get());
		} else {
			model.addAttribute(VIEW_MODE, "TIME");
		}
	}

	private void prepareModel(PlanParameters racePlanParameters, Model model) {
		model.addAttribute(AUTHORIZED_DRIVERS, getAuthorizedDrivers(racePlanParameters.getTeamId()));
		PlanParametersView viewPlanParameters = PlanParametersView.fromEntity(racePlanParameters, currentUser().getTimezone());
		viewPlanParameters.setStints(RacePlan.createRacePlanTemplate(racePlanParameters).getCurrentRacePlan());

		model.addAttribute(SELECTED_PLAN, viewPlanParameters);
		final PlanningViewModeType modelViewMode = viewModeFromModel(model);
		if (PlanningViewModeType.time == modelViewMode || PlanningViewModeType.variation == modelViewMode) {
			model.addAttribute("stintTableRows", new Integer[50]);
		}
		if (PlanningViewModeType.schedule == modelViewMode) {
			model.addAttribute(TEAM_SCHEDULE, createTeamScheduleView(racePlanParameters));
			model.addAttribute(NEW_SCHEDULE_ENTRY, new NewScheduleEntryView(racePlanParameters.getId()));
		}
		if (PlanningViewModeType.variation == modelViewMode) {
			model.addAttribute(TEAM_ESTIMATIONS, createTeamEstimationView(racePlanParameters));
			model.addAttribute(NEW_ESTIMATION_ENTRY, NewEstimationEntryView.builder()
							.planId(racePlanParameters.getId())
							.timeFrom(racePlanParameters.getTodStartTime().toLocalTime())
							.build());
		}
	}

	private PlanningViewModeType viewModeFromModel(Model model) {
		return PlanningViewModeType.valueOf((String) model.getAttribute(VIEW_MODE));
	}
}
