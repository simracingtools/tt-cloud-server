package de.bausdorf.simcacing.tt.planning;

import de.bausdorf.simcacing.tt.planning.model.RacePlanParameters;
import de.bausdorf.simcacing.tt.util.FirestoreDB;
import de.bausdorf.simcacing.tt.util.TimeCachedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
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
                .id((String)data.get(RacePlanParameters.ID))
                .name((String)data.get(RacePlanParameters.NAME))
                .driverCount(((Long)data.get(RacePlanParameters.DRIVER_COUNT)).intValue())
                .raceDuration(Duration.parse((String)data.get(RacePlanParameters.RACE_DURATION)))
                .sessionStartTime(LocalTime.parse((String)data.get(RacePlanParameters.SESSION_START_TIME)))
                .teamId((String)data.get(RacePlanParameters.TEAM_ID))
                .trackId((String)data.get(RacePlanParameters.TRACK_ID))
                .carId((String)data.get(RacePlanParameters.CAR_ID))
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
}
