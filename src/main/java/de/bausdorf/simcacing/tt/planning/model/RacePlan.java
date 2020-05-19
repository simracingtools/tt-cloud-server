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

		newRacePlan.calculateStints();
		return newRacePlan;
	}

	public LocalDateTime getTodRaceTime(LocalTime sessionTime) {
		return planParameters.getTodStartTime()
				.plusSeconds(planParameters.getGreenFlagOffsetTime().toSecondOfDay())
				.plusSeconds(sessionTime.toSecondOfDay());
	}

	public void calculateStints() {
		ZonedDateTime raceClock = planParameters.getSessionStartTime().plusSeconds(planParameters.getGreenFlagOffsetTime().toSecondOfDay());
		LocalDateTime todClock = getTodRaceTime(LocalTime.MIN);
		ZonedDateTime sessionEndTime = raceClock.plus(planParameters.getRaceDuration());
		currentRacePlan = calculateStints(raceClock, todClock, sessionEndTime);
	}

	public List<Stint> calculateStints(ZonedDateTime raceClock, LocalDateTime todClock, ZonedDateTime raceTimeLeft) {
		List<Stint> stints = new ArrayList<>();

		while( raceClock.isBefore(raceTimeLeft) ) {
			String currentDriver = getDriverForClock(raceClock);
			Stint nextStint = calculateNewStint(raceClock, todClock, currentDriver,
					planParameters.getMaxCarFuel(),
					planParameters.getDriverNameEstimationAt(currentDriver, todClock));
			stints.add(nextStint);

			raceClock = raceClock.plus(nextStint.getStintDuration(true));
			todClock = todClock.plus(nextStint.getStintDuration(true));
			// Check for last Stint ?
			if( raceClock.plus(nextStint.getStintDuration(false)).isAfter(raceTimeLeft) ) {
				currentDriver = getDriverForClock(raceClock);
				Stint lastStint = calculateLastStint(raceClock, todClock, currentDriver,
						Duration.between(raceClock, raceTimeLeft),
						planParameters.getDriverNameEstimationAt(currentDriver, todClock));
				stints.add(lastStint);
				break;
			}
		}
		return stints;
	}

	private Stint calculateNewStint(ZonedDateTime stintStartTime, LocalDateTime todStartTime, String driverName, double amountFuel, Estimation estimation) {
		Stint stint = newStintForDriver(stintStartTime, todStartTime, driverName);

		int maxLaps = (int)Math.floor(amountFuel / estimation.getAvgFuelPerLap());
		double fuelLeft = amountFuel - (estimation.getAvgFuelPerLap() * maxLaps);
		stint.setRefuelAmount(amountFuel - fuelLeft);
		stint.setLaps(maxLaps);
		Duration stintDuration = estimation.getAvgLapTime().multipliedBy(maxLaps);


		PitStop pitstop = PitStop.defaultPitStop();
		stint.setPitStop(Optional.of(pitstop));
		stint.setEndTime(stintStartTime.plus(stintDuration).plus(pitstop.getOverallDuration()));

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

	private String getDriverForClock(ZonedDateTime raceClock) {
		log.debug("Get driver for clock: {}", raceClock.toString());
		for (Stint stint : currentRacePlan) {
			log.debug("Stint from {} until {}", stint.getStartTime().toString(), stint.getEndTime().toString());
			if (stint.getStartTime().isEqual(raceClock)
					|| (raceClock.isAfter(stint.getStartTime()) && raceClock.isBefore(stint.getEndTime()))) {
				return stint.getDriverName();
			}
		}
		return "unassigned";
	}
}
