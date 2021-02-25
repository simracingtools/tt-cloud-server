package de.bausdorf.simcacing.tt.schedule.model;

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

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nonnull;

import de.bausdorf.simcacing.tt.schedule.ScheduleTools;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Time {
	public static final String TIME_PATTERN = "HH:mm:ss";
	public static final String SHORT_TIME_PATTERN = "HH:mm";

	private String localTime;
	private String zoneId;

	public Time(@Nonnull  LocalTime dateTime) {
		this.localTime = dateTime.format(DateTimeFormatter.ofPattern(TIME_PATTERN));
		this.zoneId = null;
	}

	public Time(@Nonnull  LocalTime dateTime, String zoneId) {
		this.localTime = dateTime.format(DateTimeFormatter.ofPattern(TIME_PATTERN));
		this.zoneId = zoneId;
	}

	public Time(@Nonnull ZonedDateTime dateTime) {
		this.localTime = dateTime.format(DateTimeFormatter.ofPattern(TIME_PATTERN));
		this.zoneId = dateTime.getZone().getId();
	}

	public static Time of(TimeOffset offset) {
		return new Time(LocalTime.MIN.plus(ScheduleTools.durationFromTimeOffset(offset)));
	}
}
