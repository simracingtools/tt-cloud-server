package de.bausdorf.simcacing.tt.schedule.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import de.bausdorf.simcacing.tt.schedule.ScheduleTools;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Date {
	public static final String DATE_PATTERN = "yyyy-MM-dd";

	private String localDate;

	public Date(LocalDate date) {
		localDate = date.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
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
}
