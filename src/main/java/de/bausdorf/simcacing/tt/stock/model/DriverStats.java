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

import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.bausdorf.simcacing.tt.util.MapTools;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverStats {

	public static final String DRIVER_ID = "driverId";
	public static final String TRACK_ID = "trackId";
	public static final String CAR_ID = "carId";
	public static final String STATS = "stats";

	private String driverId;
	private String trackId;
	private String carId;
	private List<StatsEntry> stats;

	public String id() {
		return buildId(driverId, trackId, carId);
	}

	public DriverStats(Map<String, Object> data) {
		this.driverId = MapTools.stringFromMap(DRIVER_ID, data);
		this.trackId = MapTools.stringFromMap(TRACK_ID, data);
		this.carId = MapTools.stringFromMap(CAR_ID, data);
		this.stats = MapTools.mapListFromMap(STATS, data).stream()
				.map(StatsEntry::new)
				.collect(Collectors.toList());
	}

	public Map<String, Object> toMap() {
		Map<String, Object> data = new HashMap<>();
		data.put(DRIVER_ID, driverId);
		data.put(TRACK_ID, trackId);
		data.put(CAR_ID, carId);
		data.put(STATS, stats.stream()
				.map(StatsEntry::toMap)
				.collect(Collectors.toList())
		);
		return data;
	}

	public static String buildId(String driver, String track, String car) {
		return new StringBuilder(driver)
				.append('-').append(track)
				.append('-').append(car)
				.toString();
	}

	public StatsEntry getFastestEntry() {
		return stats.stream()
				.filter(s -> s.getAvgLapTime() != Duration.ZERO)
				.min(Comparator.comparing(StatsEntry::getAvgLapTime))
				.orElse(null);
	}
}
