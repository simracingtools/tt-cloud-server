package de.bausdorf.simcacing.tt.web;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.bausdorf.simcacing.tt.planning.RacePlanRepository;
import de.bausdorf.simcacing.tt.schedule.RaceEventRepository;
import de.bausdorf.simcacing.tt.schedule.model.RaceEvent;
import de.bausdorf.simcacing.tt.stock.CarRepository;
import de.bausdorf.simcacing.tt.stock.TeamRepository;
import de.bausdorf.simcacing.tt.stock.TrackRepository;
import de.bausdorf.simcacing.tt.stock.model.IRacingCar;
import de.bausdorf.simcacing.tt.stock.model.IRacingTeam;
import de.bausdorf.simcacing.tt.stock.model.IRacingTrack;
import de.bausdorf.simcacing.tt.web.model.planning.PlanParametersView;
import de.bausdorf.simcacing.tt.web.model.schedule.RaceEventView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ScheduleController extends BaseController {

	public static final String EVENTSCHEDULE_VIEW = "eventschedule";
	public static final String REDIRECT_SCHEDULE = "redirect:/schedule";
	final TrackRepository trackRepository;
	final CarRepository carRepository;
	final TeamRepository teamRepository;
	final RaceEventRepository eventRepository;
	final RacePlanRepository planRepository;

	public ScheduleController(@Autowired TrackRepository trackRepository,
			@Autowired CarRepository carRepository,
			@Autowired RaceEventRepository eventRepository,
			@Autowired TeamRepository teamRepository,
			@Autowired RacePlanRepository planRepository) {
		this.carRepository = carRepository;
		this.eventRepository = eventRepository;
		this.trackRepository = trackRepository;
		this.teamRepository = teamRepository;
		this.planRepository = planRepository;
	}

	@GetMapping("/schedule")
	@Secured({ "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
	public String viewNewRacePlan(@RequestParam Optional<String> newEventTemplateId, Model model) {

		RaceEventView eventView = new RaceEventView();
		eventView.setTimezone(currentUserProfile().getTimezone());
		if(newEventTemplateId.isPresent()) {
			RaceEvent eventTemplate = eventRepository.findById(newEventTemplateId.get()).block();
			if(eventTemplate != null) {
				eventView = RaceEventView.fromRaceEvent(eventTemplate);
				eventView.setEventId(null);
				eventView.setSessionDateTime(null);
				eventView.setSaveAndNew(true);
				eventView.setTimezone(currentUserProfile().getTimezone());
			}
		}
		model.addAttribute("newEvent", eventView);
		model.addAttribute("planParamsView", new PlanParametersView());

		return EVENTSCHEDULE_VIEW;
	}

	@PostMapping("/addEvent")
	@Secured({ "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
	public String addEvent(@ModelAttribute RaceEventView newEventView, Model model) {

		try {
			RaceEvent newRaceEvent = newEventView.toEvent();
			newRaceEvent.setEventId(UUID.randomUUID().toString());
			eventRepository.save(newRaceEvent).block();
			newEventView.setEventId(newRaceEvent.getEventId());
		} catch(Exception e) {
			log.error(e.getMessage(), e);
			addError(e.getMessage(), model);
		}
		return REDIRECT_SCHEDULE + (newEventView.isSaveAndNew() ? "?newEventTemplateId=" + newEventView.getEventId() : "");
	}

	@PostMapping("/saveEvent")
	@Secured({ "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
	public String saveEvent(@ModelAttribute RaceEventView raceEventView, Model model) {
		try {
			RaceEvent raceEvent = raceEventView.toEvent();
			if(raceEvent.getEventId() == null || raceEvent.getEventId().isEmpty() || raceEventView.isSaveAndNew()) {
				raceEvent.setEventId(UUID.randomUUID().toString());
			}
			eventRepository.save(raceEvent).block();
			raceEventView.setEventId(raceEvent.getEventId());
		} catch(Exception e) {
			log.error(e.getMessage(), e);
			addError(e.getMessage(), model);
		}

		return REDIRECT_SCHEDULE + (raceEventView.isSaveAndNew() ? "?newEventTemplateId=" + raceEventView.getEventId() : "");
	}

	@GetMapping("/deleteEvent")
	public String deleteEvent(@RequestParam String eventId, Model model) {
		try {
			eventRepository.deleteById(eventId).block();
		} catch(Exception e) {
			log.error(e.getMessage(), e);
			addError(e.getMessage(), model);
		}
		return REDIRECT_SCHEDULE;
	}

	@ModelAttribute("allCars")
	List<IRacingCar> getAllCars() {
		return carRepository.loadAll(true);
	}

	@ModelAttribute("allTracks")
	List<IRacingTrack> getAllTracks() {
		return trackRepository.loadAll(true);
	}

	@ModelAttribute("teams")
	public List<IRacingTeam> getMyTeams() {
		List<IRacingTeam> teams = teamRepository.findByOwnerId(currentUser().getIRacingId());
		teams.addAll(teamRepository.findByAuthorizedDrivers(currentUser().getIRacingId()).stream()
				.filter(s -> !teams.contains(s)).collect(Collectors.toList())
		);
		return teams;
	}
}
