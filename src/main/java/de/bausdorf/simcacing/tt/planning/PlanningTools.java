package de.bausdorf.simcacing.tt.planning;

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
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import de.bausdorf.simcacing.tt.live.impl.SessionController;
import de.bausdorf.simcacing.tt.planning.persistence.ScheduleEntry;
import de.bausdorf.simcacing.tt.planning.persistence.PlanParameters;
import de.bausdorf.simcacing.tt.planning.persistence.Roster;
import de.bausdorf.simcacing.tt.planning.persistence.Stint;
import de.bausdorf.simcacing.tt.planning.persistence.Estimation;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.util.TeamtacticsServerProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlanningTools {
	public static final IRacingDriver NN_DRIVER = IRacingDriver.builder()
			.name("N.N")
			.id("0")
			.build();

	private static int wsServiceSeconds = 5;
	private static int tyreServiceSeconds = 20;
	private static int fuelServiceSeconds10l = 3;

	private PlanningTools() {
		super();
	}

	public static String driverNameAt(OffsetDateTime clock, List<Stint> stints) {
		log.debug("Get driver for clock: {}", clock.toString());
		Stint stint = stintAt(clock, stints);
		return stint != null ? stint.getDriverName() : "unassigned";
	}

	public static List<IRacingDriver> getAvailableDrivers(PlanParameters parameters, Stint stint) {
		if (parameters.getRoster() != null ) {
			return getAvailableDrivers(parameters.getRoster(), stint.getStartTime()).stream()
					.filter(s -> getDriverStatusAt(parameters.getRoster(), s.getId(), stint.getEndTime()) != ScheduleDriverOptionType.BLOCKED)
					.collect(Collectors.toList());
		}
		return Collections.singletonList(NN_DRIVER);
	}

	public static ScheduleDriverOptionType getDriverStatusAt(Roster roster, String driverId, OffsetDateTime time) {
		List<ScheduleEntry> scheduleEntryList = roster.getDriverAvailability().stream()
				.filter(e -> e.getDriver().getId().equalsIgnoreCase(driverId))
				.collect(Collectors.toList());
		ScheduleEntry last = null;
		for (ScheduleEntry scheduleEntry : scheduleEntryList) {
			if (scheduleEntry.getFromTime().isAfter(time)) {
				break;
			}
			last = scheduleEntry;
		}
		return last != null ? last.getStatus() : ScheduleDriverOptionType.UNSCHEDULED;
	}

	public static List<IRacingDriver> getAvailableDrivers(Roster roster, OffsetDateTime forTime) {
		Set<IRacingDriver> availableDrivers = new HashSet<>();
		for (ScheduleEntry scheduleEntry : roster.getDriverAvailability()) {
			if (scheduleEntry.getFromTime().isAfter(forTime)) {
				continue;
			}
			if (scheduleEntry.getStatus() != ScheduleDriverOptionType.BLOCKED) {
				availableDrivers.add(scheduleEntry.getDriver());
			}
		}
		return new ArrayList<>(availableDrivers);
	}

	public static Stint stintAt(OffsetDateTime clock, List<Stint> stints) {
		for (Stint stint : stints) {
			log.debug("Stint from {} until {}", stint.getStartTime().toString(), stint.getEndTime().toString());
			if (stint.getStartTime().isEqual(clock)
					|| (clock.isAfter(stint.getStartTime()) && clock.isBefore(stint.getEndTime()))) {
				return stint;
			}
		}
		return null;
	}

	public static int stintIndexAt(OffsetDateTime clock, List<Stint> stints) {
		for (Stint stint : stints) {
			log.debug("Stint from {} until {}", stint.getStartTime().toString(), stint.getEndTime().toString());
			if (stint.getStartTime().isEqual(clock)
					|| (clock.isAfter(stint.getStartTime()) && clock.isBefore(stint.getEndTime()))) {
				return stints.indexOf(stint);
			}
		}
		return 0;
	}

	public static Stint stintToModify(SessionController controller, int liveIndex) {
		try {
			Stint changedStint =
					controller.getRacePlan().getCurrentRacePlan().get(liveIndex);
			log.debug("Changed stint: {}", changedStint);
			return PlanningTools.stintAt(changedStint.getStartTime(), controller.getRacePlan().getPlanParameters().getStints());
		} catch (ArrayIndexOutOfBoundsException e) {
			log.warn("No stint for live index {}", liveIndex);
		}
		return null;
	}

	public static Duration calculateServiceDuration(List<PitStopServiceType> serviceList, double amountRefuel) {
		Duration serviceDuration = Duration.ZERO;
		if (serviceList != null) {
			for (PitStopServiceType serviceType : serviceList) {
				switch (serviceType) {
					case TYRES:
						serviceDuration = serviceDuration.plusSeconds(PlanningTools.tyreServiceSeconds);
						break;
					case WS:
						serviceDuration = serviceDuration.plusSeconds(PlanningTools.wsServiceSeconds);
						break;
					case FUEL:
						int refuelSeconds = (int) Math.ceil((amountRefuel / 10) * PlanningTools.fuelServiceSeconds10l);
						serviceDuration = serviceDuration.plusSeconds(refuelSeconds);
						break;
					default:
						break;
				}
			}
		}
		return serviceDuration;
	}

	public static void configureServiceDuration(TeamtacticsServerProperties config) {
		fuelServiceSeconds10l = config.getServiceDurationSecondsFuel10l();
		tyreServiceSeconds = config.getServiceDurationSecondsTyres();
		wsServiceSeconds = config.getServiceDurationSecondsWs();
	}

	public static void recalculateStints(PlanParameters parameters) {
		RacePlan plan = RacePlan.createRacePlanTemplate(parameters);
		plan.calculatePlannedStints();
		parameters.updateStints(plan.getCurrentRacePlan());
	}

	public static Duration getStintDuration(Stint stint, boolean includePitStopTimes) {
		if (stint.getStartTime() != null && stint.getEndTime() != null) {
			if (!includePitStopTimes) {
				return Duration.between(stint.getStartTime(), stint.getEndTime())
						.minus(PlanningTools.calculateServiceDuration(stint.getService(), stint.getRefuelAmount()));
			}
			return Duration.between(stint.getStartTime(), stint.getEndTime());
		}
		return Duration.ZERO;
	}

	public static Estimation getDriverNameEstimationAt(PlanParameters parameters, String driverName, LocalDateTime todDateTime) {
	if (parameters.getRoster() != null && driverName != null) {
			List<Estimation> estimationList = parameters.getRoster().getDriverEstimations().stream()
					.filter(e -> e.getDriver().getName().equalsIgnoreCase(driverName))
					.collect(Collectors.toList());
			Estimation last = getGenericEstimation(parameters);
			for (Estimation estimation : estimationList) {
				if (estimation.getTodFrom().isAfter(todDateTime)) {
					break;
				}
				last = estimation;
			}
			return last;
		}
		return getGenericEstimation(parameters);
	}

	public static Estimation getDriverEstimationAt(PlanParameters parameters, IRacingDriver driver, LocalDateTime todDateTime) {
		if (parameters.getRoster() != null) {
			List<Estimation> estimationList = parameters.getRoster().getDriverEstimations().stream()
					.filter(e -> e.getDriver().getName().equalsIgnoreCase(driver.getId()))
					.collect(Collectors.toList());
			Estimation last = getGenericEstimation(parameters);
			for (Estimation estimation : estimationList) {
				if (estimation.getTodFrom().isAfter(todDateTime)) {
					break;
				}
				last = estimation;
			}
			return last;
		}
		return getGenericEstimation(parameters);
	}

	public static Estimation getGenericEstimation(PlanParameters parameters) {
		return Estimation.builder()
				.todFrom(parameters.getTodStartTime())
				.avgFuelPerLap(parameters.getAvgFuelPerLap())
				.avgLapTime(parameters.getAvgLapTime())
				.build();
	}
}
