package de.bausdorf.simcacing.tt.live.model.live;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LapDataView {
	private String lapNo;
	private String stintLap;
	private String stintNo;
	private String lapsRemaining;
	private String stintsRemaining;
	private String lastLapTime;
	private String stintAvgLapTime;
	private String stintClock;
	private String stintRemainingTime;
	private String stintAvgTimeDelta;
	private String lastLapFuel;
	private String stintAvgFuelPerLap;
	private String stintAvgFuelDelta;
	private String trackTemp;
}
