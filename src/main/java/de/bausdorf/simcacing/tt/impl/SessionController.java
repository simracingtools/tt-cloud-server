package de.bausdorf.simcacing.tt.impl;

import de.bausdorf.simcacing.tt.clientapi.DuplicateLapException;
import de.bausdorf.simcacing.tt.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Getter
public class SessionController {
    private SessionData sessionData;
    private AssumptionHolder assumptions;

    private LocalTime greenFlagTime;
    private SortedMap<Integer, Stint> stints = new TreeMap<>();
    private SortedMap<Integer, LapData> laps = new TreeMap<>();
    private SortedMap<Integer, Pitstop> pitstops = new TreeMap<>();
    private Map<String, SyncData> heartbeats = new HashMap<>();
    private RunData runData;

    private int currentLapNo;
    private String currentDriverName;
    private TrackLocationType currentTrackLocation;
    private Duration repairTimeLeft;
    private Duration optRepairTimeLeft;
    private Duration towTimeLeft;

    public SessionController(SessionData sessionData, AssumptionHolder assumptions) {
        this.sessionData = sessionData;
        this.assumptions = assumptions;
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
        currentDriverName = newLap.getDriver();
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
            final int stintNo = currentStint.getNo();
            if( currentStint.getDriver() == null ) {
                currentStint.setDriver(newLap.getDriver());
            }
            currentStint.increaseLapCount();
            currentStint.setLastLaptime(newLap.getLapTime().isZero() ? Duration.ofSeconds(0) : newLap.getLapTime());
            currentStint.setLastLapFuel(newLap.getLastLapFuelUsage() > 0.0
                    ? newLap.getLastLapFuelUsage() : 0.0);
            try {
                currentStint.setAvgFuelPerLap(laps.values().stream()
                        .filter(s -> s.getStint() == stintNo)
                        .filter(s -> !s.getLapTime().isZero())
                        .filter(s -> s.getLastLapFuelUsage() > 0.0)
                        .mapToDouble(s -> s.getLastLapFuelUsage())
                        .average().getAsDouble()
                );
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
        Assumption assumption = assumptions.getAssumption(
                sessionData.getTrackName(),
                sessionData.getCarName(),
                newLap.getDriver(),
                sessionData.getMaxCarFuel());
        calculateStintLaps(currentStint, runData.getFuelLevel(), assumption);
        calculateExpectedStintDuration(currentStint, assumption);
        log.debug("Current stint: {}", currentStint);
    }

    public void updateRunData(RunData runData) {
        this.runData = runData;
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
                if (currentTrackLocation.equals(TrackLocationType.PIT_STALL) && pitstops.isEmpty()) {
                    log.debug("Starting from box");
                    return;
                }
                Pitstop newPitStop = getOrCreateNextPitstop();
                newPitStop.update(eventData);
                pitstops.put(newPitStop.getStint(), newPitStop);
                break;
            case PIT_STALL:
                if (pitstops.isEmpty()) {
                    log.warn("No pitstop while in pit stall");
                } else {
                    pitstops.get(pitstops.lastKey()).update(eventData);
                }
                repairTimeLeft = eventData.getRepairTime();
                optRepairTimeLeft = eventData.getOptRepairTime();
                towTimeLeft = eventData.getTowingTime();
                break;
            case ONTRACK:
                if (!pitstops.isEmpty()) {
                    if (pitstops.get(pitstops.lastKey()).update(eventData)) {
                        log.debug("Pitstop completed: {}", pitstops.get(pitstops.lastKey()));
                        Optional<LapData> lastRecordedLap = getLastRecordedLap();
                        if( lastRecordedLap.isPresent() ) {
                            log.debug("Set pit stop flag to lap {}", lastRecordedLap.get().getNo());
                            lastRecordedLap.get().setPitStop(true);
                        }
                        Optional<Stint> lastStint = getLastStint();
                        if( lastStint.isPresent() ) {
                            stints.put(lastStint.get().getNo() + 1, Stint.builder()
                                    .no(lastStint.get().getNo() + 1)
                                    .build()
                            );
                        }
                    }
                }
                break;
            case OFF_WORLD:
            case OFFTRACK:
                if (!eventData.getTowingTime().isZero()) {
                    Pitstop towPitStop = getOrCreateNextPitstop();
                    towPitStop.update(eventData);
                    pitstops.put(towPitStop.getStint(), towPitStop);
                }
                towTimeLeft = eventData.getTowingTime();
        }
        currentTrackLocation = eventData.getTrackLocationType();
    }

    public Duration getRemainingSessionTime() {
        if (sessionData.getSessionDuration().isPresent()) {
            if (greenFlagTime != null) {
                return Duration.between(runData.getSessionTime(), sessionData.getSessionDuration().get().plusSeconds(greenFlagTime.toSecondOfDay()));
            }
            return Duration.between(runData.getSessionTime(), sessionData.getSessionDuration().get());
        }
        return Duration.ZERO;
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

    private Optional<Stint> getLastStint() {
        if( stints.isEmpty() ) {
            return Optional.empty();
        }
        return Optional.of(stints.get(stints.lastKey()));
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

    private void calculateStintLaps(Stint stint, double currentFuelLevel, Assumption assumption) {

        double avgLapFuelUsage = stint.getAvgFuelPerLap();
        double carFuel = sessionData.getMaxCarFuel();
        if (avgLapFuelUsage == 0.0D) {
            if (assumption == null) {
                log.warn("No avgLapFuelPerLap assumption found for {} in {} on {}", stint.getDriver(), sessionData.getCarName(), sessionData.getTrackName());
                return;
            } else {
                avgLapFuelUsage = assumption.getAvgFuelPerLap().isPresent()
                        ? assumption.getAvgFuelPerLap().get() : stint.getAvgFuelPerLap();
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
                avgLapTime = assumption.getAvgLapTime().isPresent() ?
                        assumption.getAvgLapTime().get() : Duration.ZERO;
            } else {
                log.warn("No avgLapTime assumption found for {} in {} on {}", stint.getDriver(), sessionData.getCarName(), sessionData.getTrackName());
            }
        }

        stint.setExpectedStintDuration(avgLapTime.multipliedBy((long) Math.floor(stint.getMaxLaps())));
    }

    private Pitstop getOrCreateNextPitstop() {
        try {
            Integer lastPitStopKey = pitstops.lastKey();
            Pitstop lastPitStop = pitstops.get(lastPitStopKey);
            if (!lastPitStop.isComplete()) {
                return lastPitStop;
            } else {
                return Pitstop.builder()
                        .stint(pitstops.get(lastPitStopKey).getStint() + 1)
                        .driver(currentDriverName)
                        .lap(currentLapNo)
                        .build();
            }
        } catch (NoSuchElementException e) {
            return Pitstop.builder()
                    .stint(1)
                    .driver(currentDriverName)
                    .lap(currentLapNo)
                    .build();

        }
    }
}
