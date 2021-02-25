package de.bausdorf.simcacing.tt.schedule.model;

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
