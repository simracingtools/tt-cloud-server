package de.bausdorf.simcacing.tt.live.impl;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.bausdorf.simcacing.tt.live.model.client.LapData;
import de.bausdorf.simcacing.tt.live.model.client.Pitstop;
import de.bausdorf.simcacing.tt.live.model.client.SessionData;
import de.bausdorf.simcacing.tt.live.model.client.TrackLocationType;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.util.FirestoreDB;
import de.bausdorf.simcacing.tt.util.MapTools;
import de.bausdorf.simcacing.tt.util.SimpleRepository;

@Component
public class ActiveSessionRepository extends SimpleRepository<SessionController> {

	public static final String COLLECTION_NAME = "ActiveSessions";

	public static final String SESSION_DATA = "sessionData";
	public static final String LAST_UPDATE = "lastUpdate";
	public static final String GREEN_FLAG_TIME = "greenFlagTime";
	public static final String LAPS = "laps";
	public static final String PIT_STOPS = "pitStops";
	public static final String CURRENT_DRIVER = "currentDriver";
	public static final String CURRENT_TRACK_LOCATION = "currentTrackLocation";
	public static final String TEAM_ID = "teamId";
	public static final String SESSION_TOD = "sessionToD";

	public ActiveSessionRepository(@Autowired FirestoreDB db) {
		super(db);
	}

	@Override
	protected SessionController fromMap(Map<String, Object> data) {
		if (data == null) {
			return null;
		}
		SessionData sessionData = new SessionData((Map<String, Object>)data.get(SESSION_DATA));
		SessionController controller = new SessionController(sessionData);
		controller.setTeamId(MapTools.stringFromMap(TEAM_ID, data));
		controller.setLastUpdate(Long.parseLong(MapTools.stringFromMap(LAST_UPDATE, data)));
		controller.setGreenFlagTime(MapTools.timeFromMap(GREEN_FLAG_TIME, data));
		controller.setSessionToD(MapTools.timeFromMap(SESSION_TOD, data));

		Map<String, Object> lapMap = (Map<String, Object>)data.get(LAPS);
		if (lapMap != null) {
			lapMap.values().stream()
					.map(s -> new LapData((Map<String, Object>)s))
					.sorted(Comparator.comparing(LapData::getNo))
					.forEach(controller::addLap);
		}
		Map<String, Object> stopMap = (Map<String, Object>)data.get(PIT_STOPS);
		if (stopMap != null) {
			stopMap.values().stream()
					.map(s -> new Pitstop((Map<String, Object>)s))
					.sorted(Comparator.comparing(Pitstop::getStint))
					.forEach(s -> controller.getPitStops().put(s.getStint(), s));
		}
		Map<String, Object> driverData = (Map<String, Object>)data.get(CURRENT_DRIVER);
		if (driverData != null) {
			controller.setCurrentDriver(IRacingDriver.builder()
					.id((String)driverData.get(IRacingDriver.I_RACING_ID))
					.name((String)driverData.get(IRacingDriver.NAME))
					.validated((Boolean)driverData.get(IRacingDriver.VALIDATED))
					.build());
		}
		String locationString = MapTools.stringFromMap(CURRENT_TRACK_LOCATION, data);
		controller.setCurrentTrackLocation(locationString != null ? TrackLocationType.valueOf(locationString) : null);
		return controller;
	}

	@Override
	protected Map<String, Object> toMap(SessionController object) {
		Map<String, Object> dbData = new HashMap<>();

		dbData.put(SESSION_DATA, object.getSessionData().toMap());
		dbData.put(TEAM_ID, object.getTeamId());
		dbData.put(LAST_UPDATE, Long.toString(object.getLastUpdate()));
		dbData.put(GREEN_FLAG_TIME, object.getGreenFlagTime() != null ? object.getGreenFlagTime().toString() : null);
		Map<String, Object> lapMap = new HashMap<>();
		for (LapData lap : object.getLaps().values()) {
			lapMap.put(Integer.toUnsignedString(lap.getNo()), lap.toMap());
		}
		dbData.put(LAPS, lapMap);
		Map<String, Object> stopMap = new HashMap<>();
		for (Pitstop stop : object.getPitStops().values()) {
			stopMap.put(Integer.toUnsignedString(stop.getStint()), stop.toMap());
		}
		dbData.put(PIT_STOPS, stopMap);
		dbData.put(CURRENT_DRIVER, object.getCurrentDriver() != null ? object.getCurrentDriver().toMap() : null);
		dbData.put(CURRENT_TRACK_LOCATION, object.getCurrentTrackLocation() != null ? object.getCurrentTrackLocation().name() : null);
		dbData.put(SESSION_TOD, object.getSessionToD() != null ? object.getSessionToD().toString() : null);
		return dbData;
	}

	public void saveGreenFlagTime(String sessionId, LocalTime greenFlagTime) {
		Map<String, Object> updates = new HashMap<>();
		updates.put(GREEN_FLAG_TIME, greenFlagTime.toString());
		updates.put(LAST_UPDATE, Long.toString(System.currentTimeMillis()));
		super.update(COLLECTION_NAME, sessionId, updates);
	}

	public void saveTrackLocation(String sessionId, TrackLocationType trackLocation) {
		Map<String, Object> updates = new HashMap<>();
		updates.put(CURRENT_TRACK_LOCATION, trackLocation.name());
		updates.put(LAST_UPDATE, Long.toString(System.currentTimeMillis()));
		super.update(COLLECTION_NAME, sessionId, updates);
	}

	public void saveCurrentDriver(String sessionId, IRacingDriver currentDriver) {
		Map<String, Object> updates = new HashMap<>();
		updates.put(CURRENT_DRIVER, currentDriver.toMap());
		updates.put(LAST_UPDATE, Long.toString(System.currentTimeMillis()));
		super.update(COLLECTION_NAME, sessionId, updates);
	}

	public void saveLap(String sessionId, LapData lap) {
		Map<String, Object> updates = new HashMap<>();
		updates.put(LAPS + "." + lap.getNo(), lap.toMap());
		updates.put(LAST_UPDATE, Long.toString(System.currentTimeMillis()));
		super.update(COLLECTION_NAME, sessionId, updates);
	}

	public void savePitstop(String sessionId, Pitstop pitstop) {
		Map<String, Object> updates = new HashMap<>();
		updates.put(PIT_STOPS + "." + pitstop.getStint(), pitstop.toMap());
		updates.put(LAST_UPDATE, Long.toString(System.currentTimeMillis()));
		super.update(COLLECTION_NAME, sessionId, updates);
	}

	public List<SessionController> loadAll() {
		return super.loadAll(COLLECTION_NAME);
	}

	public void save(SessionController object) {
		super.save(COLLECTION_NAME, object.getSessionData().getSessionId().toString(), object);
	}

	public void delete(String name) {
		super.delete(COLLECTION_NAME, name);
	}

	protected Optional<SessionController> findById(String key) {
		return super.findByName(COLLECTION_NAME, key);
	}
}
