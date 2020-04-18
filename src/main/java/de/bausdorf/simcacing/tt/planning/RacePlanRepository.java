package de.bausdorf.simcacing.tt.planning;

import de.bausdorf.simcacing.tt.planning.model.RacePlanParameters;
import de.bausdorf.simcacing.tt.util.FirestoreDB;
import de.bausdorf.simcacing.tt.util.TimeCachedRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class RacePlanRepository extends TimeCachedRepository<RacePlanParameters> {
    public static final String COLLECTION_NAME = "RacePlanParameters";

    public RacePlanRepository(@Autowired FirestoreDB db) {
        super(db, 720 * 60000);
    }

    @Override
    protected RacePlanParameters fromMap(Map<String, Object> data) {
        if( data == null) {
            return null;
        }
        return RacePlanParameters.builder()
                .id(stringFromMap(RacePlanParameters.ID, data))
                .name(stringFromMap(RacePlanParameters.NAME, data))
                .driverCount(((Long)data.get(RacePlanParameters.DRIVER_COUNT)).intValue())
                .raceDuration(durationFromMap(RacePlanParameters.RACE_DURATION, data))
                .sessionStartTime(timeFromMap(RacePlanParameters.SESSION_START_TIME, data))
                .teamId(stringFromMap(RacePlanParameters.TEAM_ID, data))
                .trackId(stringFromMap(RacePlanParameters.TRACK_ID, data))
                .carId(stringFromMap(RacePlanParameters.CAR_ID, data))
                .avgFuelPerLap(doubleFromMap(RacePlanParameters.AVG_FUEL_PER_LAP, data))
                .avgLapTime(durationFromMap(RacePlanParameters.AGV_LAP_TIME, data))
                .avgPitStopTime(durationFromMap(RacePlanParameters.AVG_PIT_STOP_TIME, data))
                .maxCarFuel(doubleFromMap(RacePlanParameters.MAX_CAR_FUEL, data))
                .greenFlagOffsetTime(timeFromMap(RacePlanParameters.GREEN_FLAG_OFFSET_TIME, data))
                .todStartTime(timeFromMap(RacePlanParameters.TOD_START_TIME, data))
                .build();
    }

    @Override
    protected Map<String, Object> toMap(RacePlanParameters object) {
        return object.toMap();
    }

    public void save(RacePlanParameters planParameters) {
        super.save(COLLECTION_NAME, planParameters.getId(), planParameters);
    }

    public List<RacePlanParameters> findByTeamIds(List<String> teamIds) {
        List<RacePlanParameters> plans = new ArrayList<>();
        for( String teamId : teamIds ) {
            plans.addAll(super.findByFieldValue(COLLECTION_NAME, RacePlanParameters.TEAM_ID, teamId));
        }
        return plans;
    }

    public Optional<RacePlanParameters> findById(String id) {
        return super.findByName(COLLECTION_NAME, id);
    }

    private String stringFromMap(String key, Map<String, Object> data) {
        try {
            return (String)data.get(key);
        } catch( Exception e ) {
            log.warn(e.getMessage());
        }
        return "";
    }

    private double doubleFromMap(String key, Map<String, Object> data) {
        try {
            return (Double)data.get(key);
        } catch( Exception e ) {
            log.warn(e.getMessage());
        }
        return 0.0D;
    }

    private Duration durationFromMap(String key, Map<String, Object> data) {
        try {
            return Duration.parse((String)data.get(key));
        } catch( Exception e ) {
            log.warn(e.getMessage());
        }
        return Duration.ZERO;
    }

    private LocalTime timeFromMap(String key, Map<String, Object> data) {
        try {
            return LocalTime.parse((String)data.get(key));
        } catch( Exception e ) {
            log.warn(e.getMessage());
        }
        return LocalTime.MIN;
    }
}
