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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

import de.bausdorf.simcacing.tt.schedule.model.Date;
import de.bausdorf.simcacing.tt.schedule.model.Time;
import de.bausdorf.simcacing.tt.schedule.model.TimeOffset;

public class ScheduleTools {

	private ScheduleTools() {
		super();
	}

	public static LocalDate localDateFromDate(Date date) {
		return LocalDate.parse(date.getLocalDate(), DateTimeFormatter.ofPattern(Date.DATE_PATTERN));
	}

	public static LocalTime localTimeFromTime(Time time) {
		try {
			return LocalTime.parse(time.getLocalTime(), DateTimeFormatter.ofPattern(Time.TIME_PATTERN));
		} catch(DateTimeParseException e) {
			return LocalTime.parse(time.getLocalTime(), DateTimeFormatter.ofPattern(Time.SHORT_TIME_PATTERN));
		}
	}

	public static Duration durationFromTime(Time time) {
		LocalTime localTime = ScheduleTools.localTimeFromTime(time);
		return Duration.of(localTime.toSecondOfDay(), ChronoUnit.SECONDS);
	}

	public static LocalDateTime localDateTimeFromDateAndTime(Date date, Time time) {
		return LocalDateTime.of(localDateFromDate(date), localTimeFromTime(time));
	}

	public static ZonedDateTime zonedDateTimeFromDateAndTime(Date date, Time time) {
		return ZonedDateTime.of(localDateFromDate(date), localTimeFromTime(time), ZoneId.of(time.getZoneId()));
	}

	public static ZonedDateTime zonedDateTimeFromDateAndTime(LocalDateTime dateTime, String timezone) {
		return ZonedDateTime.of(dateTime, ZoneId.of(timezone));
	}

	public static Duration durationFromTimeOffset(TimeOffset offset) {
		return Duration.parse(offset.getOffset());
	}

	public static long daysFromTimeOffset(TimeOffset offset) {
		long offsetSeconds =  ScheduleTools.durationFromTimeOffset(offset).getSeconds();
		return offsetSeconds / 86400;
	}

	public static String shortTimeString(Time time) {
		return LocalTime.parse(time.getLocalTime(), DateTimeFormatter.ofPattern(Time.TIME_PATTERN))
				.format(DateTimeFormatter.ofPattern(Time.SHORT_TIME_PATTERN));
	}

	public static String shortTimeString(TimeOffset offset) {
		return LocalTime.MIN.plus(ScheduleTools.durationFromTimeOffset(offset))
				.format(DateTimeFormatter.ofPattern(Time.SHORT_TIME_PATTERN));
	}

	public static String startTimeString(TimeOffset offset) {
		long days = ScheduleTools.daysFromTimeOffset(offset);
		String timeString = ScheduleTools.shortTimeString(offset);
		return (days > 0 ? "+" + days : "") + " " + timeString;
	}

	public static String startTimesString(List<TimeOffset> startTimes) {
		// 8:00\r\n18:00\r\n+1 14:00
		StringBuilder sb = new StringBuilder();
		startTimes.forEach(ScheduleTools::startTimeString);
		return sb.toString();
	}
}
