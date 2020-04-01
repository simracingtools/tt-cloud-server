package de.bausdorf.simcacing.tt.model;

import de.bausdorf.simcacing.tt.clientapi.ClientData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@Builder
public class LapData implements ClientData {

	private int no;
	private String driver;
	private double fuelLevel;
	private Duration lapTime;
	private double trackTemp;
	private LocalTime sessionTime;
	@Setter
	private double lastLapFuelUsage;
	@Setter
	private int stint;
	@Setter
	private int stintLap;

}
