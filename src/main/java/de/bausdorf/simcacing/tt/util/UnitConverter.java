package de.bausdorf.simcacing.tt.util;

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

public class UnitConverter {

    public static final double KG_TO_L = 1.3564471935108;
    public static final double GAL_TO_L = 3.785411784;
    public static final double IGAL_TO_L = 4.5460902819948;

    private UnitConverter() {
        super();
    }

    public static double toLiters(double value, String unit) {
        switch(unit) {
            case "g": return value * GAL_TO_L;
            case "i": return value * IGAL_TO_L;
            case "k": return value * KG_TO_L;
            case "l": return value;
            default: throw new IllegalArgumentException("Unknown unit type: " + unit);
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
