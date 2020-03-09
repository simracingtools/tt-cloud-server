package de.bausdorf.simcacing.tt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Builder
public class Stint {

	private int no;
	private int laps;
	private String driver;
	@Setter
	private double avgLapTime;
	@Setter
	private double avgFuelPerLap;
	@Setter
	private double lastLapFuel;
	@Setter
	private double lastLaptime;
	@Setter
	private double availableLaps;
	@Setter
	private double maxLaps;
	@Setter
	private double stintDuration;
}