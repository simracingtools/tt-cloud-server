package de.bausdorf.simcacing.tt.schedule;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import de.bausdorf.simcacing.tt.schedule.model.Date;
import de.bausdorf.simcacing.tt.schedule.model.RaceEvent;
import de.bausdorf.simcacing.tt.schedule.model.RaceSeries;
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
		return LocalTime.parse(time.getLocalTime(), DateTimeFormatter.ofPattern(Time.TIME_PATTERN));
	}

	public static ZonedDateTime zonedDateTimeFromDateAndTime(Date date, Time time) {
		return ZonedDateTime.of(localDateFromDate(date), localTimeFromTime(time), ZoneId.of(time.getZoneId()));
	}

	public static Duration durationFromTimeOffset(TimeOffset offset) {
		return Duration.parse(offset.getOffset());
	}

	public static String shortTimeString(Time time) {
		return LocalTime.parse(time.getLocalTime(), DateTimeFormatter.ofPattern(Time.TIME_PATTERN))
				.format(DateTimeFormatter.ofPattern(Time.SHORT_TIME_PATTERN));
	}

	public static List<RaceEvent> generateSeriesEvents(RaceSeries series, int eventCount) {
		List<RaceEvent> events = new ArrayList<>();
		for( int i = 0; i < eventCount; i++) {
			final Date startDate = series.getStartDate()
					.plus(ScheduleTools.durationFromTimeOffset(series.getEventInterval()).multipliedBy(i));
			final String raceName = "Race No " + (i + 1);
			final String eventId = series.getName() + "#" + series.getSeason() + "#Race" + (i+1) + "#";
			series.getStartTimes().forEach(s -> events.add(RaceEvent.builder()
						.eventId(eventId + shortTimeString(Time.of(s)))
						.name(raceName + " " + shortTimeString(Time.of(s)))
						.carIds(series.getCars())
						.series(series.getName())
						.season(series.getSeason())
						.raceSessionOffset(series.getRaceSessionOffset())
						.sessionDate(startDate.plus(s))
						.sessionTime(Time.of(s))
						.build()));
		}
		return events;
	}
}
