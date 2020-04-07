package de.bausdorf.simcacing.tt.stock.model;

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
