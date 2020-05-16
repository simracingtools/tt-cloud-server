package de.bausdorf.simcacing.tt.live.impl.assumptions;

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

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@Data
public class TrackAssumption {
    String track;
    private List<CarAssumption> cars;

    public Optional<DriverAssumption> findByName(String carName, String driverName) {
        for (CarAssumption a : cars) {
            if (a.getCar().equalsIgnoreCase(carName)) {
                return a.findByName(driverName);
            }
        }
        return Optional.empty();
    }
}
