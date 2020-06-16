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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import de.bausdorf.simcacing.tt.live.impl.SessionController;
import de.bausdorf.simcacing.tt.live.model.client.Pitstop;
import de.bausdorf.simcacing.tt.live.model.client.RunData;
import de.bausdorf.simcacing.tt.live.model.client.ServiceFlagType;
import de.bausdorf.simcacing.tt.live.model.client.Stint;
import de.bausdorf.simcacing.tt.planning.PlanningTools;
import de.bausdorf.simcacing.tt.planning.model.PitStopServiceType;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.util.TimeTools;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@Builder
@ToString
public class PitstopDataView {
	private String stintNo;
	private String raceTimeLeft;
	private String lapNo;
	private String driver;
	private List<String> allDrivers;
	private String timePitted;
	private String pitStopDuration;
	private boolean refuel;
	private boolean changeTyres;
	private boolean clearWindshield;
	private String serviceDuration;
	private String refuelAmount;
	private String repairTime;
	private boolean currentStint;
	private boolean lastStint;
	private boolean plannedStint;

	public static List<PitstopDataView> getPitstopDataView(SessionController controller) {
		List<PitstopDataView> pitstopDataViews = new ArrayList<>();
		if (controller != null) {
			RaceClock clock = new PitstopDataView.RaceClock();
			ZonedDateTime from = viewForPittedStints(controller, clock, pitstopDataViews);
			if (from == null) {
				from = controller.getSessionRegistered();
			}
			if (controller.getRacePlan() != null) {
				viewForPlannedStints(controller, from, clock, pitstopDataViews);
			}
		}
		return pitstopDataViews;
	}

	private static void viewForPlannedStints(SessionController controller, ZonedDateTime from, RaceClock clock, List<PitstopDataView> pitstopDataViews) {
		RunData runData = controller.getRunData();
		LocalDateTime sessionToD = controller.getRacePlan().getPlanParameters().getTodStartTime();
		if (runData != null) {
			sessionToD = LocalDateTime.of(sessionToD.toLocalDate(), runData.getSessionToD());
		}
		List<de.bausdorf.simcacing.tt.planning.model.Stint> stints =
				controller.getRacePlan().calculateLiveStints(from, sessionToD, from.plus(controller.getRemainingSessionTime()));
		controller.getRacePlan().setCurrentRacePlan(stints);
		for (de.bausdorf.simcacing.tt.planning.model.Stint stint : stints) {
			clock.lapCount += stint.getLaps();
			clock.stintNo++;
			clock.accumulatedStintDuration = clock.accumulatedStintDuration.plus(stint.getStintDuration(true));
			Duration raceTimeLeft = controller.getRacePlan().getPlanParameters().getRaceDuration()
					.minus(clock.accumulatedStintDuration);
			List<String> driverList = controller.getRacePlan().getPlanParameters().getAllDrivers().stream()
					.map(IRacingDriver::getName)
					.collect(Collectors.toList());
			driverList.add("unassigned");

			String pitstopDuration = TimeTools.shortDurationString(
					controller.getRacePlan().getPlanParameters().getAvgPitLaneTime().plus(
						PlanningTools.calculateServiceDuration(stint.getService(), stint.getRefuelAmount()))
					);
			String serviceDuration = TimeTools.shortDurationString(
					PlanningTools.calculateServiceDuration(stint.getService(), stint.getRefuelAmount()));

			PitstopDataView view = PitstopDataView.builder()
					.driver(stint.getDriverName())
					.lapNo(stint.isLastStint() ? "(" + stint.getLaps() + ")" : Integer.toString(clock.lapCount))
					.timePitted(stint.getEndTime().format(DateTimeFormatter.ofPattern(TimeTools.HH_MM_SS_XXX)))
					.stintNo(Integer.toUnsignedString(clock.stintNo))
					.allDrivers(driverList)
					.raceTimeLeft(TimeTools.shortDurationString(raceTimeLeft))
					.pitStopDuration(pitstopDuration)
					.repairTime("")
					.refuel(stint.getService().contains(PitStopServiceType.FUEL))
					.changeTyres(stint.getService().contains(PitStopServiceType.TYRES))
					.clearWindshield(stint.getService().contains(PitStopServiceType.WS))
					.serviceDuration(serviceDuration)
					.refuelAmount(String.format(Locale.US, "%.3f", stint.getRefuelAmount()))
					.plannedStint(true)
					.currentStint(stints.indexOf(stint) == 0)
					.lastStint(stint.isLastStint())
					.build();

			pitstopDataViews.add(view);
		}
	}

	private static ZonedDateTime viewForPittedStints(SessionController controller, RaceClock clock, List<PitstopDataView> pitstopDataViews) {
		ZonedDateTime endTime = controller.getSessionRegistered();
		for (Stint stint : controller.getStints().tailMap(0).values()) {
			Pitstop pitstop = controller.getPitStops().get(stint.getNo());
			if (pitstop == null || !pitstop.isComplete()) {
				// Stint not complete
				continue;
			}
			clock.lapCount += stint.getLaps();
			clock.stintNo++;
			Duration stintDuration = stint.getCurrentStintDuration() != null ? stint.getCurrentStintDuration() : Duration.ZERO;
			clock.accumulatedStintDuration = clock.accumulatedStintDuration.plus(stintDuration);
			Duration raceDuration = controller.getRacePlan() != null
					? controller.getRacePlan().getPlanParameters().getRaceDuration()
					: Duration.ofSeconds(controller.getSessionData().getSessionDuration().orElse(LocalTime.MIN).toSecondOfDay());
			Duration raceTimeLeft = raceDuration.minus(clock.accumulatedStintDuration);

			PitstopDataView view = PitstopDataView.builder()
					.allDrivers(Collections.singletonList(stint.getDriver()))
					.driver(stint.getDriver())
					.lapNo(Integer.toUnsignedString(clock.lapCount))
					.raceTimeLeft(raceTimeLeft.isNegative() ? "" : TimeTools.shortDurationString(raceTimeLeft))
					.stintNo(Integer.toUnsignedString(stint.getNo()))
					.timePitted(ZonedDateTime.of(LocalDate.now(), pitstop.getEnterPits(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(TimeTools.HH_MM_SS_XXX)))
					.serviceDuration(TimeTools.shortDurationString(pitstop.getPitstopServiceTime()))
					.refuel(pitstop.getServiceFlags() != null && pitstop.getServiceFlags().contains(ServiceFlagType.FUEL))
					.clearWindshield(pitstop.getServiceFlags() != null && pitstop.getServiceFlags().contains(ServiceFlagType.WS))
					.changeTyres(pitstop.getServiceFlags() != null && pitstop.getServiceFlags().contains(ServiceFlagType.TYRES))
					.repairTime(TimeTools.shortDurationString(pitstop.getRepairAndTowingTime()))
					.pitStopDuration(TimeTools.shortDurationString(pitstop.getPitstopDuration()))
					.refuelAmount(fuelString(pitstop.getRefuelAmount()))
					.plannedStint(false)
					.lastStint(false)
					.currentStint(false)
					.build();

			pitstopDataViews.add(view);
			endTime = stint.getEndTime();
		}
		return endTime;
	}

	private static String fuelString(double fuelAmount) {
		return String.format("%.3f", fuelAmount).replace(",", ".");
	}

	@Data
	private static class RaceClock {
		private int stintNo = 0;
		private int lapCount = 0;
		private Duration accumulatedStintDuration = Duration.ZERO;
	}
}
