package de.bausdorf.simcacing.tt.stock.model;

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

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IRacingCar {

	public static final String NAME = "Name";
	public static final String ID = "ID";
	public static final String MAX_FUEL = "maxFuel";
	public static final String UNIT = "unit";

	private String id;
	private String name;
	private double maxFuel;
	private String unit;

	public Map<String, Object> toMap() {
		Map<String, Object> carMap = new HashMap<>();
		carMap.put(NAME, name);
		carMap.put(ID, id);
		carMap.put(MAX_FUEL, maxFuel);
		carMap.put(UNIT, unit);
		return carMap;
	}
}
