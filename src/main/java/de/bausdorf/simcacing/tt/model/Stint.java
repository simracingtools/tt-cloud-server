package de.bausdorf.simcacing.tt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@AllArgsConstructor
@Builder
public class Stint {

	private int no;
	private int laps;
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

	public Duration addStintDuration(Duration toAdd) {
		currentStintDuration = currentStintDuration.plus(toAdd);
		return currentStintDuration;
	}
}