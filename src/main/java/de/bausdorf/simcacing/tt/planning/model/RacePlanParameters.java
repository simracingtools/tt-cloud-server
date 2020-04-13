package de.bausdorf.simcacing.tt.planning.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalTime;
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

    private String id;
    private String name;
    private String teamId;
    private String trackId;
    private String carId;
    private Duration raceDuration;
    private LocalTime sessionStartTime;
    private int driverCount;

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
        return map;
    }
}
