package de.bausdorf.simcacing.tt.live.impl;

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

import de.bausdorf.simcacing.tt.live.clientapi.DuplicateLapException;
import de.bausdorf.simcacing.tt.live.model.client.*;
import de.bausdorf.simcacing.tt.planning.PlanningTools;
import de.bausdorf.simcacing.tt.planning.persistence.Estimation;
import de.bausdorf.simcacing.tt.planning.RacePlan;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.util.TimeTools;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@Getter
public class SessionController {

	@Setter
	private long lastUpdate;
	private final SessionData sessionData;
	@Setter
	private RacePlan racePlan;
	@Setter
	private ZonedDateTime sessionRegistered;
	@Setter
	private Duration greenFlagTime;
	private final SortedMap<Integer, Stint> stints = new TreeMap<>();
	private final SortedMap<Integer, LapData> laps = new TreeMap<>();
	private final SortedMap<Integer, Pitstop> pitStops = new TreeMap<>();
	private final Map<String, SyncData> heartbeats = new HashMap<>();
	private RunData runData;
	private TyreData tyreData;
	private int currentLapNo;
	private boolean uncleanLap;
	private boolean onOutLap;

	@Setter
	private IRacingDriver currentDriver;
	@Setter
	private String teamId;
	@Setter
	private TrackLocationType currentTrackLocation;
	private Duration repairTimeLeft;
	private Duration optRepairTimeLeft;
	private Duration towTimeLeft;
	@Setter
	private LocalTime sessionToD;
	private final ChartData chartData;

	public SessionController(SessionData sessionData) {
		this.sessionData = sessionData;
		this.chartData = new ChartData();
		log.info("Created session controller for {}", sessionData.getSessionId());
	}

	public void addLap(LapData newLap) {

		if (laps.containsKey(newLap.getNo())) {
			throw new DuplicateLapException("Lap " + newLap.getNo() + " already there");
		}
		double lastLapFuel = 0.0;
		Optional<LapData> lastLap = getPreviousLap(newLap.getNo());
		if (lastLap.isPresent()) {
			lastLapFuel = lastLap.get().getFuelLevel() - newLap.getFuelLevel();
		}
		newLap.setLastLapFuelUsage(lastLapFuel);
		setStintValuesToLap(newLap);

		log.debug("New Lap: {}", newLap);
		currentLapNo = newLap.getNo();
		newLap.setUnclean(uncleanLap);
		newLap.setOutLap(onOutLap);
		uncleanLap = false;
		onOutLap = false;

		laps.put(currentLapNo, newLap);
		Stint currentStint = stints.get(newLap.getStint());
		if (currentStint == null) {
			currentStint = Stint.builder()
					.no(newLap.getStint())
					.driver(newLap.getDriver())
					.laps(newLap.getStintLap())
					.lastLaptime(newLap.getLapTime())
					.avgLapTime(newLap.getLapTime())
					.lastLapFuel(newLap.getLastLapFuelUsage())
					.avgFuelPerLap(lastLapFuel)
					.currentStintDuration(newLap.getLapTime())
					.todStart(sessionToD)
					.avgTrackTemp(newLap.getTrackTemp())
					.build();
			log.debug("New Stint: {}", currentStint);
			stints.put(currentStint.getNo(), currentStint);
		} else {
			updateStint(currentStint, newLap);
		}

		Assumption assumption = getAssumptionFromRacePlan();
		calculateStintLaps(currentStint, runData != null ? runData.getFuelLevel() : 0.0D, assumption);
		calculateExpectedStintDuration(currentStint, assumption);
		chartData.addLap(newLap);
		log.debug("Current stint: {}", currentStint);
	}

	public boolean updateRunData(RunData runData) {
		this.runData = runData;
		this.sessionToD = runData.getSessionToD();
		if (greenFlagTime == null && runData.getFlags().contains(FlagType.GREEN)) {
			greenFlagTime = runData.getSessionTime();
			return true;
		}
		return false;
	}

