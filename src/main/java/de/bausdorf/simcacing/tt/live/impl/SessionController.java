package de.bausdorf.simcacing.tt.live.impl;

import de.bausdorf.simcacing.tt.live.clientapi.DuplicateLapException;
import de.bausdorf.simcacing.tt.live.model.client.*;
import de.bausdorf.simcacing.tt.planning.model.Estimation;
import de.bausdorf.simcacing.tt.planning.model.RacePlan;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.util.TimeTools;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Getter
public class SessionController {
    @Setter
    private long lastUpdate;
    private final SessionData sessionData;
    @Setter
    private RacePlan racePlan;

    private LocalTime greenFlagTime;
    private final SortedMap<Integer, Stint> stints = new TreeMap<>();
    private final SortedMap<Integer, LapData> laps = new TreeMap<>();
    private final SortedMap<Integer, Pitstop> pitStops = new TreeMap<>();
    private final Map<String, SyncData> heartbeats = new HashMap<>();
    private RunData runData;

    private int currentLapNo;
    @Setter
    private IRacingDriver currentDriver;
    private TrackLocationType currentTrackLocation;
    private Duration repairTimeLeft;
    private Duration optRepairTimeLeft;
    private Duration towTimeLeft;
    private LocalTime sessionToD;

    public SessionController(SessionData sessionData) {
        this.sessionData = sessionData;
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
                    .build();
            log.debug("New Stint: {}", currentStint);
            stints.put(currentStint.getNo(), currentStint);
        } else {
            updateStint(currentStint, newLap);
        }

