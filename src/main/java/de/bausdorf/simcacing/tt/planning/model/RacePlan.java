package de.bausdorf.simcacing.tt.planning.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RacePlan {
	public final static String COMMON_ESTIMATION_KEY = "COMMON";
	public final static List<PitStopServiceType> DEFAULT_SERVICE = Collections.unmodifiableList(
			Arrays.asList(PitStopServiceType.FUEL, PitStopServiceType.WS, PitStopServiceType.TYRES));

	private RacePlanParameters planParameters;

	private List<Stint> currentRacePlan;

	private List<String> availableDrivers;
	private Map<String, Estimation> estimations;

	public static RacePlan createRacePlanTemplate(RacePlanParameters params) {
		RacePlan new_race_plan =  RacePlan.builder()
				.planParameters(params)
				.estimations(new HashMap<String, Estimation>())
				.build();

		Estimation estimation = Estimation.builder()
				.avgFuelPerLap(params.getAvgFuelPerLap())
				.avgLapTime(params.getAvgLapTime())
				.driverName(RacePlan.COMMON_ESTIMATION_KEY)
				.build();

		new_race_plan.getEstimations().put(COMMON_ESTIMATION_KEY, estimation);

		List<String> availableDrivers = new ArrayList<>();
		for( int i = 1; i <= params.getDriverCount(); i++) {
			availableDrivers.add("Driver " + i);
		}
		new_race_plan.setAvailableDrivers(availableDrivers);

		new_race_plan.calculateStints();
		return new_race_plan;
	}

	public void calculateStints() {
		if( currentRacePlan != null ) {
			if( !currentRacePlan.isEmpty() ) {
				currentRacePlan.clear();
			}
		} else {
			currentRacePlan = new ArrayList<>();
		}

		LocalDateTime raceClock = planParameters.getSessionStartTime().plusSeconds(planParameters.getGreenFlagOffsetTime().toSecondOfDay());
		LocalDateTime todClock = planParameters.getTodStartTime().plusSeconds(planParameters.getGreenFlagOffsetTime().toSecondOfDay());
		LocalDateTime sessionEndTime = raceClock.plus(planParameters.getRaceDuration());
		int currentDriverIndex = 0;
		while( raceClock.isBefore(sessionEndTime) ) {
			String currentDriver = availableDrivers.get(currentDriverIndex);
			Estimation driverEstimation = estimations.containsKey(currentDriver) ? estimations.get(currentDriver) : estimations.get(COMMON_ESTIMATION_KEY);
			Stint nextStint = calculateNewStint(raceClock, todClock, currentDriver, planParameters.getMaxCarFuel(), driverEstimation);
			currentRacePlan.add(nextStint);

			raceClock = raceClock.plus(nextStint.getStintDuration(true));
			todClock = todClock.plus(nextStint.getStintDuration(true));
			currentDriverIndex = ++currentDriverIndex % availableDrivers.size();
			// Check for last Stint ?
			if( raceClock.plus(nextStint.getStintDuration(false)).isAfter(sessionEndTime) ) {
				currentDriver = availableDrivers.get(currentDriverIndex);
				driverEstimation = estimations.containsKey(currentDriver) ? estimations.get(currentDriver) : estimations.get(COMMON_ESTIMATION_KEY);
				Stint lastStint = calculateLastStint(raceClock, todClock, currentDriver, Duration.between(raceClock, sessionEndTime), driverEstimation);
				currentRacePlan.add(lastStint);
				break;
			}
		}

	}

	public Stint calculateNewStint(LocalDateTime stintStartTime, LocalDateTime todStartTime, String driverName, double amountFuel, Estimation estimation) {
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

	public Stint calculateLastStint(LocalDateTime stintStartTime, LocalDateTime todStartTime, String driverName, Duration timeLeft, Estimation estimation) {
		Stint lastStint = newStintForDriver(stintStartTime, todStartTime, driverName);

		int lapsLeft = (int)Math.ceil((double)timeLeft.toMillis() / estimation.getAvgLapTime().toMillis());
		double neededFuel = lapsLeft * estimation.getAvgFuelPerLap();
		lastStint.setRefuelAmount(neededFuel);
		lastStint.setLaps(lapsLeft);
		lastStint.setEndTime(stintStartTime.plus(estimation.getAvgLapTime().multipliedBy(lapsLeft)));

		return lastStint;
	}

	public Stint newStintForDriver(LocalDateTime stintStartTime, LocalDateTime todStartTime, String driverName) {
		return Stint.builder()
				.driverName(driverName)
				.startTime(stintStartTime)
				.todStartTime(todStartTime)
				.pitStop(Optional.empty())
				.build();
	}

	public Estimation getEstimationForDriver(String name) {
		if( estimations.containsKey(name) ) {
			return estimations.get(name);
		}
		return estimations.get(COMMON_ESTIMATION_KEY);
	}
}
