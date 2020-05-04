package de.bausdorf.simcacing.tt.live.model.client;

import de.bausdorf.simcacing.tt.live.clientapi.ClientData;
import lombok.*;

import java.time.Duration;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class LapData implements ClientData {

	private int no;
	private String driver;
	private String driverId;
	private double fuelLevel;
	private Duration lapTime;
	private double trackTemp;
	private LocalTime sessionTime;
	@Setter
	private boolean pitStop;
	@Setter
	private double lastLapFuelUsage;
	@Setter
	private int stint;
	@Setter
	private int stintLap;

}
