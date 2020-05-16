package de.bausdorf.simcacing.tt.stock.model;

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
}
