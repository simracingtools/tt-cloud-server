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
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import de.bausdorf.simcacing.tt.live.impl.SessionController;
import de.bausdorf.simcacing.tt.planning.model.PitStop;
import de.bausdorf.simcacing.tt.planning.model.PitStopServiceType;
import de.bausdorf.simcacing.tt.planning.model.RacePlan;
import de.bausdorf.simcacing.tt.planning.model.RacePlanParameters;
import de.bausdorf.simcacing.tt.planning.model.Stint;
import de.bausdorf.simcacing.tt.util.TeamtacticsServerProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlanningTools {

	private static int wsServiceSeconds = 5;
	private static int tyreServiceSeconds = 20;
	private static int fuelServiceSeconds10l = 3;

	private PlanningTools() {
		super();
	}

	public static String driverNameAt(ZonedDateTime clock, List<Stint> stints) {
		log.debug("Get driver for clock: {}", clock.toString());
		Stint stint = stintAt(clock, stints);
		return stint != null ? stint.getDriverName() : "unassigned";
	}

	public static Optional<PitStop> pitstopAt(ZonedDateTime clock, List<Stint> stints) {
		log.debug("Get pitstop for clock: {}", clock.toString());
		Stint stint = stintAt(clock, stints);
		return stint != null ? stint.getPitStop() : Optional.empty();
	}

	public static Stint stintAt(ZonedDateTime clock, List<Stint> stints) {
		for (Stint stint : stints) {
			log.debug("Stint from {} until {}", stint.getStartTime().toString(), stint.getEndTime().toString());
			if (stint.getStartTime().isEqual(clock)
					|| (clock.isAfter(stint.getStartTime()) && clock.isBefore(stint.getEndTime()))) {
				return stint;
			}
		}
		return null;
	}

	public static Stint stintToModify(SessionController controller, int liveIndex) {
		try {
			de.bausdorf.simcacing.tt.planning.model.Stint changedStint =
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
		for (PitStopServiceType serviceType : serviceList) {
			switch (serviceType) {
				case TYRES:
					serviceDuration = serviceDuration.plusSeconds(PlanningTools.tyreServiceSeconds);
					break;
				case WS:
					serviceDuration = serviceDuration.plusSeconds(PlanningTools.wsServiceSeconds);
					break;
				case FUEL:
					int refuelSeconds = (int)Math.ceil((amountRefuel / 10) * PlanningTools.fuelServiceSeconds10l);
					serviceDuration = serviceDuration.plusSeconds(refuelSeconds);
					break;
				default:
					break;
			}
		}
		return serviceDuration;
	}

	public static void configureServiceDuration(TeamtacticsServerProperties config) {
		fuelServiceSeconds10l = config.getServiceDurationSecondsFuel10l();
		tyreServiceSeconds = config.getServiceDurationSecondsTyres();
		wsServiceSeconds = config.getServiceDurationSecondsWs();
	}

	public static void updatePitLaneDurations(Duration approach, Duration depart, RacePlanParameters planParameters) {
		for (Stint stint : planParameters.getStints()) {
			Optional<PitStop> pitstop = stint.getPitStop();
			if (pitstop.isPresent()) {
				pitstop.get().setApproach(approach);
				pitstop.get().setDepart(depart);
			}
		}
	}

	public static void recalculateStints(RacePlanParameters parameters) {
		RacePlan plan = RacePlan.createRacePlanTemplate(parameters);
		plan.calculatePlannedStints();
		parameters.setStints(plan.getCurrentRacePlan());
	}
}
