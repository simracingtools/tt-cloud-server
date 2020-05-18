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
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class IRacingTrack {

	public static final String ID = "ID";
	public static final String NAME = "Name";
	public static final String COST = "Cost";
	public static final String SURFACE = "Surface";
	public static final String NIGHT_LIGHTING = "NightLighting";
	public static final String NOMINAL_LAP_TIME = "NominalLapTime";
	public static final String TRACK_TYPE = "TrackType";

	private String id;				// 341
	private String name;			// Silverstone Circuit Grand Prix
	private String cost;			// Paid
	private String surface;			// Paved
	private String nightLighting;	// No
	private String nominalLapTime;	// 2:19.831
	private String trackType;		// Road

	public Map<String, Object> toMap() {
		Map<String, Object> trackMap = new HashMap<>();
		trackMap.put(ID, id);
		trackMap.put(NAME, name);
		trackMap.put(COST, cost);
		trackMap.put(SURFACE, surface);
		trackMap.put(NIGHT_LIGHTING, nightLighting);
		trackMap.put(NOMINAL_LAP_TIME, nominalLapTime);
		trackMap.put(TRACK_TYPE, trackType);
		return trackMap;
	}
}