        Assumption assumption = getAssumptionFromRacePlan();
        calculateStintLaps(currentStint, runData.getFuelLevel(), assumption);
        calculateExpectedStintDuration(currentStint, assumption);
        log.debug("Current stint: {}", currentStint);
    }

    public void updateRunData(RunData runData) {
        this.runData = runData;
        this.sessionToD = runData.getSessionToD();
        if (greenFlagTime == null && runData.getFlags().contains(FlagType.GREEN)) {
            greenFlagTime = runData.getSessionTime();
        }
    }

    public void updateSyncData(SyncData syncData) {
        heartbeats.put(syncData.getClientId(), syncData);
    }

    public void processEventData(EventData eventData) {
        log.debug("New event: {}", eventData);

        switch (eventData.getTrackLocationType()) {
            case APPROACHING_PITS:
                if (currentTrackLocation.equals(TrackLocationType.PIT_STALL) && pitStops.isEmpty()) {
                    log.debug("Starting from box");
                    return;
                }
                Pitstop newPitStop = getOrCreateNextPitstop();
                newPitStop.update(eventData);
                pitStops.put(newPitStop.getStint(), newPitStop);
                break;
            case PIT_STALL:
                if (pitStops.isEmpty()) {
                    log.warn("No pitstop while in pit stall");
                } else {
                    pitStops.get(pitStops.lastKey()).update(eventData);
                }
                repairTimeLeft = eventData.getRepairTime();
                optRepairTimeLeft = eventData.getOptRepairTime();
                towTimeLeft = eventData.getTowingTime();
                break;
            case ONTRACK:
                if (!pitStops.isEmpty() && pitStops.get(pitStops.lastKey()).update(eventData)) {
                    log.debug("Pitstop completed: {}", pitStops.get(pitStops.lastKey()));
                    Optional<LapData> lastRecordedLap = getLastRecordedLap();
                    if( lastRecordedLap.isPresent() ) {
                        log.debug("Set pit stop flag to lap {}", lastRecordedLap.get().getNo());
                        lastRecordedLap.get().setPitStop(true);
                    }
                    Optional<Stint> lastStint = getLastStint();
                    lastStint.ifPresent(stint -> stints.put(stint.getNo() + 1, Stint.builder()
                            .no(stint.getNo() + 1)
                            .build()
                    ));
                }
                break;
            case OFF_WORLD:
            case OFFTRACK:
                if (!eventData.getTowingTime().isZero()) {
                    Pitstop towPitStop = getOrCreateNextPitstop();
                    towPitStop.update(eventData);
                    pitStops.put(towPitStop.getStint(), towPitStop);
                }
                towTimeLeft = eventData.getTowingTime();
        }
        sessionToD = eventData.getSessionToD();
        currentTrackLocation = eventData.getTrackLocationType();
    }

    public Duration getRemainingSessionTime() {
        Optional<LocalTime> sessionDuration = sessionData.getSessionDuration();
        if (sessionDuration.isPresent()) {
            if (greenFlagTime != null) {
                return Duration.between(runData.getSessionTime(), sessionDuration.get().plusSeconds(greenFlagTime.toSecondOfDay()));
            }
            return Duration.between(runData.getSessionTime(), sessionDuration.get());
        }
        return Duration.ZERO;
    }

    public LocalTime getCurrentSessionTime() {
        return runData == null ? LocalTime.MIN : runData.getSessionTime();
    }

    public LocalTime getCurrentRaceSessionTime() {
        if (runData == null) {
            return LocalTime.MIN;
        }
        return runData.getSessionTime().minusSeconds(
                greenFlagTime != null ? greenFlagTime.toSecondOfDay() : 0);
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

    public Optional<Stint> getLastStint() {
        if( stints.isEmpty() ) {
            return Optional.empty();
        }
        return Optional.of(stints.get(stints.lastKey()));
    }

    public Duration getCurrentStintTime() {
        Optional<Stint> lastStint = getLastStint();
        if (lastStint.isPresent()) {
            return lastStint.get().getCurrentStintDuration();
        }
        return Duration.ZERO;
    }

    public Duration getRemainingStintTime() {
        Optional<Stint> lastStint = getLastStint();
        if (lastStint.isPresent()) {
            return lastStint.get().getExpectedStintDuration().minus(lastStint.get().getCurrentStintDuration());
        }
        return Duration.ZERO;
    }

    public int getRemainingStintCount() {
        if (racePlan != null) {
            return racePlan.getCurrentRacePlan().size() - stints.size();
        }
        return 0;
    }

    public int getRemainingLapCount() {
        if (racePlan != null) {
            int lapCount = 0;
            for (de.bausdorf.simcacing.tt.planning.model.Stint stint : racePlan.getCurrentRacePlan()) {
                lapCount += stint.getLaps();
            }
            return lapCount - currentLapNo;
        }
        return 0;
    }
    private Optional<LapData> getPreviousLap(int currentLapNo) {
        if (laps.isEmpty() || currentLapNo <= 1) {
            return Optional.empty();
        }

        return Optional.of(laps.get(currentLapNo - 1));
    }

    private Optional<LapData> getLastRecordedLap() {
        if (laps.isEmpty()) {
            return Optional.empty();
        }
        Integer lastKey = laps.lastKey();
        return Optional.of(laps.get(lastKey));
    }

    private void setStintValuesToLap(LapData newLap) {
        Optional<LapData> lastRecordedLap = getLastRecordedLap();
        if (!lastRecordedLap.isPresent()) {
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
        if( currentStint.getDriver() == null ) {
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
            currentStint.setAvgLapTime(TimeTools.getAverageLapDuration(
                    laps.values().stream()
                            .filter(s -> s.getStint() == stintNo)
                            .filter(s -> !s.getLapTime().isZero())
            ));
        } catch (NoSuchElementException e) {
            currentStint.setAvgLapTime(newLap.getLapTime());
        }
        currentStint.addStintDuration(newLap.getLapTime());
    }

    private double calculateAvgFuelPerLap(int stintNo) {
        return laps.values().stream()
                .filter(s -> s.getStint() == stintNo)
                .filter(s -> !s.getLapTime().isZero())
                .filter(s -> s.getLastLapFuelUsage() > 0.0)
                .mapToDouble(LapData::getLastLapFuelUsage)
                .average().orElse(0.001D);
    }

    private void calculateStintLaps(Stint stint, double currentFuelLevel, Assumption assumption) {

        double avgLapFuelUsage = stint.getAvgFuelPerLap();
        double carFuel = sessionData.getMaxCarFuel();
        if (avgLapFuelUsage == 0.0D) {
            if (assumption == null) {
                log.warn("No avgLapFuelPerLap assumption found for {} in {} on {}", stint.getDriver(), sessionData.getCarName(), sessionData.getTrackName());
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
                log.warn("No avgLapTime assumption found for {} in {} on {}", stint.getDriver(), sessionData.getCarName(), sessionData.getTrackName());
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
    Estimation estimation = racePlan == null ? null : racePlan.getPlanParameters()
            .getDriverEstimationAt(currentDriver, LocalDateTime.of(atTod.toLocalDate(), sessionToD));
    return Assumption.builder()
            .avgFuelPerLap(Optional.ofNullable(estimation == null ? null : estimation.getAvgFuelPerLap()))
            .avgLapTime(Optional.ofNullable(estimation == null ? null : estimation.getAvgLapTime()))
            .carFuel(racePlan == null ? 0.0D : racePlan.getPlanParameters().getMaxCarFuel())
            .avgPitStopTime(Optional.ofNullable(racePlan == null ? null : racePlan.getPlanParameters().getAvgPitStopTime()))
            .build();
    }
}
