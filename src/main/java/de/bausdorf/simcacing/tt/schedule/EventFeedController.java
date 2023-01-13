package de.bausdorf.simcacing.tt.schedule;

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

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.bausdorf.simcacing.tt.planning.RacePlanRepository;
import de.bausdorf.simcacing.tt.planning.model.RacePlanParameters;
import de.bausdorf.simcacing.tt.schedule.model.RaceEvent;
import de.bausdorf.simcacing.tt.stock.TeamRepository;
import de.bausdorf.simcacing.tt.stock.TrackRepository;
import de.bausdorf.simcacing.tt.stock.model.IRacingTeam;
import de.bausdorf.simcacing.tt.stock.model.IRacingTrack;
import de.bausdorf.simcacing.tt.util.TimeTools;
import de.bausdorf.simcacing.tt.web.model.planning.PlanDescriptionView;
import de.bausdorf.simcacing.tt.web.model.schedule.CalendarEvent;
import de.bausdorf.simcacing.tt.web.model.schedule.RaceEventView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class EventFeedController {

	public static final String BR = "<br/>";
	public static final String NEW_TABLELINE = "</tr><tr>";
	public static final String END_CELL = "</td>";
	public static final String START_CELL = "<td>";

	private final RaceEventRepository eventRepository;
	private final TrackRepository trackRepository;
	private final RacePlanRepository planRepository;
	private final TeamRepository teamRepository;

	public EventFeedController(@Autowired RaceEventRepository raceEventRepository,
			@Autowired TrackRepository trackRepository,
			@Autowired RacePlanRepository planRepository,
			@Autowired TeamRepository teamRepository) {
		this.eventRepository = raceEventRepository;
		this.trackRepository = trackRepository;
		this.planRepository = planRepository;
		this.teamRepository = teamRepository;
	}

	@GetMapping("/events")
	public List<CalendarEvent> events(@RequestParam Optional<String> start,
			@RequestParam Optional<String> end, @RequestParam Optional<String> series) {

		List<RaceEvent> events;
		if(series.isPresent()) {
			events = eventRepository.findAllBySeriesAndSessionDateTimeAfter(series.get(), OffsetDateTime.now());
		} else {
			events = eventRepository.findAllBySessionDateTimeAfter(OffsetDateTime.now());
		}

		List<CalendarEvent> calendarEvents = new ArrayList<>();
		events.forEach(event -> {
			ZonedDateTime startTime = ScheduleTools.zonedDateTimeFromDateAndTime(event.getSessionDateTime().toLocalDateTime(), event.getSessionDateTime().getOffset().toString());
			ZonedDateTime endTime = startTime.plus(event.getRaceDuration());
			Optional<IRacingTrack> track = trackRepository.findById(event.getTrackId());

			String eventDescription = "<b>" + event.getName() + "</b>" + BR +
					event.getSeason() + " - " + event.getSeries() + BR +
					"Session start: " + event.getSessionDateTime().toLocalTime().toString() + BR +
					"Track: " + (track.isPresent() ? track.get().getName() : "?") + BR +
					"Race duration: " + event.getRaceDuration().toString();

			calendarEvents.add(
					CalendarEvent.builder()
							.id(event.getEventId().toString())
							.title(event.getName())
							.start(startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
							.end(endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
							.description(eventDescription)
							.build());
		});
		return calendarEvents;
	}

	@GetMapping("/event")
	public RaceEventView getEventView(@RequestParam Long eventId) {
		RaceEvent raceEvent = eventRepository.findByEventId(eventId).orElse(null);
		return raceEvent != null ? RaceEventView.fromRaceEvent(raceEvent) : new RaceEventView();
	}

	@GetMapping("/planList")
	public List<PlanDescriptionView> getRacePlans(@RequestParam List<String> teamIds,
			@RequestParam String trackId, @RequestParam String duration) {
		List<RacePlanParameters> planParameters = planRepository.findByTeamIds(teamIds);
		return planParameters.stream()
				.filter(s -> s.getTrackId().equalsIgnoreCase(trackId))
				.filter(s -> s.getRaceDuration().equals(TimeTools.durationFromString(duration)))
				.map(s -> {
					Optional<IRacingTeam> team = teamRepository.findById(s.getTeamId());
					return PlanDescriptionView.builder()
							.id(s.getId())
							.name(s.getName())
							.team(team.isPresent() ? team.get().getName() : "")
							.build();
				})
				.collect(Collectors.toList());
	}
}
