package de.bausdorf.simcacing.tt.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
@Getter
public class DataContainer {

	private Map<String, Map<String, SessionData>> data = new HashMap<>();

	public boolean addSession(String teamId, String sessionId, Map<String, Object> sessionMessage) {
//		_info['track'] = self.track
//		_info['sessionLaps'] = self.sessionLaps
//		_info['sessionTime'] = self.sessionTime
//		_info['sessionType'] = self.sessionType
//		_info['teamName'] = self.teamName
//		_info['car'] = self.car
//		_info['maxFuel'] = self.maxFuel

		Map<String, SessionData> sessionMap = data.get(teamId);
		boolean hasSession = false;
		if( sessionMap != null ) {
			if( sessionMap.containsKey(sessionId) ) {
				hasSession = true;
			}
		} else {
			sessionMap = new HashMap<>();
			sessionMap.put(sessionId, createSessionDataFromMap(sessionId, sessionMessage));
			data.put(teamId, sessionMap);
		}
		if( !hasSession ) {
			sessionMap.put(sessionId, createSessionDataFromMap(sessionId, sessionMessage));
		}
		return !hasSession;
	}

	public SessionData getSession(String teamId, String sessionId) {
		Map<String, SessionData> sessionMap = data.get(teamId);
		if( sessionMap != null ) {
			return sessionMap.get(sessionId);
		}
		throw new IllegalArgumentException("No session " + sessionId + " for team " + teamId);
	}

	private SessionData createSessionDataFromMap(String sessionId, Map<String, Object> sessionMessage) {

		Double maxFuel = (Double)sessionMessage.get("maxFuel");

		if( maxFuel == null ) {
			throw new IllegalArgumentException("no maxFuel in session");
		}

		String sessionTime = (String)sessionMessage.get("sessionTime");
		String sessionLaps = (String)sessionMessage.get("sessionLaps");

		if( sessionTime == null | sessionLaps == null ) {
			throw new IllegalArgumentException("sessionLaps or sessionTime has to be present");
		}

		return SessionData.builder()
				.carName((String)sessionMessage.get("car"))
				.maxFuel(maxFuel)
				.sessionId(sessionId)
				.sessionLaps(sessionLaps)
				.sessionTime(sessionTime)
				.sessionType((String)sessionMessage.get("sessionType"))
				.teamName((String)sessionMessage.get("teamName"))
				.build();

	}
}
