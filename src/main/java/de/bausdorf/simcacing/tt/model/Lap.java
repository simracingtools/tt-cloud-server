package de.bausdorf.simcacing.tt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Lap {

	private int no;
	private String driver;
	private int stint;
	private int stintLap;
	private double fuelLevel;
	private double lastLapFuelUsage;
	private double lapTime;
	private double trackTemp;
	private double sessionTime;

}
