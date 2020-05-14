package de.bausdorf.simcacing.tt.live.model.client;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import de.bausdorf.simcacing.tt.live.clientapi.ClientData;
import de.bausdorf.simcacing.tt.util.MapTools;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SessionData implements ClientData {

	private static final String UNLIMITED = "unlimited";
	public static final String SESSION_ID = "sessionId";
	public static final String TEAM_NAME = "teamName";
	public static final String CAR_NAME = "carName";
	public static final String CAR_ID = "carId";
	public static final String SESSION_LAPS = "sessionLaps";
	public static final String SESSION_TIME = "sessionTime";
	public static final String MAX_CAR_FUEL = "maxCarFuel";
	public static final String TRACK_NAME = "trackName";
	public static final String TRACK_ID = "trackId";
	public static final String SESSION_TYPE = "sessionType";

	private final SessionIdentifier sessionId;
	private final String teamName;
	private final String carName;
	private final String carId;
	private final String sessionLaps;
	private final String sessionTime;
	private final double maxCarFuel;
	private final String trackName;
	private final String trackId;
	private final String sessionType;

	public SessionData(Map<String, Object> data) {
		this.sessionId = SessionIdentifier.parse(MapTools.stringFromMap(SESSION_ID, data));
		this.teamName = MapTools.stringFromMap(TEAM_NAME, data);
		this.carId = MapTools.stringFromMap(CAR_ID, data);
		this.carName = MapTools.stringFromMap(CAR_NAME, data);
		this.sessionLaps = MapTools.stringFromMap(SESSION_LAPS, data);
		this.sessionTime = MapTools.stringFromMap(SESSION_TIME, data);
		this.maxCarFuel = MapTools.doubleFromMap(MAX_CAR_FUEL, data);
		this.trackName = MapTools.stringFromMap(TRACK_NAME, data);
		this.trackId = MapTools.stringFromMap(TRACK_ID, data);
		this.sessionType = MapTools.stringFromMap(SESSION_TYPE, data);
	}

	public Optional<LocalTime> getSessionDuration() {
		if( UNLIMITED.equalsIgnoreCase(sessionTime) ) {
			return Optional.empty();
		}
		double iRacingSecondOfDay = Double.parseDouble(sessionTime);
		return Optional.of(LocalTime.ofSecondOfDay((long)iRacingSecondOfDay));
	}

	public Optional<Integer> getSessionMaxLaps() {
		if( UNLIMITED.equalsIgnoreCase(sessionLaps) ) {
			return Optional.empty();
		}
		return Optional.of(Integer.parseInt(sessionTime));
	}

	public Map<String, Object> toMap() {
		Map<String, Object> dbData = new HashMap<>();
		dbData.put(SESSION_ID, sessionId.toString());
		dbData.put(TEAM_NAME, teamName);
		dbData.put(CAR_NAME, carName);
		dbData.put(CAR_ID, carId);
		dbData.put(SESSION_LAPS, this.sessionLaps);
		dbData.put(SESSION_TIME, sessionTime);
		dbData.put(MAX_CAR_FUEL, maxCarFuel);
		dbData.put(TRACK_NAME, trackName);
		dbData.put(TRACK_ID, trackId);
		dbData.put(SESSION_TYPE, sessionType);
		return dbData;
	}
}
