package de.bausdorf.simcacing.tt.planning.persistence;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 - 2023 bausdorf engineering
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

import de.bausdorf.simcacing.tt.util.OffsetDateTimeConverter;
import de.bausdorf.simcacing.tt.util.TimeTools;
import lombok.*;

import javax.persistence.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class PlanParameters {
    @Id
    private String id;
    private String name;
    private long teamId;
    private long trackId;
    private long carId;
    private Duration raceDuration;
    @Convert(converter = OffsetDateTimeConverter.class)
    private OffsetDateTime sessionStartDateTime;
    private LocalDateTime todStartTime;
    private Duration greenFlagOffsetTime;
    private Duration avgLapTime;
    private Duration avgPitLaneTime;
    private Double avgFuelPerLap;
    private Double maxCarFuel;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Stint> stints;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "roster_id")
    private Roster roster;

    public PlanParameters(PlanParameters other) {
        this.name = other.name;
        this.id = other.id;
        this.teamId = other.teamId;
        this.trackId = other.trackId;
        this.carId = other.carId;
        this.raceDuration = other.raceDuration;
        this.sessionStartDateTime = other.sessionStartDateTime;
        this.todStartTime = other.todStartTime;
        this.greenFlagOffsetTime = other.greenFlagOffsetTime;
        this.avgLapTime = other.avgLapTime;
        this.avgPitLaneTime = other.avgPitLaneTime;
        this.avgFuelPerLap = other.avgFuelPerLap;
        this.maxCarFuel = other.maxCarFuel;
        this.stints = new ArrayList<>();
        for (Stint stint : other.stints) {
            this.stints.add(Stint.builder()
                    .todStartTime(stint.getTodStartTime())
                    .driverName(stint.getDriverName())
                    .startTime(stint.getStartTime())
                    .endTime(stint.getEndTime())
                    .laps(stint.getLaps())
                    .refuelAmount(stint.getRefuelAmount())
                    .service(stint.getService())
                    .id(stint.getId())
                    .build());
        }
        this.roster = new Roster(other.roster);
    }

    public void shiftTimezone(ZoneId zoneId) {
        sessionStartDateTime = TimeTools.shiftTimezone(sessionStartDateTime, zoneId);
        stints.forEach(stint -> stint.shiftTimezone(zoneId));
        roster.getDriverAvailability().forEach(a -> a.setFromTime(TimeTools.shiftTimezone(a.getFromTime(), zoneId)));
    }

    public void shiftSessionStartTime(ZonedDateTime shiftTo) {
        OffsetDateTime newStartTime = OffsetDateTime.of(shiftTo.toLocalDateTime(), shiftTo.getOffset());
        Duration timeShift = Duration.between(sessionStartDateTime, newStartTime);
        roster.getDriverAvailability().forEach(a -> a.setFromTime(a.getFromTime().plus(timeShift)));
        stints.forEach(stint -> stint.shitStartTime(timeShift));
        sessionStartDateTime = newStartTime;
    }

    public void updateStints(List<Stint> currentRacePlan) {
        List<Stint> updatedStints = new ArrayList<>();
        for (int i = 0; i < currentRacePlan.size(); i++) {
            Stint newData = currentRacePlan.get(i);
            if (i < stints.size()) {
                Stint toUpdate = stints.get(i);
                toUpdate.updateData(newData);
                updatedStints.add(toUpdate);
            } else {
                updatedStints.add(newData);
            }
        }
        stints.clear();
        stints.addAll(updatedStints);
    }
}
