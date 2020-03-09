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
			data.put(teamId, sessionMap);
		}
		if( hasSession ) {
			SessionData sd = SessionData.builder()
					.carName((String)sessionMessage.get("car"))
					.maxFuel((Double)sessionMessage.get("maxFuel"))
					.sessionId(sessionId)
					.sessionLaps((String)sessionMessage.get("sessionLaps"))
					.sessionTime((String)sessionMessage.get("sessionTime"))
					.sessionType((String)sessionMessage.get("sessionType"))
					.teamName((String)sessionMessage.get("teamName"))
					.build();
			sessionMap.put(sessionId, sd);
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
}
