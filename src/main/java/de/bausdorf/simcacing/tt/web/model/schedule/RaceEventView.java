package de.bausdorf.simcacing.tt.web.model.schedule;

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

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.cloud.Timestamp;

import de.bausdorf.simcacing.tt.schedule.ScheduleTools;
import de.bausdorf.simcacing.tt.schedule.model.Date;
import de.bausdorf.simcacing.tt.schedule.model.RaceEvent;
import de.bausdorf.simcacing.tt.schedule.model.Time;
import de.bausdorf.simcacing.tt.schedule.model.TimeOffset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RaceEventView {

	private String eventId;
	private String name;
	private String series;
	private String season;
	private String eventTrackId;
	private List<String> carsIds = new ArrayList<>();
	private LocalDateTime simDateTime;
	private String timezone;
	private LocalDateTime sessionDateTime;
	private String duration;
	private TimeOffset raceSessionOffset;
	private boolean saveAndNew;

	public static RaceEventView fromRaceEvent(@Nonnull RaceEvent event) {
		return RaceEventView.builder()
				.eventId(event.getEventId())
				.name(event.getName())
				.series(event.getSeries())
				.season(event.getSeason())
				.eventTrackId(event.getTrackId())
				.carsIds(event.getCarIds())
				.simDateTime(ScheduleTools.localDateTimeFromDateAndTime(event.getSimDate(), event.getSimTime()))
				.sessionDateTime(ScheduleTools.localDateTimeFromDateAndTime(event.getSessionDate(), event.getSessionTime()))
				.timezone(event.getSessionTime().getZoneId())
				.duration(event.getRaceDuration() == null ? null : event.getRaceDuration().getLocalTime())
				.raceSessionOffset(event.getRaceSessionOffset())
				.saveAndNew(false)
				.build();
	}

	public RaceEvent toEvent() {
		ZonedDateTime sessionTime = ScheduleTools.zonedDateTimeFromDateAndTime(sessionDateTime, timezone);
		return RaceEvent.builder()
				.eventId(eventId)
				.name(name)
				.series(series)
				.season(season)
				.trackId(eventTrackId)
				.carIds(carsIds)
				.simDate(new Date(getSimDateTime().toLocalDate()))
				.simTime(new Time(simDateTime.toLocalTime()))
				.sessionDate(new Date(sessionDateTime.toLocalDate()))
				.sessionTime(new Time(sessionDateTime.toLocalTime(), timezone))
				.sessionTimestamp(Timestamp.ofTimeMicroseconds(sessionTime.toInstant().toEpochMilli() * 1000))
				.raceSessionOffset(raceSessionOffset)
				.raceDuration(new Time(duration, null))
				.build();
	}
}