	public void updateTyreData(TyreData tyreData) {
		this.tyreData = tyreData;
	}

	public void updateSyncData(SyncData syncData) {
		heartbeats.put(syncData.getClientId(), syncData);
	}

	public Stint processEventData(EventData eventData) {
		log.debug("New event: {}", eventData);
		sessionToD = eventData.getSessionToD();
		if (currentTrackLocation == null) {
			// This is the first event
			currentTrackLocation = eventData.getTrackLocationType();
			return null;
		}
		Stint stint = null;
		switch (eventData.getTrackLocationType()) {
			case APPROACHING_PITS:
				processApproachingPitsEvent(eventData);
				break;
			case PIT_STALL:
				processPitStallEvent(eventData);
				break;
			case ONTRACK:
				stint = processOnTrackEvent(eventData);
				break;
			case OFF_WORLD:
			case OFFTRACK:
				processOffTrackEvent(eventData);
		}
		currentTrackLocation = eventData.getTrackLocationType();

		return stint;
	}

	public Duration getRemainingSessionTime() {
		if (runData != null && !runData.getSessionTimeRemaining().isZero()) {
			return runData.getSessionTimeRemaining();
		}
		Optional<Duration> sessionDuration = sessionData.getSessionDuration();
		if (sessionDuration.isPresent()) {
			return sessionDuration.get().minus(getCurrentSessionTime());
		}
		return Duration.ZERO;
	}

	public Duration getCurrentSessionTime() {
		return runData == null ? Duration.ZERO : runData.getSessionTime();
	}

	public LocalTime getCurrentRaceSessionTime() {
		if (runData == null) {
			return LocalTime.MIN;
		}
		return LocalTime.MIN.plus(runData.getSessionTime().minus(
				greenFlagTime != null ? greenFlagTime : Duration.ZERO));
	}

	public double getAvailableLapsForFuelLevel(double currentFuelLevel) {
		double avgFuelPerLap = getAssumptionFromRacePlan().getAvgFuelPerLap().orElse(0.0D);
		if (avgFuelPerLap > 0.0D) {
			return currentFuelLevel / avgFuelPerLap;
		} else {
			Optional<Stint> lastStint = getLastStint();
			if (lastStint.isPresent()) {
				return currentFuelLevel / lastStint.get().getAvgFuelPerLap();
			}
		}
		return 0.0D;
	}

	public double getAvailableLapsForFuelLevel() {
		if (runData != null) {
			return getAvailableLapsForFuelLevel(runData.getFuelLevel());
		}
		return 0.0D;
	}

	public Optional<Stint> getLastStint() {
		if (stints.isEmpty()) {
			return Optional.empty();
		}
		return Optional.ofNullable(stints.get(stints.lastKey()));
	}

	public Duration getCurrentStintTime() {
		Optional<Stint> lastStint = getLastStint();
		return lastStint.filter(stint -> stint.getCurrentStintDuration() != null)
				.map(Stint::getCurrentStintDuration)
				.orElse(Duration.ZERO);
	}

	public Duration getRemainingStintTime() {
		Optional<Stint> lastStint = getLastStint();
		if (lastStint.isPresent()) {
			Duration expectedStintDuration = lastStint.get().getExpectedStintDuration();
			Duration currentStintDuration = lastStint.get().getCurrentStintDuration();
			if (expectedStintDuration != null && currentStintDuration != null) {
				return expectedStintDuration.minus(currentStintDuration);
			}
		}
		return Duration.ZERO;
	}

	public int getRemainingStintCount() {
		if (racePlan != null) {
			return racePlan.getCurrentRacePlan().size();
		}
		return 0;
	}

