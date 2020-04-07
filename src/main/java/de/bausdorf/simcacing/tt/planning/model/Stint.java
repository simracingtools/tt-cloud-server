package de.bausdorf.simcacing.tt.planning.model;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

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

	private LocalTime startTime;
	private LocalTime endTime;
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
}
