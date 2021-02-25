package de.bausdorf.simcacing.tt.schedule;

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
