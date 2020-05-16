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

import java.util.ArrayList;
import java.util.List;

public enum FlagType {
    CHECKERED(0x00000001L, Constants.WHITE),
    WHITE(0x00000002L, Constants.WHITE),
    GREEN(0x00000004L, Constants.GREEN),
    YELLOW(0x00000008L, Constants.YELLOW),
    RED(0x00000010L, Constants.RED),
    BLUE(0x00000020L, Constants.BLUE),
    DEBRIS(0x00000010L, Constants.YELLOW),
    CROSSED(0x00000080L, Constants.WHITE),
    YELLOW_WAVED(0x00000100L, Constants.YELLOW),
    ONE_TO_GREEN(0x00000200L, Constants.GREEN),
    GREEN_HELD(0x00000400L, Constants.GREEN),
    TEN_TO_GO(0x00000800L, Constants.WHITE),
    FIVE_TO_GO(0x00001000L, Constants.WHITE),
    RANDOM_WAVED(0x00002000L, Constants.WHITE),
    CAUTION(0x00004000L, Constants.YELLOW),
    CAUTION_WAVED(0x00008000L, Constants.YELLOW),
    BLACK(0x00010000L, Constants.BLACK),
    DQ(0x00020000L, Constants.DQ),
    SERVICEABLE(0x00040000L, ""),
    SERVICIBLE(0x00040000L, ""), // error - to be fixed in TT-Client
    FURLED(0x00080000L, ""),
    REPAIR(0x0010000L, Constants.REPAIR),
    START_HIDDEN(0x10000000L, ""),
    START_READY(0x20000000L, ""),
    START_SET(0x40000000L, "");

    long bitFlag;
    String cssClass;

    FlagType(long flag, String cssClass) {
        this.cssClass = cssClass;
        this.bitFlag = flag;
    }

    public String cssClass() {
        return cssClass;
    }

    public static List<FlagType> fromIrBitmask(long sessionFlags) {
        List<FlagType> flags = new ArrayList<>();

        for (FlagType flagType : FlagType.values()) {
            if ((flagType.bitFlag & sessionFlags) > 0) {
                flags.add(flagType);
            }
        }

        return flags;
    }

    private static class Constants {

        public static final String WHITE = "flag-white";
        public static final String BLACK = "flag-black";
        public static final String GREEN = "flag-green";
        public static final String YELLOW = "flag-yellow";
        public static final String RED = "flag-red";
        public static final String BLUE = "flag-blue";
        public static final String DQ = "flag-dq";
        public static final String REPAIR = "flag-repair";

    }
}
