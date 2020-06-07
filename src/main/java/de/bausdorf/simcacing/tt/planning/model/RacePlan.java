package de.bausdorf.simcacing.tt.planning.model;

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
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import de.bausdorf.simcacing.tt.planning.PlanningTools;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@Builder
@Slf4j
public class RacePlan {
	public static final List<PitStopServiceType> DEFAULT_SERVICE = Collections.unmodifiableList(
			Arrays.asList(PitStopServiceType.FUEL, PitStopServiceType.WS, PitStopServiceType.TYRES));

	private RacePlanParameters planParameters;

	private List<Stint> currentRacePlan;

	public static RacePlan createRacePlanTemplate(RacePlanParameters params) {
		RacePlan newRacePlan =  RacePlan.builder()
				.planParameters(params)
				.currentRacePlan(params.getStints() == null ? new ArrayList<>() : params.getStints())
				.build();

		newRacePlan.calculatePlannedStints();
		return newRacePlan;
	}

	public LocalDateTime getTodRaceTime(LocalTime sessionTime) {
		return planParameters.getTodStartTime()
				.plus(planParameters.getGreenFlagOffsetTime())
				.plusSeconds(sessionTime.toSecondOfDay());
	}

	public void calculateLiveStints() {
		ZonedDateTime raceClock = planParameters.getSessionStartTime().plus(planParameters.getGreenFlagOffsetTime());
		LocalDateTime todClock = getTodRaceTime(LocalTime.MIN);
		ZonedDateTime sessionEndTime = raceClock.plus(planParameters.getRaceDuration());
		currentRacePlan = calculateLiveStints(raceClock, todClock, sessionEndTime);
	}

	public void calculatePlannedStints() {
		ZonedDateTime raceClock = planParameters.getSessionStartTime().plus(planParameters.getGreenFlagOffsetTime());
		LocalDateTime todClock = getTodRaceTime(LocalTime.MIN);
		ZonedDateTime sessionEndTime = raceClock.plus(planParameters.getRaceDuration());
		if (!Duration.ZERO.equals(planParameters.getAvgLapTime())) {
			currentRacePlan = calculatePlannedStints(raceClock, todClock, sessionEndTime);
		}
	}

	public List<Stint> calculatePlannedStints(ZonedDateTime raceClock, LocalDateTime todClock, ZonedDateTime raceTimeLeft) {
		List<Stint> stints = new ArrayList<>();
		int stintIndex = 0;
		while( raceClock.isBefore(raceTimeLeft) ) {

			Optional<PitStop> pitStop = Optional.of(PitStop.builder()
					.depart(planParameters.getAvgPitLaneTime().dividedBy(2L))
					.approach(planParameters.getAvgPitLaneTime().dividedBy(2L))
					.service(Arrays.asList(PitStopServiceType.WS, PitStopServiceType.FUEL, PitStopServiceType.TYRES))
					.build());
			String currentDriver = "unassigned";

			if (planParameters.getStints().size() > stintIndex) {
				currentDriver = planParameters.getStints().get(stintIndex).getDriverName();
				pitStop = planParameters.getStints().get(stintIndex).getPitStop();
				if(pitStop.isPresent() && Duration.ZERO.equals(pitStop.get().getApproach())
						&& Duration.ZERO.equals(pitStop.get().getDepart())) {
					pitStop.get().updatePitLaneDuration(planParameters.getAvgPitLaneTime());
				}
			}

			Stint nextStint = calculateNewStint(raceClock, todClock, currentDriver,
					planParameters.getMaxCarFuel(), pitStop,
					planParameters.getDriverNameEstimationAt(currentDriver, todClock));
			stints.add(nextStint);

			raceClock = raceClock.plus(nextStint.getStintDuration(true));
			todClock = todClock.plus(nextStint.getStintDuration(true));
			stintIndex++;
			// Check for last Stint ?
			if( raceClock.plus(nextStint.getStintDuration(false)).isAfter(raceTimeLeft) ) {
				if (planParameters.getStints().size() > stintIndex) {
					currentDriver = planParameters.getStints().get(stintIndex).getDriverName();
				}
				Stint lastStint = calculateLastStint(raceClock, todClock, currentDriver,
						Duration.between(raceClock, raceTimeLeft),
						planParameters.getDriverNameEstimationAt(currentDriver, todClock));
				stints.add(lastStint);
				break;
			}
		}
		return stints;
	}

