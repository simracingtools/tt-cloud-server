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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.bausdorf.simcacing.tt.planning.persistence.PlanParameters;
import de.bausdorf.simcacing.tt.planning.persistence.Stint;
import de.bausdorf.simcacing.tt.planning.persistence.Estimation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@Builder
@Slf4j
public class RacePlan {
	private PlanParameters planParameters;

	private List<Stint> currentRacePlan;

	public static RacePlan createRacePlanTemplate(PlanParameters params) {
		RacePlan newRacePlan =  RacePlan.builder()
				.planParameters(params)
				.currentRacePlan(params.getStints() == null ? new ArrayList<>() : params.getStints())
				.build();

		newRacePlan.calculatePlannedStints();
		return newRacePlan;
	}

	public LocalDateTime getTodRaceTime(Duration sessionTime) {
		return planParameters.getTodStartTime()
				.plus(planParameters.getGreenFlagOffsetTime())
				.plus(sessionTime);
	}

	public void calculateLiveStints() {
		OffsetDateTime raceClock = planParameters.getSessionStartDateTime().plus(planParameters.getGreenFlagOffsetTime());
		LocalDateTime todClock = getTodRaceTime(Duration.ZERO);
		OffsetDateTime sessionEndTime = raceClock.plus(planParameters.getRaceDuration());
		currentRacePlan = calculateLiveStints(raceClock, todClock, sessionEndTime);
	}

	public List<Stint> calculateLiveStints(OffsetDateTime raceClock, LocalDateTime todClock, OffsetDateTime sessionEndTime) {
		int stintIndex = PlanningTools.stintIndexAt(raceClock, planParameters.getStints());
		log.debug("calculating live stint plan from index: {}", stintIndex);
		return calculateStints(raceClock, todClock, sessionEndTime, stintIndex);
	}

	public void calculatePlannedStints() {
		OffsetDateTime raceClock = planParameters.getSessionStartDateTime().plus(planParameters.getGreenFlagOffsetTime());
		LocalDateTime todClock = getTodRaceTime(Duration.ZERO);
		OffsetDateTime sessionEndTime = raceClock.plus(planParameters.getRaceDuration());
		if (!Duration.ZERO.equals(planParameters.getAvgLapTime())) {
			currentRacePlan = calculateStints(raceClock, todClock, sessionEndTime, 0);
		}
	}

	public List<Stint> calculateStints(OffsetDateTime raceClock, LocalDateTime todClock, OffsetDateTime sessionEndTime, int fromIndex) {
		List<Stint> stints = new ArrayList<>();
		int stintIndex = fromIndex;
		while( raceClock.isBefore(sessionEndTime) ) {

			List<PitStopServiceType> service = new ArrayList<>(Arrays.asList(PitStopServiceType.values()));
			String currentDriver = "unassigned";

			if (planParameters.getStints().size() > stintIndex) {
				currentDriver = planParameters.getStints().get(stintIndex).getDriverName();
				service = planParameters.getStints().get(stintIndex).getService();
			}

			Stint nextStint = calculateNewStint(raceClock, todClock, currentDriver,
					planParameters.getMaxCarFuel(), service,
					PlanningTools.getDriverNameEstimationAt(planParameters, currentDriver, todClock),
					Duration.between(raceClock, sessionEndTime));
			stints.add(nextStint);

			raceClock = raceClock.plus(PlanningTools.getStintDuration(nextStint, true));
			todClock = todClock.plus(PlanningTools.getStintDuration(nextStint, true));
			stintIndex++;
		}
		return stints;
	}

	private Stint calculateNewStint(OffsetDateTime stintStartTime, LocalDateTime todStartTime, String driverName,
			double amountFuel, List<PitStopServiceType> service, Estimation estimation, Duration timeLeft) {
		Stint stint = newStintForDriver(stintStartTime, todStartTime, driverName);

		int stintLaps = (int)Math.floor(amountFuel / estimation.getAvgFuelPerLap());
		double fuelLeft = amountFuel - (estimation.getAvgFuelPerLap() * stintLaps);
		Duration stintDuration = estimation.getAvgLapTime().multipliedBy(stintLaps);

		if (stintDuration.toMillis() > timeLeft.toMillis()) {
			// This is the last stint
			stintLaps = (int)Math.ceil((double)timeLeft.toMillis() / estimation.getAvgLapTime().toMillis());
			stintDuration = estimation.getAvgLapTime().multipliedBy(stintLaps);
			double neededFuel = stintLaps * estimation.getAvgFuelPerLap();
			stint.setRefuelAmount(neededFuel);
			stint.setEndTime(stintStartTime.plus(stintDuration));
			stint.setLastStint(true);
		} else {
			// There are further stints

			stint.setService(new ArrayList<>(service)); // Duplicate service list to prevent entity identity problems
			stint.setRefuelAmount(amountFuel - fuelLeft);
			stint.setEndTime(stintStartTime
					.plus(stintDuration)
					.plus(planParameters.getAvgPitLaneTime())
					.plus(PlanningTools.calculateServiceDuration(stint.getService(), stint.getRefuelAmount())));
		}

		stint.setLaps(stintLaps);

		return stint;
	}

	private Stint newStintForDriver(OffsetDateTime stintStartTime, LocalDateTime todStartTime, String driverName) {
		return Stint.builder()
				.driverName(driverName)
				.startTime(stintStartTime)
				.todStartTime(todStartTime)
				.service(new ArrayList<>())
				.build();
	}
}
