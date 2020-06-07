package de.bausdorf.simcacing.tt.planning;

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

import static de.bausdorf.simcacing.tt.util.MapTools.dateTimeFromMap;
import static de.bausdorf.simcacing.tt.util.MapTools.doubleFromMap;
import static de.bausdorf.simcacing.tt.util.MapTools.durationFromMap;
import static de.bausdorf.simcacing.tt.util.MapTools.stringFromMap;
import static de.bausdorf.simcacing.tt.util.MapTools.stringListFromMap;
import static de.bausdorf.simcacing.tt.util.MapTools.zonedDateTimeFromMap;

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

import java.time.Duration;
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
        RacePlanParameters planParameters = RacePlanParameters.builder()
                .id(stringFromMap(RacePlanParameters.ID, data))
                .name(stringFromMap(RacePlanParameters.NAME, data))
                .raceDuration(durationFromMap(RacePlanParameters.RACE_DURATION, data))
                .sessionStartTime(zonedDateTimeFromMap(RacePlanParameters.SESSION_START_TIME, data))
                .teamId(stringFromMap(RacePlanParameters.TEAM_ID, data))
                .trackId(stringFromMap(RacePlanParameters.TRACK_ID, data))
                .carId(stringFromMap(RacePlanParameters.CAR_ID, data))
                .avgFuelPerLap(doubleFromMap(RacePlanParameters.AVG_FUEL_PER_LAP, data))
                .avgLapTime(durationFromMap(RacePlanParameters.AGV_LAP_TIME, data))
                .avgPitLaneTime(durationFromMap(RacePlanParameters.AVG_PIT_STOP_TIME, data))
                .maxCarFuel(doubleFromMap(RacePlanParameters.MAX_CAR_FUEL, data))
                .greenFlagOffsetTime(durationFromMap(RacePlanParameters.GREEN_FLAG_OFFSET_TIME, data))
                .todStartTime(dateTimeFromMap(RacePlanParameters.TOD_START_TIME, data))
                .stints(stintsFromMap(data))
                .roster(new Roster((Map<String, Object>)data.get(RacePlanParameters.ROSTER)))
                .build();

        PlanningTools.updatePitLaneDurations(planParameters.getAvgPitLaneTime().dividedBy(2L),
                planParameters.getAvgPitLaneTime().dividedBy(2L), planParameters);

        return planParameters;
    }

    @Override
    protected Map<String, Object> toMap(RacePlanParameters object) {
        return object.toMap();
    }

    public void save(RacePlanParameters planParameters) {
        super.save(COLLECTION_NAME, planParameters.getId(), planParameters);
    }

    public void delete(String planId) {
        super.delete(COLLECTION_NAME, planId);
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

    private List<Stint> stintsFromMap(Map<String, Object> data) {
        SortedMap<Integer, Stint> stints = new TreeMap<>();
        try {
            Map<String, Object> stintsMap = (Map<String, Object>) data.get(RacePlanParameters.STINTS);
            for( Map.Entry<String, Object> stintEntry : stintsMap.entrySet() ) {
                Map<String, Object> stintMap = (Map<String, Object>)stintEntry.getValue();
                Stint stint = Stint.builder()
                        .driverName(stringFromMap(Stint.DRIVER_NAME, stintMap))
                        .startTime(zonedDateTimeFromMap(Stint.START_TIME, stintMap))
                        .todStartTime(dateTimeFromMap(Stint.TOD_START_TIME, stintMap))
                        .endTime(zonedDateTimeFromMap(Stint.END_TIME, stintMap))
                        .laps(((Long)stintMap.get(Stint.LAPS)).intValue())
                        .refuelAmount(doubleFromMap(Stint.REFUEL_AMOUNT, stintMap))
                        .pitStop(Optional.empty())
                        .build();

                List<String> pitService = stringListFromMap(Stint.PITSTOP_SERVICE, stintMap);
                if( pitService != null && !pitService.isEmpty() ) {
                    PitStop pitstop = PitStop.builder()
                            .approach(Duration.ZERO)
                            .depart(Duration.ZERO)
                            .service(new ArrayList<>())
                            .build();
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
