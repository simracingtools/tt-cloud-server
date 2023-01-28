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

import de.bausdorf.simcacing.tt.planning.PitStopServiceType;
import de.bausdorf.simcacing.tt.util.OffsetDateTimeConverter;
import de.bausdorf.simcacing.tt.util.TimeTools;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity
public class Stint {
    @Id
    @GeneratedValue
    private long id;

    private String driverName;
    private LocalDateTime todStartTime;
    @Convert(converter = OffsetDateTimeConverter.class)
    private OffsetDateTime startTime;
    @Convert(converter = OffsetDateTimeConverter.class)
    private OffsetDateTime endTime;
    private double refuelAmount;
    private int laps;
    @ElementCollection
    private List<PitStopServiceType> service;
    private boolean lastStint;

    public void addService(PitStopServiceType serviceType) {
        if (service == null) {
            service = new ArrayList<>();
        }
        service.add(serviceType);
    }

    public void removeService(PitStopServiceType serviceType) {
        if (service != null) {
            service.remove(serviceType);
        }
    }

    public boolean hasService(PitStopServiceType serviceType) {
        if (service != null) {
            return service.contains(serviceType);
        }
        return false;
    }

    public void updateData(Stint newStintData) {
        // to update an existing entity
        driverName = newStintData.getDriverName();
        todStartTime = newStintData.getTodStartTime();
        startTime = newStintData.getStartTime();
        endTime = newStintData.getEndTime();
        refuelAmount = newStintData.getRefuelAmount();
        laps = newStintData.getLaps();
        lastStint = newStintData.isLastStint();
        service = newStintData.getService();
    }

    public void shiftTimezone(ZoneId zoneId) {
        startTime = TimeTools.shiftTimezone(startTime, zoneId);
        endTime = TimeTools.shiftTimezone(endTime, zoneId);
    }
}
