package de.bausdorf.simcacing.tt.live.model.live;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 bausdorf engineering
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
	private String fastestLap;
	private String slowestLap;
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
				.stintAvgLapTime(TimeTools.longDurationString(stintAvgLapTime != null ? stintAvgLapTime : Duration.ZERO))
				.stintAvgFuelPerLap(fuelString(stintAvgFuel))
				.stintAvgFuelDelta(fuelString(avgFuelDelta))
				.stintAvgFuelDeltaCssClass(avgFuelDelta < 0.0 ? TABLE_SUCCESS : TABLE_DANGER)
				.stintAvgTimeDelta(avgTimeDelta)
				.stintAvgTimeDeltaCssClass(avgTimeDelta.startsWith("-") ? TABLE_SUCCESS : TABLE_DANGER)
				.stintClock(TimeTools.shortDurationString(controller.getCurrentStintTime()))
				.stintRemainingTime(TimeTools.shortDurationString(controller.getRemainingStintTime()))
				.stintsRemaining(Integer.toUnsignedString(controller.getRemainingStintCount()))
				.stintLap(Integer.toUnsignedString(stintLap))
				.trackTemp(String.format(Locale.US,"%.1f",clientData.getTrackTemp()))
				.driverBestLap(TimeTools.longDurationString(controller.getCurrentDriverBestLap()))
				.estimatedFuelPerLap(estimation != null ? fuelString(estimation.getAvgFuelPerLap()) : "-.---")
				.estimatedFuelDelta(fuelString(estimatedFuelDelta))
				.estimatedFuelDeltaCssClass(estimatedFuelDelta < 0.0 ?  TABLE_SUCCESS : TABLE_DANGER)
				.estimatedFuelLaps(String.format(Locale.US,"%.2f", oneLapMore))
				.estimatedLapTime(TimeTools.longDurationString(estimatedLapTime))
				.estimatedLapTimeDelta(estimatedLapTimeDelta)
				.estimatedLapTimeDeltaCssClass(estimatedLapTimeDelta.startsWith("-") ? TABLE_SUCCESS : TABLE_DANGER)
				.estimatedStintTime(estimatedStintTime)
				.requiredFuelPerLapOneMore(fuelString(controller.getSessionData().getMaxCarFuel() / oneLapMore))
				.maxCarFuel(String.format(Locale.US,"%.1f", controller.getMaxCarFuel()))
				.fastestLap(TimeTools.longDurationString(controller.getFastestLap()))
				.slowestLap(TimeTools.longDurationString(controller.getSlowestLap()))
				.build();
	}

	private static String fuelString(double fuelAmount) {
		return String.format("%.3f", fuelAmount).replace(",", ".");
	}
}
