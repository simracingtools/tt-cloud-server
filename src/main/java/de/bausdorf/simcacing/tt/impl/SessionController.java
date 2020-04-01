package de.bausdorf.simcacing.tt.impl;

import de.bausdorf.simcacing.tt.model.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

@Slf4j
public class SessionController {
    private SessionData sessionData;
    private AssumptionHolder assumptions;

    private LocalTime greenFlagTime;
    private Map<Integer, Stint> stints = new TreeMap<>();
    private Map<Integer, LapData> laps = new TreeMap<>();
    private Map<String, SyncData> heartbeats = new HashMap<>();
    private RunData runData;

    public SessionController(SessionData sessionData, AssumptionHolder assumptions) {
        this.sessionData = sessionData;
        this.assumptions = assumptions;
        log.info("Created session controller for {}", sessionData.getSessionId());
    }

    public void addLap(LapData newLap) {

        if (laps.containsKey(newLap.getNo())) {
            throw new IllegalArgumentException("Lap " + newLap.getNo() + " already there");
        }
        double lastLapFuel = 0.0;
        Optional<LapData> lastLap = getPreviousLap(newLap.getNo());
        if (lastLap.isPresent()) {
            lastLapFuel = lastLap.get().getFuelLevel() - newLap.getFuelLevel();
        }
        newLap.setLastLapFuelUsage(lastLapFuel);
        setStintValuesToLap(newLap);

        laps.put(newLap.getNo(), newLap);

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
            stints.put(currentStint.getNo(), currentStint);
        } else {
            final int stintNo = currentStint.getNo();
            currentStint.setLastLaptime(newLap.getLapTime().isZero() ? Duration.ofSeconds(0) : newLap.getLapTime());
            currentStint.setLastLapFuel(newLap.getLastLapFuelUsage());
            currentStint.setAvgFuelPerLap(laps.values().stream()
                    .filter(s -> s.getStint() == stintNo)
                    .filter(s -> !s.getLapTime().isZero())
                    .mapToDouble(s -> s.getLastLapFuelUsage())
                    .average().getAsDouble()
            );
            currentStint.setAvgLapTime(TimeTools.getAverageLapDuration(
                    laps.values().stream()
                            .filter(s -> s.getStint() == stintNo)
                            .filter(s -> !s.getLapTime().isZero())
            ));
            currentStint.addStintDuration(newLap.getLapTime());
        }
        Assumption assumption = assumptions.getAssumption(
                sessionData.getTrackName(),
                sessionData.getCarName(),
                newLap.getDriver(),
                sessionData.getMaxCarFuel());
        calculateStintLaps(currentStint, runData.getFuelLevel(), assumption);
        calculateExpectedStintDuration(currentStint, assumption);
    }

    public void updateRunData(RunData runData) {
        this.runData = runData;
        if( greenFlagTime == null && runData.getFlags().contains(FlagType.GREEN)) {
            greenFlagTime = runData.getSessionTime();
        }
    }

    public void updateSyncData(SyncData syncData) {
        heartbeats.put(syncData.getClientId(), syncData);
    }

    public void processEventData(EventData eventData) {
        throw new UnsupportedOperationException("processEventData not implemented yet");
    }

    public Duration getRemainingSessionTime() {
        if (sessionData.getSessionDuration().isPresent()) {
            if (greenFlagTime != null ) {
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
        return Optional.of(laps.get(laps.size() - 1));
    }

    private void setStintValuesToLap(LapData newLap) {
        Optional<LapData> lastRecordedLap = getLastRecordedLap();
        if (!lastRecordedLap.isPresent()) {
            newLap.setStint(1);
            newLap.setStintLap(1);
        } else {
            if (lastRecordedLap.get().getDriver().equalsIgnoreCase(newLap.getDriver())) {
                newLap.setStint(lastRecordedLap.get().getStint());
                newLap.setStintLap(lastRecordedLap.get().getStintLap() + 1);
            } else {
                newLap.setStint(lastRecordedLap.get().getStint() + 1);
                newLap.setStintLap(1);
            }
        }
    }

    private void calculateStintLaps(Stint stint, double currentFuelLevel, Assumption assumption ) {

        double avgLapFuelUsage = stint.getAvgFuelPerLap();
        double carFuel = sessionData.getMaxCarFuel();
        if (avgLapFuelUsage == 0.0D ) {
            if(assumption == null) {
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

        if( avgLapTime.isZero() ) {
            if( assumption != null ) {
                avgLapTime = assumption.getAvgLapTime().isPresent() ?
                        assumption.getAvgLapTime().get() : Duration.ZERO;
            } else {
                log.warn("No avgLapTime assumption found for {} in {} on {}", stint.getDriver(), sessionData.getCarName(), sessionData.getTrackName());
            }
        }

        stint.setExpectedStintDuration(avgLapTime.multipliedBy((long)Math.floor(stint.getMaxLaps())));
    }
}
