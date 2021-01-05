package de.bausdorf.simcacing.tt.schedule.model;

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
		localTime = dateTime.format(DateTimeFormatter.ofPattern(TIME_PATTERN));
		zoneId = null;
	}

	public Time(@Nonnull ZonedDateTime dateTime) {
		localTime = dateTime.format(DateTimeFormatter.ofPattern(TIME_PATTERN));
		zoneId = dateTime.getZone().getId();
	}

	public static Time of(TimeOffset offset) {
		return new Time(LocalTime.MIN.plus(ScheduleTools.durationFromTimeOffset(offset)));
	}
}