	public List<Stint> calculateLiveStints(ZonedDateTime raceClock, LocalDateTime todClock, ZonedDateTime raceTimeLeft) {
		List<Stint> stints = new ArrayList<>();
		while( raceClock.isBefore(raceTimeLeft) ) {

			Optional<PitStop> pitStop = PlanningTools.pitstopAt(raceClock, planParameters.getStints());
			String currentDriver = PlanningTools.driverNameAt(raceClock, planParameters.getStints());

			Stint nextStint = calculateNewStint(raceClock, todClock, currentDriver,
					planParameters.getMaxCarFuel(), pitStop,
					planParameters.getDriverNameEstimationAt(currentDriver, todClock));
			stints.add(nextStint);

			raceClock = raceClock.plus(nextStint.getStintDuration(true));
			todClock = todClock.plus(nextStint.getStintDuration(true));
			// Check for last Stint ?
			if( raceClock.plus(nextStint.getStintDuration(false)).isAfter(raceTimeLeft) ) {
				currentDriver = PlanningTools.driverNameAt(raceClock, planParameters.getStints());
				Stint lastStint = calculateLastStint(raceClock, todClock, currentDriver,
						Duration.between(raceClock, raceTimeLeft),
						planParameters.getDriverNameEstimationAt(currentDriver, todClock));
				stints.add(lastStint);
				break;
			}
		}
		return stints;
	}

	private Stint calculateNewStint(ZonedDateTime stintStartTime, LocalDateTime todStartTime, String driverName,
			double amountFuel, Optional<PitStop> pitstop, Estimation estimation) {
		Stint stint = newStintForDriver(stintStartTime, todStartTime, driverName);

		int maxLaps = (int)Math.floor(amountFuel / estimation.getAvgFuelPerLap());
		double fuelLeft = amountFuel - (estimation.getAvgFuelPerLap() * maxLaps);
		stint.setRefuelAmount(amountFuel - fuelLeft);
		stint.setLaps(maxLaps);
		Duration stintDuration = estimation.getAvgLapTime().multipliedBy(maxLaps);

		stint.setPitStop(pitstop);
		stint.setEndTime(stintStartTime
				.plus(stintDuration)
				.plus(pitstop.isPresent() ? pitstop.get().getOverallDuration(stint.getRefuelAmount()) : Duration.ZERO)
		);

		return stint;
	}

	private Stint calculateLastStint(ZonedDateTime stintStartTime, LocalDateTime todStartTime, String driverName, Duration timeLeft, Estimation estimation) {
		Stint lastStint = newStintForDriver(stintStartTime, todStartTime, driverName);

		int lapsLeft = (int)Math.ceil((double)timeLeft.toMillis() / estimation.getAvgLapTime().toMillis());
		double neededFuel = lapsLeft * estimation.getAvgFuelPerLap();
		lastStint.setRefuelAmount(neededFuel);
		lastStint.setLaps(lapsLeft);
		lastStint.setEndTime(stintStartTime.plus(estimation.getAvgLapTime().multipliedBy(lapsLeft)));
		lastStint.setLastStint(true);

		return lastStint;
	}

	private Stint newStintForDriver(ZonedDateTime stintStartTime, LocalDateTime todStartTime, String driverName) {
		return Stint.builder()
				.driverName(driverName)
				.startTime(stintStartTime)
				.todStartTime(todStartTime)
				.pitStop(Optional.empty())
				.build();
	}
}
