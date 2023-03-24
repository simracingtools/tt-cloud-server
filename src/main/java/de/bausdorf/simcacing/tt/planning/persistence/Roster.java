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

import de.bausdorf.simcacing.tt.planning.ScheduleDriverOptionType;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import lombok.*;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Roster {
    @Id
    @GeneratedValue
    private long id;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<IRacingDriver> drivers = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleEntry> driverAvailability = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Estimation> driverEstimations = new ArrayList<>();

    public Roster(Roster other) {
        this();
        this.id = other.getId();
        this.drivers.addAll(other.getDrivers());
        this.driverEstimations.addAll(other.getDriverEstimations());
        this.driverAvailability.addAll(other.getDriverAvailability());
    }

    public void updateDrivers(List<IRacingDriver> updatedDrivers, OffsetDateTime sessionStartTime) {
        drivers = updatedDrivers;
        List<String> updatedDriverIds = updatedDrivers.stream().map(IRacingDriver::getId).collect(Collectors.toList());
        driverAvailability.removeAll(driverAvailability.stream()
                .filter(e -> !updatedDriverIds.contains(e.getDriver().getId()))
                .collect(Collectors.toList())
        );
        driverEstimations.removeAll(driverEstimations.stream()
                .filter(e -> !updatedDriverIds.contains(e.getDriver().getId()))
                .collect(Collectors.toList())
        );

        drivers.forEach(driver -> {
            Optional<ScheduleEntry> driverSchedule = driverAvailability.stream()
                    .filter(s -> s.getDriver().getId().equalsIgnoreCase(driver.getId()))
                    .findFirst();
            driverSchedule.ifPresentOrElse(
                    s -> {},
                    () -> driverAvailability.add(ScheduleEntry.builder()
                        .fromTime(sessionStartTime)
                        .driver(driver)
                        .status(ScheduleDriverOptionType.OPEN)
                        .build())
            );
        });
    }
}
