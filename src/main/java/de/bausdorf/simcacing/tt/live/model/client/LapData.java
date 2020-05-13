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

	private final int no;
	private final String driver;
	private final String driverId;
	private final double fuelLevel;
	private final Duration lapTime;
	private final double trackTemp;
	private final LocalTime sessionTime;
	@Setter
	private boolean pitStop;
	@Setter
	private boolean unclean;
	@Setter
	private double lastLapFuelUsage;
	@Setter
	private int stint;
	@Setter
	private int stintLap;

}
