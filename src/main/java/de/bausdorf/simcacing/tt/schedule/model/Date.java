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

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import de.bausdorf.simcacing.tt.schedule.ScheduleTools;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class Date implements Comparable<Date> {
	public static final String DATE_PATTERN = "yyyy-MM-dd";

	private String localDate;

	public Date() {
		this.localDate= "1970-01-01";
	}

	public Date(LocalDate date) {
		this.localDate = date.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
	}

	public Date plus(TimeOffset offset) {
		long offsetSeconds =  ScheduleTools.durationFromTimeOffset(offset).getSeconds();
		long days = offsetSeconds / 86400;
		return new Date(ScheduleTools.localDateFromDate(this).plusDays(days));
	}

	public Date plus(Duration duration) {
		long offsetSeconds =  duration.getSeconds();
		long days = offsetSeconds / 86400;
		return new Date(ScheduleTools.localDateFromDate(this).plusDays(days));
	}

	@Override
	public int compareTo(Date other) {
		LocalDate thisLocalDate = LocalDate.parse(localDate, DateTimeFormatter.ofPattern(DATE_PATTERN));
		LocalDate otherLocalDate = ScheduleTools.localDateFromDate(other);

		if(thisLocalDate.isBefore(otherLocalDate)) {
			return -1;
		} else if(thisLocalDate.isAfter(otherLocalDate)) {
			return 1;
		} else {
			return 0;
		}
 	}
}