	public int getRemainingLapCount() {
		if (runData != null && runData.getLapsRemaining() < 1000) {
			return runData.getLapsRemaining();
		}
		Duration remainingSessionTime = getRemainingSessionTime();
		Optional<Stint> lastStint = getLastStint();
		if (lastStint.isPresent()) {
			Duration lastStintAvgLapTime = lastStint.get().getAvgLapTime();
			if (lastStintAvgLapTime != null && !lastStintAvgLapTime.isZero()) {
				return (int) Math.ceil((double) remainingSessionTime.getSeconds() / lastStintAvgLapTime.getSeconds());
			}
		}
		return 0;
	}

	public Optional<LapData> getLastRecordedLap() {
		if (laps.isEmpty()) {
			return Optional.empty();
		}
		Integer lastKey = laps.lastKey();
		return Optional.ofNullable(laps.get(lastKey));
	}

	public Pitstop getLastPitstop() {
		return !pitStops.isEmpty() ? pitStops.get(pitStops.lastKey()) : null;
	}

	public Duration getCurrentDriverBestLap() {
		if (currentDriver == null) {
			return Duration.ZERO;
		}
		double millis = laps.values().stream()
				.filter(s -> s.getDriverId().equalsIgnoreCase(currentDriver.getId()))
				.filter(s-> s.getLapTime().getSeconds() > 0)
				.mapToDouble(s -> s.getLapTime().toMillis())
				.min().orElse(0.0D);
		return Duration.ofMillis((long) millis);
	}

	public Duration getFastestLap() {
		double millis = laps.values().stream()
				.filter(s -> s.getLapTime().toMillis() > 0)
				.mapToDouble(s -> s.getLapTime().toMillis())
				.min().orElse(0.0D);
		return Duration.ofMillis((long) millis);
	}

	public Duration getSlowestLap() {
		double millis = laps.values().stream()
				.filter(s -> !s.isPitStop())
				.filter(s-> !s.isUnclean())
				.filter(s-> !s.isOutLap())
				.mapToDouble(s -> s.getLapTime().toMillis())
				.max().orElse(0.0D);
		return Duration.ofMillis((long) millis);
	}

	public Estimation getCurrentDriverEstimation() {
		if (racePlan != null && sessionToD != null) {
			LocalDateTime todStartTime = racePlan.getPlanParameters().getTodStartTime();
			LocalDate todDate = todStartTime.toLocalDate();
			if (sessionToD.isBefore(todStartTime.toLocalTime())) {
				todDate = todDate.plusDays(1);
			}
			return PlanningTools.getDriverEstimationAt(racePlan.getPlanParameters(), currentDriver, LocalDateTime.of(todDate, sessionToD));
		}
		return null;
	}

	public double getMaxCarFuel() {
		if (racePlan != null) {
			return racePlan.getPlanParameters().getMaxCarFuel();
		}
		return sessionData.getMaxCarFuel();
	}

	private Optional<LapData> getPreviousLap(int currentLapNo) {
		if (laps.isEmpty() || currentLapNo <= 1) {
			return Optional.empty();
		}

		return Optional.ofNullable(laps.get(currentLapNo - 1));
	}

	private void setStintValuesToLap(LapData newLap) {
		Optional<LapData> lastRecordedLap = getLastRecordedLap();
		if (lastRecordedLap.isEmpty()) {
			newLap.setStint(1);
			newLap.setStintLap(1);
		} else {
			if (!lastRecordedLap.get().isPitStop()) {
				newLap.setStint(lastRecordedLap.get().getStint());
				newLap.setStintLap(lastRecordedLap.get().getStintLap() + 1);
			} else {
				log.debug("Last recorded lap was pit lap, starting new stint");
				newLap.setStint(lastRecordedLap.get().getStint() + 1);
				newLap.setStintLap(1);
			}
		}
	}

