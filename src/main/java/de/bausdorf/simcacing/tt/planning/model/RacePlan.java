package de.bausdorf.simcacing.tt.planning.model;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;
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

	private String name;
	private String teamId;
	private ZonedDateTime sessionStart;
	private LocalTime greenFlagOffset;
	private LocalTime simStartTime;
	private Duration raceDuration;
	private String trackName;
	private String carName;
	private double carMaxFuel;

	private List<Stint> currentRacePlan;

	private List<String> availableDrivers;
	private Map<String, Estimation> estimations;
	private PitStopEstimation pitStopEstimation;

	public static RacePlan createRacePlanTemplate(String teamId,
			Duration raceDuration,
			ZonedDateTime sessionStartTime,
			double carMaxFuel,
			int driverCount,
			Estimation baseEstimation) {
		RacePlan new_race_plan =  RacePlan.builder()
				.name("New race plan")
				.teamId(teamId)
				.raceDuration(raceDuration)
				.sessionStart(sessionStartTime)
				.carMaxFuel(carMaxFuel)
				.estimations(new HashMap<String, Estimation>())
				.greenFlagOffset(LocalTime.MIN.plusSeconds(300))
				.pitStopEstimation(PitStopEstimation.defaultPitStopEstimation())
				.build();

		new_race_plan.getEstimations().put(COMMON_ESTIMATION_KEY, baseEstimation);

		List<String> availableDrivers = new ArrayList<>();
		for( int i = 1; i <= driverCount; i++) {
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

		LocalTime raceClock = sessionStart.toLocalTime().plusSeconds(greenFlagOffset.toSecondOfDay());
		LocalTime sessionEndTime = raceClock.plus(raceDuration);
		int currentDriverIndex = 0;
		while( raceClock.isBefore(sessionEndTime) ) {
			String currentDriver = availableDrivers.get(currentDriverIndex);
			Estimation driverEstimation = estimations.containsKey(currentDriver) ? estimations.get(currentDriver) : estimations.get(COMMON_ESTIMATION_KEY);
			Stint nextStint = calculateNewStint(raceClock, currentDriver, carMaxFuel, driverEstimation);
			currentRacePlan.add(nextStint);

			raceClock = raceClock.plus(nextStint.getStintDuration(true));
			currentDriverIndex = ++currentDriverIndex % availableDrivers.size();
			// Check for last Stint ?
			if( raceClock.plus(nextStint.getStintDuration(false)).isAfter(sessionEndTime) ) {
				currentDriver = availableDrivers.get(currentDriverIndex);
				driverEstimation = estimations.containsKey(currentDriver) ? estimations.get(currentDriver) : estimations.get(COMMON_ESTIMATION_KEY);
				Stint lastStint = calculateLastStint(raceClock, currentDriver, Duration.between(raceClock, sessionEndTime), driverEstimation);
				currentRacePlan.add(lastStint);
				break;
			}
		}

	}

	public Stint calculateNewStint(LocalTime stintStartTime, String driverName, double amountFuel, Estimation estimation) {
		Stint stint = newStintForDriver(stintStartTime, driverName);

		int maxLaps = (int)Math.floor(amountFuel / estimation.getAvgFuelPerLap());
		double fuelLeft = amountFuel - (estimation.getAvgFuelPerLap() * maxLaps);
		stint.setRefuelAmount(amountFuel - fuelLeft);
		stint.setLaps(maxLaps);
		Duration stintDuration = estimation.getAvgLapTime().multipliedBy(maxLaps);

		Duration pitStopDuration = pitStopEstimation.getOverallDuration();
		PitStop pitstop = PitStop.builder()
				.service(DEFAULT_SERVICE)
				.overallDuration(pitStopDuration)
				.service(DEFAULT_SERVICE)
				.build();
		stint.setPitStop(Optional.of(pitstop));
		stint.setEndTime(stintStartTime.plus(stintDuration).plus(pitStopDuration));

		return stint;
	}

	public Stint calculateLastStint(LocalTime stintStartTime, String driverName, Duration timeLeft, Estimation estimation) {
		Stint lastStint = newStintForDriver(stintStartTime, driverName);

		int lapsLeft = (int)Math.ceil((double)timeLeft.toMillis() / estimation.getAvgLapTime().toMillis());
		double neededFuel = lapsLeft * estimation.getAvgFuelPerLap();
		lastStint.setRefuelAmount(neededFuel);
		lastStint.setLaps(lapsLeft);
		lastStint.setEndTime(stintStartTime.plus(estimation.getAvgLapTime().multipliedBy(lapsLeft)));

		return lastStint;
	}

	public Stint newStintForDriver(LocalTime stintStartTime, String driverName) {
		return Stint.builder()
				.driverName(driverName)
				.startTime(stintStartTime)
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
