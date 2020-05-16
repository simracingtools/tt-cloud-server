package de.bausdorf.simcacing.tt.live.model.client;

import lombok.*;

import java.time.Duration;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class Stint {

	private final int no;
	private int laps;
	@Setter
	private String driver;
	@Setter
	private Duration avgLapTime;
	@Setter
	private double avgFuelPerLap;
	@Setter
	private double lastLapFuel;
	@Setter
	private Duration lastLaptime;
	@Setter
	private double availableLaps;
	@Setter
	private double maxLaps;
	@Setter
	private Duration currentStintDuration;
	@Setter
	private Duration expectedStintDuration;
	@Setter
	private LocalTime todStart;
	@Setter
	private LocalTime todEnd;
	@Setter
	private double avgTrackTemp;

	public Duration addStintDuration(Duration toAdd) {
		if( currentStintDuration == null ) {
			currentStintDuration = Duration.ZERO;
		}
		currentStintDuration = currentStintDuration.plus(toAdd);
		return currentStintDuration;
	}

	public int increaseLapCount() {
		return ++laps;
	}
}