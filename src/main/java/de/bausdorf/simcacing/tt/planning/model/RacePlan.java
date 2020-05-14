package de.bausdorf.simcacing.tt.planning.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
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
		LocalDateTime raceClock = planParameters.getSessionStartTime().plusSeconds(planParameters.getGreenFlagOffsetTime().toSecondOfDay());
		LocalDateTime todClock = getTodRaceTime(LocalTime.MIN);
		LocalDateTime sessionEndTime = raceClock.plus(planParameters.getRaceDuration());
		currentRacePlan = calculateStints(raceClock, todClock, sessionEndTime);
	}

	public List<Stint> calculateStints(LocalDateTime raceClock, LocalDateTime todClock, LocalDateTime raceTimeLeft) {
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

	private Stint calculateNewStint(LocalDateTime stintStartTime, LocalDateTime todStartTime, String driverName, double amountFuel, Estimation estimation) {
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

	private Stint calculateLastStint(LocalDateTime stintStartTime, LocalDateTime todStartTime, String driverName, Duration timeLeft, Estimation estimation) {
		Stint lastStint = newStintForDriver(stintStartTime, todStartTime, driverName);

		int lapsLeft = (int)Math.ceil((double)timeLeft.toMillis() / estimation.getAvgLapTime().toMillis());
		double neededFuel = lapsLeft * estimation.getAvgFuelPerLap();
		lastStint.setRefuelAmount(neededFuel);
		lastStint.setLaps(lapsLeft);
		lastStint.setEndTime(stintStartTime.plus(estimation.getAvgLapTime().multipliedBy(lapsLeft)));

		return lastStint;
	}

	private Stint newStintForDriver(LocalDateTime stintStartTime, LocalDateTime todStartTime, String driverName) {
		return Stint.builder()
				.driverName(driverName)
				.startTime(stintStartTime)
				.todStartTime(todStartTime)
				.pitStop(Optional.empty())
				.build();
	}

	private String getDriverForClock(LocalDateTime raceClock) {
		for (Stint stint : currentRacePlan) {
			if (stint.getStartTime().isEqual(raceClock)
					|| (raceClock.isAfter(stint.getStartTime()) && raceClock.isBefore(stint.getEndTime()))) {
				return stint.getDriverName();
			}
		}
		return "unassigned";
	}
}
