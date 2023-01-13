package de.bausdorf.simcacing.tt.web;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 - 2021 bausdorf engineering
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

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.bausdorf.simcacing.tt.iracing.IRacingClient;
import de.bausdorf.simcacing.tt.web.model.schedule.ImportSelectView;
import de.bausdorf.simracing.irdataapi.model.SeasonDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	private final TrackRepository trackRepository;
	private final CarRepository carRepository;
	private final TeamRepository teamRepository;
	private final RaceEventRepository eventRepository;
	private final IRacingClient dataClient;

	private List<SeasonDto> seasonDtos = new ArrayList<>();
	private OffsetDateTime lastUpdated;

	public ScheduleController(@Autowired TrackRepository trackRepository,
							  @Autowired CarRepository carRepository,
							  @Autowired RaceEventRepository eventRepository,
							  @Autowired TeamRepository teamRepository,
							  @Autowired IRacingClient dataClient) {
		this.carRepository = carRepository;
		this.eventRepository = eventRepository;
		this.trackRepository = trackRepository;
		this.teamRepository = teamRepository;
		this.dataClient = dataClient;
	}

	@GetMapping("/schedule")
	@Secured({ "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
	public String viewNewRacePlan(@RequestParam Optional<String> newEventTemplateId, Optional<String> initialDate, Model model) {

		RaceEventView eventView = new RaceEventView();
		eventView.setTimezone(currentUserProfile().getTimezone());
		if(newEventTemplateId.isPresent()) {
			RaceEvent eventTemplate = eventRepository.findById(newEventTemplateId.get()).orElse(null);
			if(eventTemplate != null) {
				eventView = RaceEventView.fromRaceEvent(eventTemplate);
				eventView.setEventId(null);
				eventView.setSessionDateTime(null);
				eventView.setSaveAndNew(true);
				eventView.setTimezone(currentUserProfile().getTimezone());
			}
		}
		model.addAttribute("newEvent", eventView);
		model.addAttribute("calendarInitialDate", initialDate.orElse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
		model.addAttribute("importSelectView", importSelectView());
		model.addAttribute(RacePlanController.RACEPLAN, new PlanParametersView());

		return EVENTSCHEDULE_VIEW;
	}

	@PostMapping("/saveEvent")
	@Secured({ "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
	public String saveEvent(@ModelAttribute RaceEventView raceEventView, Model model) {
		try {
			RaceEvent raceEvent = raceEventView.toEvent();
			eventRepository.save(raceEvent);
			raceEventView.setEventId(raceEvent.getEventId().toString());
		} catch(Exception e) {
			log.error(e.getMessage(), e);
			addError(e.getMessage(), model);
		}

		return REDIRECT_SCHEDULE
				+ (raceEventView.isSaveAndNew() ? "?newEventTemplateId=" + raceEventView.getEventId() : "")
				+ (raceEventView.isSaveAndNew() ? "&" : "?")
				+ "initialDate=" + raceEventView.getSessionDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}

	@GetMapping("/deleteEvent")
	@Secured({ "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
	public String deleteEvent(@RequestParam String eventId, Model model) {
		try {
			eventRepository.deleteById(eventId);
		} catch(Exception e) {
			log.error(e.getMessage(), e);
			addError(e.getMessage(), model);
		}
		return REDIRECT_SCHEDULE;
	}

	@PostMapping("/import-events")
	public String importEvents(@ModelAttribute ImportSelectView selectView, Model model) {
		seasonDtos.forEach(seasonDto -> {
			if (selectView.getSelectedSeries().contains(seasonDto.getSeasonId())) {
				List<RaceEvent> raceEvents = dataClient.getRaceEventsForSeason(seasonDto);
				eventRepository.saveAll(raceEvents);
			}
		});

		return REDIRECT_SCHEDULE;
	}

	@ModelAttribute("allCars")
	List<IRacingCar> getAllCars() {
		return carRepository.loadAll();
	}

	@ModelAttribute("allTracks")
	List<IRacingTrack> getAllTracks() {
		return trackRepository.loadAll();
	}

	@ModelAttribute("teams")
	public List<IRacingTeam> getMyTeams() { return getMyTeams(teamRepository); }

	private ImportSelectView importSelectView() {
		if (lastUpdated == null || lastUpdated.isBefore(OffsetDateTime.now().minusHours(12)) || seasonDtos.isEmpty()) {
			seasonDtos = dataClient.getTeamSeasons();
			lastUpdated = OffsetDateTime.now();
		}
		List<ImportSelectView.SelectView> series = new ArrayList<>();
		seasonDtos.forEach(dto -> series.add(new ImportSelectView.SelectView(dto.getSeasonName(), dto.getSeasonId())));
		return ImportSelectView.builder()
				.title(seasonDtos.get(0).getSeasonShortName())
				.series(series)
				.selectedSeries(new ArrayList<>())
				.build();
	}
}
