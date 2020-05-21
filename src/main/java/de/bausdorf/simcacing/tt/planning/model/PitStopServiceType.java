package de.bausdorf.simcacing.tt.planning.model;

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

import lombok.Getter;

@Getter
public enum PitStopServiceType {
	WS("W", 5),
	TYRES("T", 20),
	FUEL("F", 20),
	FR("FR", 0);

	private final String code;

	private PitStopServiceType(String code, long seconds) {
		this.code = code;
	}

	public static PitStopServiceType ofCode(String c) {
		switch(c) {
			case "W": return WS;
			case "T": return TYRES;
			case "F": return FUEL;
			case "FR": return FR;
			default: throw new IllegalArgumentException("Unknown PitStopServiceType code " + c);
		}
	}

	public static PitStopServiceType fromCheckId(String checkId) {
		switch (checkId) {
			case "pitServiceTyres": return TYRES;
			case "pitServiceFuel": return FUEL;
			case "pitServiceWs": return WS;
			default: return null;
		}
	}
}
