package de.bausdorf.simcacing.tt.live.impl;

import java.util.HashMap;

import de.bausdorf.simcacing.tt.live.clientapi.SessionKey;
import de.bausdorf.simcacing.tt.live.model.client.SessionData;

public class SessionMap extends HashMap<SessionKey, SessionController> {

	public boolean putSession(SessionKey sessionKey, SessionData sessionData) {
		if (containsKey(sessionKey)) {
			return false;
		}
		SessionController controller = new SessionController(sessionData);
		controller.setTeamId(sessionKey.getTeamId());
		put(sessionKey, controller);
		return true;
	}
}