	private void updateStint(Stint currentStint, LapData newLap) {
		final int stintNo = currentStint.getNo();
		if (currentStint.getDriver() == null) {
			currentStint.setDriver(newLap.getDriver());
		}
		currentStint.increaseLapCount();
		currentStint.setLastLaptime(newLap.getLapTime().isZero() ? Duration.ofSeconds(0) : newLap.getLapTime());
		currentStint.setLastLapFuel(newLap.getLastLapFuelUsage() > 0.0
				? newLap.getLastLapFuelUsage() : 0.0);
		try {
			currentStint.setAvgFuelPerLap(calculateAvgFuelPerLap(stintNo));
		} catch (NoSuchElementException e) {
			currentStint.setAvgFuelPerLap(newLap.getLastLapFuelUsage() > 0.0
					? newLap.getLastLapFuelUsage() : 0.0);
		}
		try {
			currentStint.setAvgTrackTemp(calculateAvgTrackTemp(stintNo));
		} catch (NoSuchElementException e) {
			currentStint.setAvgTrackTemp(newLap.getTrackTemp());
		}
		try {
			currentStint.setAvgLapTime(TimeTools.getAverageLapDuration(
					laps.values().stream()
							.filter(s -> s.getStint() == stintNo)
							.filter(s -> !s.getLapTime().isZero())
							.filter(s -> !s.isPitStop())
							.filter(s -> !s.isOutLap())
			));
		} catch (NoSuchElementException e) {
			currentStint.setAvgLapTime(newLap.getLapTime());
		}
		currentStint.addStintDuration(newLap.getLapTime());
		currentStint.setTodEnd(sessionToD);
	}

	private double calculateAvgFuelPerLap(int stintNo) {
		return laps.values().stream()
				.filter(s -> s.getStint() == stintNo)
				.filter(s -> !s.getLapTime().isZero())
				.filter(s -> s.getLastLapFuelUsage() > 0.0)
				.mapToDouble(LapData::getLastLapFuelUsage)
				.average().orElse(0.001D);
	}

	private double calculateAvgTrackTemp(int stintNo) {
		return laps.values().stream()
				.filter(s -> s.getStint() == stintNo)
				.filter(s -> s.getTrackTemp() > 0.0)
				.mapToDouble(LapData::getTrackTemp)
				.average().orElse(0.001D);
	}

	private void calculateStintLaps(Stint stint, double currentFuelLevel, Assumption assumption) {

		double avgLapFuelUsage = stint.getAvgFuelPerLap();
		double carFuel = sessionData.getMaxCarFuel();
		if (avgLapFuelUsage == 0.0D) {
			if (assumption == null) {
				log.warn("No avgLapFuelPerLap assumption found for {} in {} on {}", stint.getDriver(), sessionData.getCarName(),
						sessionData.getTrackName());
				return;
			} else {
				avgLapFuelUsage = assumption.getAvgFuelPerLap().orElse(stint.getAvgFuelPerLap());
				carFuel = assumption.getCarFuel();
			}
		}
		stint.setMaxLaps(carFuel / avgLapFuelUsage);
		stint.setAvailableLaps(currentFuelLevel / avgLapFuelUsage);
	}

	private void calculateExpectedStintDuration(Stint stint, Assumption assumption) {

		Duration avgLapTime = stint.getAvgLapTime();

		if (avgLapTime.isZero()) {
			if (assumption != null) {
				avgLapTime = assumption.getAvgLapTime().orElse(Duration.ZERO);
			} else {
				log.warn("No avgLapTime assumption found for {} in {} on {}", stint.getDriver(), sessionData.getCarName(),
						sessionData.getTrackName());
			}
		}

		stint.setExpectedStintDuration(avgLapTime.multipliedBy((long) Math.floor(stint.getMaxLaps())));
	}

