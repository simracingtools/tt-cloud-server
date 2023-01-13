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

import java.time.*;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import de.bausdorf.simcacing.tt.schedule.model.RaceEvent;
import de.bausdorf.simcacing.tt.schedule.model.TimeOffset;
import de.bausdorf.simcacing.tt.util.TimeTools;
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
				.eventId(event.getEventId().toString())
				.name(event.getName())
				.series(event.getSeries())
				.season(event.getSeason())
				.eventTrackId(event.getTrackId())
				.carsIds(event.getCarIds())
				.simDateTime(event.getSimDateTime())
				.sessionDateTime(event.getSessionDateTime().toLocalDateTime())
				.timezone(ZoneId.ofOffset("UTC", event.getSessionDateTime().getOffset()).toString())
				.duration(event.getRaceDuration() == null ? null : TimeTools.raceDurationString(event.getRaceDuration()))
				.raceSessionOffset(new TimeOffset(event.getRaceSessionOffset()))
				.saveAndNew(false)
				.build();
	}

	public RaceEvent toEvent() {
		return RaceEvent.builder()
				.eventId(Long.parseLong(eventId))
				.name(name)
				.series(series)
				.season(season)
				.trackId(eventTrackId)
				.carIds(carsIds)
				.simDateTime(simDateTime)
				.sessionDateTime(OffsetDateTime.of(sessionDateTime, ZoneOffset.of(timezone)))
				.raceSessionOffset(raceSessionOffset.getOffset())
				.raceDuration(Duration.parse(duration))
				.build();
	}
}
