package de.bausdorf.simcacing.tt.planning.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class RacePlanParameters {
    public static final String NAME = "name";
    public static final String TEAM_ID = "teamId";
    public static final String TRACK_ID = "trackId";
    public static final String CAR_ID = "carId";
    public static final String RACE_DURATION = "raceDuration";
    public static final String SESSION_START_TIME = "sessionStartTime";
    public static final String DRIVER_COUNT = "driverCount";
    public static final String ID = "id";
    public static final String AGV_LAP_TIME = "agvLapTime";
    public static final String AVG_PIT_STOP_TIME = "avgPitStopTime";
    public static final String AVG_FUEL_PER_LAP = "avgFuelPerLap";
    public static final String MAX_CAR_FUEL = "maxCarFuel";
    public static final String TOD_START_TIME = "todStartTime";
    public static final String GREEN_FLAG_OFFSET_TIME = "greenFlagOffsetTime";

    private String id;
    private String name;
    private String teamId;
    private String trackId;
    private String carId;
    private Duration raceDuration;
    private LocalTime sessionStartTime;
    private LocalTime todStartTime;
    private LocalTime greenFlagOffsetTime;
    private Integer driverCount;
    private Duration avgLapTime;
    private Duration avgPitStopTime;
    private double avgFuelPerLap;
    private double maxCarFuel;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(ID, id);
        map.put(NAME, name);
        map.put(TEAM_ID, teamId);
        map.put(TRACK_ID, trackId);
        map.put(CAR_ID, carId);
        map.put(RACE_DURATION, raceDuration.toString());
        map.put(SESSION_START_TIME, sessionStartTime.toString());
        map.put(DRIVER_COUNT, driverCount);
        map.put(AGV_LAP_TIME, avgLapTime.toString());
        map.put(AVG_PIT_STOP_TIME, avgPitStopTime.toString());
        map.put(AVG_FUEL_PER_LAP, avgFuelPerLap);
        map.put(MAX_CAR_FUEL, maxCarFuel);
        map.put(TOD_START_TIME, todStartTime.toString());
        map.put(GREEN_FLAG_OFFSET_TIME, greenFlagOffsetTime.toString());
        return map;
    }

    public void updateData(RacePlanParameters update) {
        if( update.getDriverCount() != null ) {
            driverCount = update.getDriverCount();
        }
        if( update.getAvgFuelPerLap() > 0 ) {
            avgFuelPerLap = update.getAvgFuelPerLap();
        }
        if( update.getAvgLapTime() != null ) {
            avgLapTime = update.getAvgLapTime();
        }
        if( update.getGreenFlagOffsetTime() != null ) {
            greenFlagOffsetTime = update.getGreenFlagOffsetTime();
        }
        if( update.getMaxCarFuel() > 0 ) {
            maxCarFuel = update.getMaxCarFuel();
        }
        if( update.getRaceDuration() != null ) {
            raceDuration = update.getRaceDuration();
        }
        if( update.getSessionStartTime() != null ) {
            sessionStartTime = update.getSessionStartTime();
        }
        if( update.getTeamId() != null ) {
            teamId = update.getTeamId();
        }
        if( update.getTodStartTime() != null ) {
            todStartTime = update.getTodStartTime();
        }
        if( update.getAvgPitStopTime() != null ) {
            avgPitStopTime = update.getAvgPitStopTime();
        }
        if( update.getCarId() != null ) {
            carId = update.getCarId();
        }
        if( update.getTrackId() != null ) {
            trackId = update.getTrackId();
        }
        if( update.getName() != null ) {
            name = update.getName();
        }
    }
}
