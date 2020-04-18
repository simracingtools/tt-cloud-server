package de.bausdorf.simcacing.tt.planning.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import de.bausdorf.simcacing.tt.util.TimeTools;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@Builder
@ToString
public class Stint {

	private String driverName;

	private LocalDateTime todStartTime;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private double refuelAmount;
	private int laps;
	private Optional<PitStop> pitStop;

	public Duration getStintDuration(boolean includePitStopTimes) {
		if( startTime != null && endTime != null ) {
			if( includePitStopTimes && pitStop.isPresent() ) {
				return Duration.between(startTime, endTime).minus(pitStop.get().getOverallDuration());
			}
			return Duration.between(startTime, endTime);
		}
		return Duration.ZERO;
	}

	public String getStintDurationString(boolean includePitStopTimes) {
		return TimeTools.shortDurationString(getStintDuration(includePitStopTimes));
	}

	public String getStartTimeString() {
		return getStartTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	}

	public String getTodStartTimeString() {
		return getTodStartTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	}

	public String getEndTimeString() {
		return getEndTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	}
}
