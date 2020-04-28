package de.bausdorf.simcacing.tt.planning.model;

import java.time.Duration;
import java.time.LocalDateTime;
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
				.build();

		newRacePlan.calculateStints();
		return newRacePlan;
	}

	public void calculateStints() {
		List<Stint> oldRacePlan = prepareOldRacePlan();

		LocalDateTime raceClock = planParameters.getSessionStartTime().plusSeconds(planParameters.getGreenFlagOffsetTime().toSecondOfDay());
		LocalDateTime todClock = planParameters.getTodStartTime().plusSeconds(planParameters.getGreenFlagOffsetTime().toSecondOfDay());
		LocalDateTime sessionEndTime = raceClock.plus(planParameters.getRaceDuration());
		int stintCount = 1;
		while( raceClock.isBefore(sessionEndTime) ) {
			String currentDriver = "N.N.";
			if( oldRacePlan.size() > stintCount) {
				currentDriver = oldRacePlan.get(stintCount-1).getDriverName();
			}
			Stint nextStint = calculateNewStint(raceClock, todClock, currentDriver,
					planParameters.getMaxCarFuel(),
					planParameters.getDriverNameEstimationAt(currentDriver, todClock));
			currentRacePlan.add(nextStint);
			stintCount++;

			raceClock = raceClock.plus(nextStint.getStintDuration(true));
			todClock = todClock.plus(nextStint.getStintDuration(true));
			// Check for last Stint ?
			if( raceClock.plus(nextStint.getStintDuration(false)).isAfter(sessionEndTime) ) {
				currentDriver = "N.N.";
				if( oldRacePlan.size() >= stintCount) {
					currentDriver = oldRacePlan.get(stintCount-1).getDriverName();
				}
				Stint lastStint = calculateLastStint(raceClock, todClock, currentDriver,
						Duration.between(raceClock, sessionEndTime),
						planParameters.getDriverNameEstimationAt(currentDriver, todClock));
				currentRacePlan.add(lastStint);
				break;
			}
		}

	}

	private List<Stint> prepareOldRacePlan() {
		if( currentRacePlan != null ) {
			if( !currentRacePlan.isEmpty() ) {
				List<Stint> oldRacePlan = new ArrayList<>(currentRacePlan);
				currentRacePlan.clear();
				return oldRacePlan;
			}
		} else {
			currentRacePlan = new ArrayList<>();
			return planParameters.getStints();
		}
		return new ArrayList<>();
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
}
