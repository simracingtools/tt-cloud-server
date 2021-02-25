package de.bausdorf.simcacing.tt.web.model.schedule;

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