	private Pitstop getOrCreateNextPitstop() {
		try {
			Integer lastPitStopKey = pitStops.lastKey();
			Pitstop lastPitStop = pitStops.get(lastPitStopKey);
			if (!lastPitStop.isComplete()) {
				return lastPitStop;
			} else {
				return Pitstop.builder()
						.stint(pitStops.get(lastPitStopKey).getStint() + 1)
						.driver(currentDriver.getName())
						.lap(currentLapNo)
						.build();
			}
		} catch (NoSuchElementException e) {
			return Pitstop.builder()
					.stint(1)
					.driver(currentDriver.getName())
					.lap(currentLapNo)
					.build();

		}
	}

	private Assumption getAssumptionFromRacePlan() {
		LocalDateTime atTod = racePlan == null ? null : racePlan.getTodRaceTime(getCurrentSessionTime());
		Estimation estimation = null;
		if (atTod != null) {
			LocalDateTime todDateTime = LocalDateTime.of(atTod.toLocalDate(), sessionToD != null ? sessionToD : LocalTime.MIN);
			estimation = racePlan == null ? null : PlanningTools.getDriverEstimationAt(racePlan.getPlanParameters(), currentDriver, todDateTime);
		}
		return Assumption.builder()
				.avgFuelPerLap(Optional.ofNullable(estimation == null ? null : estimation.getAvgFuelPerLap()))
				.avgLapTime(Optional.ofNullable(estimation == null ? null : estimation.getAvgLapTime()))
				.carFuel(racePlan == null ? 0.0D : racePlan.getPlanParameters().getMaxCarFuel())
				.build();
	}

	private Stint processOnTrackEvent(EventData eventData) {
		if (!pitStops.isEmpty()
				&& pitStops.get(pitStops.lastKey()).update(eventData,
				runData != null ? runData.getFuelLevel() : 0.0D)) {
			log.debug("Pitstop completed: {}", pitStops.get(pitStops.lastKey()));
			markPitstopLap();
			onOutLap = true;
			Optional<Stint> lastStint = getLastStint();
			lastStint.ifPresent(stint -> stint.setEndTime(ZonedDateTime.now()));
			lastStint.ifPresent(stint -> stints.put(stint.getNo() + 1, Stint.builder()
					.no(stint.getNo() + 1)
					.todStart(sessionToD)
					.build()
			));
			return lastStint.orElse(null);
		}
		return null;
	}

	private void processPitStallEvent(EventData eventData) {
		if (pitStops.isEmpty()) {
			log.warn("No pitstop while in pit stall");
		} else {
			log.debug("Update pitstop (PIT_STALL)");
			pitStops.get(pitStops.lastKey()).update(eventData, runData != null ? runData.getFuelLevel() : 0.0D);
		}
		repairTimeLeft = eventData.getRepairTime();
		optRepairTimeLeft = eventData.getOptRepairTime();
		towTimeLeft = eventData.getTowingTime();
	}

	private void processApproachingPitsEvent(EventData eventData) {
		if (currentTrackLocation.equals(TrackLocationType.PIT_STALL) && pitStops.isEmpty()) {
			log.debug("Starting from box");
			onOutLap = true;
			return;
		}
		Pitstop newPitStop = getOrCreateNextPitstop();
		newPitStop.update(eventData, runData != null ? runData.getFuelLevel() : 0.0D);
		pitStops.put(newPitStop.getStint(), newPitStop);
	}

	private void processOffTrackEvent(EventData eventData) {
		if (!eventData.getTowingTime().isZero()) {
			Pitstop towPitStop = getOrCreateNextPitstop();
			towPitStop.update(eventData, runData != null ? runData.getFuelLevel() : 0.0D);
			pitStops.put(towPitStop.getStint(), towPitStop);
		}
		uncleanLap = true;
		towTimeLeft = eventData.getTowingTime();
	}

	private void markPitstopLap() {
		Optional<LapData> lastRecordedLap = getLastRecordedLap();
		if (lastRecordedLap.isPresent()) {
			log.debug("Set pit stop flag to lap {}", lastRecordedLap.get().getNo());
			lastRecordedLap.get().setPitStop(true);
		}
	}
}
