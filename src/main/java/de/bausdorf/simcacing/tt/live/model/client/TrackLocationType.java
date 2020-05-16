package de.bausdorf.simcacing.tt.live.model.client;

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

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
public enum TrackLocationType {
    OFF_WORLD(-1, "loc-black"),
    OFFTRACK(0, "loc-yellow"),
    PIT_STALL(1, "loc-blue"),
    APPROACHING_PITS(2, "loc-orange"),
    ONTRACK(3, "loc-green");

    TrackLocationType(int code, String cssClass) {
        this.cssClass = cssClass;
        this.irCode = code;
    }

    private int irCode;
    private String cssClass;

    public static TrackLocationType forIrCode(int code) {
        switch(code) {
            case -1: return OFF_WORLD;
            case 0: return OFFTRACK;
            case 1: return PIT_STALL;
            case 2: return APPROACHING_PITS;
            case 3: return ONTRACK;
            default: throw new IllegalArgumentException("iRacing track location " + code + " not valid");
        }
    }
}
