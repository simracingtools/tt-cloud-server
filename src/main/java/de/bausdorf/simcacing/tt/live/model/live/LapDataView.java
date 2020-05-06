package de.bausdorf.simcacing.tt.live.model.live;

import java.time.Duration;
import java.util.Locale;
import java.util.Optional;

import de.bausdorf.simcacing.tt.live.impl.SessionController;
import de.bausdorf.simcacing.tt.live.model.client.LapData;
import de.bausdorf.simcacing.tt.live.model.client.Stint;
import de.bausdorf.simcacing.tt.planning.model.Estimation;
import de.bausdorf.simcacing.tt.util.TimeTools;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LapDataView {
	public static final String TABLE_SUCCESS = "table-success";
	public static final String TABLE_DANGER = "table-danger";

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
	private String stintAvgTimeDeltaCssClass;
	private String lastLapFuel;
	private String stintAvgFuelPerLap;
	private String stintAvgFuelDelta;
	private String stintAvgFuelDeltaCssClass;
	private String estimatedFuelPerLap;
	private String estimatedFuelDelta;
	private String estimatedFuelDeltaCssClass;
	private String estimatedFuelLaps;
	private String estimatedLapTime;
	private String estimatedLapTimeDelta;
	private String estimatedLapTimeDeltaCssClass;
	private String estimatedStintTime;
	private String requiredFuelPerLapOneMore;
	private String trackTemp;
	private String driverBestLap;
	private String maxCarFuel;

	public static LapDataView getLapDataView(LapData clientData, SessionController controller) {
		if (clientData == null) {
			return null;
		}
		Optional<Stint> lastStint = controller.getLastStint();
		Duration stintAvgLapTime = Duration.ZERO;
		int stintLap = clientData.getNo();
		double stintAvgFuel = 0.0D;
		if (lastStint.isPresent()) {
			stintAvgFuel = lastStint.get().getAvgFuelPerLap();
			stintAvgLapTime = lastStint.get().getAvgLapTime();
			stintLap = lastStint.get().getLaps();
		}

		double avgFuelDelta = clientData.getLastLapFuelUsage() - stintAvgFuel;
		String avgTimeDelta = TimeTools.longDurationDeltaString(clientData.getLapTime(), stintAvgLapTime);

		Estimation estimation = controller.getCurrentDriverEstimation();
		double estimatedFuelDelta = estimation != null ? clientData.getLastLapFuelUsage() - estimation.getAvgFuelPerLap() : 0.0D;
		double estimatedFuelLaps = estimation != null ? controller.getSessionData().getMaxCarFuel() / estimation.getAvgFuelPerLap() : 0.0D;
		double oneLapMore = Math.ceil(estimatedFuelLaps);
		Duration estimatedLapTime = estimation != null ? estimation.getAvgLapTime() : Duration.ZERO;
		String estimatedLapTimeDelta = TimeTools.longDurationDeltaString(clientData.getLapTime(), estimatedLapTime);
		String estimatedStintTime = TimeTools.shortDurationString(estimatedLapTime
				.multipliedBy((int)Math.floor(controller.getAvailableLapsForFuelLevel()))
		);

		return LapDataView.builder()
				.lapNo(Integer.toUnsignedString(clientData.getNo()))
				.lapsRemaining(Integer.toUnsignedString(controller.getRemainingLapCount()))
				.lastLapFuel(fuelString(clientData.getLastLapFuelUsage()))
				.lastLapTime(TimeTools.longDurationString(clientData.getLapTime()))
				.stintNo(lastStint.map(stint -> Integer.toUnsignedString(stint.getNo())).orElse("-"))
				.stintAvgLapTime(TimeTools.longDurationString(stintAvgLapTime))
				.stintAvgFuelPerLap(fuelString(stintAvgFuel))
				.stintAvgFuelDelta(fuelString(avgFuelDelta))
				.stintAvgFuelDeltaCssClass(avgFuelDelta < 0.0 ? TABLE_SUCCESS : TABLE_DANGER)
				.stintAvgTimeDelta(avgTimeDelta)
				.stintAvgTimeDeltaCssClass(avgTimeDelta.startsWith("-") ? TABLE_SUCCESS : TABLE_DANGER)
				.stintClock(TimeTools.shortDurationString(controller.getCurrentStintTime()))
				.stintRemainingTime(TimeTools.shortDurationString(controller.getRemainingStintTime()))
				.stintsRemaining(Integer.toUnsignedString(controller.getRemainingStintCount()))
				.stintLap(Integer.toUnsignedString(stintLap))
				.trackTemp(String.format("%.1f",clientData.getTrackTemp()) + "°C")
				.driverBestLap(TimeTools.longDurationString(controller.getCurrentDriverBestLap()))
				.estimatedFuelPerLap(estimation != null ? fuelString(estimation.getAvgFuelPerLap()) : "-.---")
				.estimatedFuelDelta(fuelString(estimatedFuelDelta))
				.estimatedFuelDeltaCssClass(estimatedFuelDelta < 0.0 ?  TABLE_SUCCESS : TABLE_DANGER)
				.estimatedFuelLaps(String.format("%.2f", oneLapMore))
				.estimatedLapTime(TimeTools.longDurationString(estimatedLapTime))
				.estimatedLapTimeDelta(estimatedLapTimeDelta)
				.estimatedLapTimeDeltaCssClass(estimatedLapTimeDelta.startsWith("-") ? TABLE_SUCCESS : TABLE_DANGER)
				.estimatedStintTime(estimatedStintTime)
				.requiredFuelPerLapOneMore(fuelString(controller.getSessionData().getMaxCarFuel() / oneLapMore))
				.maxCarFuel(String.format(Locale.US,"%.1f", controller.getSessionData().getMaxCarFuel()))
				.build();
	}

	private static String fuelString(double fuelAmount) {
		return String.format("%.3f", fuelAmount).replace(",", ".");
	}
}
