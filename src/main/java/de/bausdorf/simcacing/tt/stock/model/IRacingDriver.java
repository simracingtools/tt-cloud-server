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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class IRacingDriver {

	public static final String I_RACING_ID = "iRacingId";
	public static final String NAME = "Name";
	public static final String VALIDATED = "validated";

	private String name;
	private String id;
	private boolean validated;

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put(I_RACING_ID, id);
		map.put(NAME, name);
		map.put(VALIDATED, validated);
		return map;
	}

	public List<String> toNameIdList() {
		List<String> list = new ArrayList<>();
		list.add(name);
		list.add(id);
		return list;
	}
}
