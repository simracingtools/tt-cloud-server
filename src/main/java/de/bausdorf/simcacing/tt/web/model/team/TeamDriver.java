package de.bausdorf.simcacing.tt.web.model.team;

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

import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamDriver {
    private String name;
    private String id;
    private boolean validated;
    private boolean teamAdmin;
    private boolean owner;

    public TeamDriver(IRacingDriver iRacingDriver) {
        this.name = iRacingDriver.getName();
        this.id = iRacingDriver.getId();
        this.validated = iRacingDriver.isValidated();
        this.teamAdmin = false;
        this.owner = false;
    }
}
