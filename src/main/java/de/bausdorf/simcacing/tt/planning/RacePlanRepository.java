package de.bausdorf.simcacing.tt.planning;

import static de.bausdorf.simcacing.tt.util.MapTools.dateTimeFromMap;
import static de.bausdorf.simcacing.tt.util.MapTools.doubleFromMap;
import static de.bausdorf.simcacing.tt.util.MapTools.durationFromMap;
import static de.bausdorf.simcacing.tt.util.MapTools.stringFromMap;
import static de.bausdorf.simcacing.tt.util.MapTools.stringListFromMap;
import static de.bausdorf.simcacing.tt.util.MapTools.timeFromMap;

import de.bausdorf.simcacing.tt.planning.model.PitStop;
import de.bausdorf.simcacing.tt.planning.model.PitStopServiceType;
import de.bausdorf.simcacing.tt.planning.model.RacePlanParameters;
import de.bausdorf.simcacing.tt.planning.model.Roster;
import de.bausdorf.simcacing.tt.planning.model.Stint;
import de.bausdorf.simcacing.tt.util.FirestoreDB;
import de.bausdorf.simcacing.tt.util.TimeCachedRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

@Component
@Slf4j
public class RacePlanRepository extends TimeCachedRepository<RacePlanParameters> {
    public static final String COLLECTION_NAME = "RacePlanParameters";

    public RacePlanRepository(@Autowired FirestoreDB db) {
        super(db, 720L * 60000L);
    }

    @Override
    protected RacePlanParameters fromMap(Map<String, Object> data) {
        if( data == null) {
            return null;
        }
        return RacePlanParameters.builder()
                .id(stringFromMap(RacePlanParameters.ID, data))
                .name(stringFromMap(RacePlanParameters.NAME, data))
                .raceDuration(durationFromMap(RacePlanParameters.RACE_DURATION, data))
                .sessionStartTime(dateTimeFromMap(RacePlanParameters.SESSION_START_TIME, data))
                .teamId(stringFromMap(RacePlanParameters.TEAM_ID, data))
                .trackId(stringFromMap(RacePlanParameters.TRACK_ID, data))
                .carId(stringFromMap(RacePlanParameters.CAR_ID, data))
                .avgFuelPerLap(doubleFromMap(RacePlanParameters.AVG_FUEL_PER_LAP, data))
                .avgLapTime(durationFromMap(RacePlanParameters.AGV_LAP_TIME, data))
                .avgPitStopTime(durationFromMap(RacePlanParameters.AVG_PIT_STOP_TIME, data))
                .maxCarFuel(doubleFromMap(RacePlanParameters.MAX_CAR_FUEL, data))
                .greenFlagOffsetTime(timeFromMap(RacePlanParameters.GREEN_FLAG_OFFSET_TIME, data))
                .todStartTime(dateTimeFromMap(RacePlanParameters.TOD_START_TIME, data))
                .stints(stintsFromMap(RacePlanParameters.STINTS, data))
                .roster(new Roster((Map<String, Object>)data.get(RacePlanParameters.ROSTER)))
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

    public List<RacePlanParameters> findByFieldValue(String fieldName, String fieldValue) {
        return super.findByFieldValue(COLLECTION_NAME, fieldName, fieldValue);
    }

    private List<Stint> stintsFromMap(String key, Map<String, Object> data) {
        SortedMap<Integer, Stint> stints = new TreeMap<>();
        try {
            Map<String, Object> stintsMap = (Map<String, Object>) data.get(key);
            for( Map.Entry<String, Object> stintEntry : stintsMap.entrySet() ) {
                Map<String, Object> stintMap = (Map<String, Object>)stintEntry.getValue();
                Stint stint = Stint.builder()
                        .driverName(stringFromMap(Stint.DRIVER_NAME, stintMap))
                        .startTime(dateTimeFromMap(Stint.START_TIME, stintMap))
                        .todStartTime(dateTimeFromMap(Stint.TOD_START_TIME, stintMap))
                        .endTime(dateTimeFromMap(Stint.END_TIME, stintMap))
                        .laps(((Long)stintMap.get(Stint.LAPS)).intValue())
                        .refuelAmount(doubleFromMap(Stint.REFUEL_AMOUNT, stintMap))
                        .pitStop(Optional.empty())
                        .build();

                List<String> pitService = stringListFromMap(Stint.PITSTOP_SERVICE, stintMap);
                if( pitService != null && !pitService.isEmpty() ) {
                    PitStop pitstop = PitStop.defaultPitStop();
                    pitstop.getService().clear();
                    pitService.forEach(s -> pitstop.addService(PitStopServiceType.valueOf(s)));
                    stint.setPitStop(Optional.of(pitstop));
                }

                stints.put(Integer.parseInt(stintEntry.getKey()), stint);
            }
        } catch( Exception e ) {
            log.warn(e.getMessage());
        }
        return new ArrayList<>(stints.tailMap(0).values());
    }
}
