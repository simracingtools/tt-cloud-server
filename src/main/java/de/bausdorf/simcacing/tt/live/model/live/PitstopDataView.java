package de.bausdorf.simcacing.tt.live.model.live;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.bausdorf.simcacing.tt.live.impl.SessionController;
import de.bausdorf.simcacing.tt.live.model.client.Pitstop;
import de.bausdorf.simcacing.tt.live.model.client.RunData;
import de.bausdorf.simcacing.tt.live.model.client.Stint;
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
	private String service;
	private String serviceDuration;
	private String refuelAmount;
	private String repairTime;

	public static List<PitstopDataView> getPitstopDataView(SessionController controller) {
		List<PitstopDataView> pitstopDataViews = new ArrayList<>();
		if (controller != null && controller.getRacePlan() != null) {
			int lapCount = 0;
			int stintNo = 0;
			RunData runData = controller.getRunData();
			LocalDateTime sessionToD = controller.getRacePlan().getPlanParameters().getTodStartTime();
			if (runData != null) {
				sessionToD = LocalDateTime.of(sessionToD.toLocalDate(), runData.getSessionToD());
			}
			LocalDateTime now = LocalDateTime.now();
			Duration accumulatedStintDuration = Duration.ZERO;
			for (Stint stint : controller.getStints().tailMap(0).values()) {
				Pitstop pitstop = controller.getPitStops().get(stint.getNo());
				if (pitstop == null || !pitstop.isComplete()) {
					// Stint not complete
					continue;
				}
				lapCount += stint.getLaps();
				stintNo++;
				Duration stintDuration = stint.getCurrentStintDuration() != null ? stint.getCurrentStintDuration() : Duration.ZERO;
				accumulatedStintDuration = accumulatedStintDuration.plus(stintDuration);
				Duration raceTimeLeft = controller.getRacePlan().getPlanParameters().getRaceDuration()
						.minus(accumulatedStintDuration);

				PitstopDataView view = PitstopDataView.builder()
						.allDrivers(new ArrayList<>())
						.driver(stint.getDriver())
						.lapNo(Integer.toUnsignedString(lapCount))
						.raceTimeLeft(TimeTools.shortDurationString(raceTimeLeft))
						.stintNo(Integer.toUnsignedString(stint.getNo()))
						.timePitted(pitstop.getEnterPits().format(DateTimeFormatter.ofPattern(TimeTools.HH_MM_SS)))
						.serviceDuration(TimeTools.shortDurationString(pitstop.getPitstopServiceTime()))
						.service(pitstop.getServiceFlagsString())
						.repairTime(TimeTools.shortDurationString(pitstop.getRepairAndTowingTime()))
						.pitStopDuration(TimeTools.shortDurationString(pitstop.getPitstopDuration()))
						.refuelAmount(fuelString(pitstop.getRefuelAmount()))
						.build();

				pitstopDataViews.add(view);
			}
			List<de.bausdorf.simcacing.tt.planning.model.Stint> stints =
					controller.getRacePlan().calculateStints(now, sessionToD, now.plus(controller.getRemainingSessionTime()));
			controller.getRacePlan().setCurrentRacePlan(stints);
			for (de.bausdorf.simcacing.tt.planning.model.Stint stint : stints) {
				lapCount += stint.getLaps();
				stintNo++;
				accumulatedStintDuration = accumulatedStintDuration.plus(stint.getStintDuration(true));
				Duration raceTimeLeft = controller.getRacePlan().getPlanParameters().getRaceDuration()
						.minus(accumulatedStintDuration);

				PitstopDataView view = PitstopDataView.builder()
						.driver(stint.getDriverName())
						.lapNo(Integer.toUnsignedString(lapCount))
						.timePitted(stint.getEndTimeString())
						.stintNo(Integer.toUnsignedString(stintNo))
						.allDrivers(controller.getRacePlan().getPlanParameters().getAllDrivers().stream()
								.map(IRacingDriver::getName)
								.collect(Collectors.toList())
						)
						.raceTimeLeft(TimeTools.shortDurationString(raceTimeLeft))
						.pitStopDuration("")
						.repairTime("")
						.service("")
						.serviceDuration("")
						.refuelAmount("")
						.build();

				pitstopDataViews.add(view);
			}
		}
		return pitstopDataViews;
	}

	private static String fuelString(double fuelAmount) {
		return String.format("%.3f", fuelAmount).replace(",", ".");
	}
}
