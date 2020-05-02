package de.bausdorf.simcacing.tt.live.impl;

import java.util.HashMap;

import de.bausdorf.simcacing.tt.live.clientapi.SessionKey;
import de.bausdorf.simcacing.tt.live.model.client.SessionData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionMap extends HashMap<SessionKey, SessionController> {

	public boolean putSession(SessionKey sessionKey, SessionData sessionData) {
		if (containsKey(sessionKey)) {
			return false;
		}
		put(sessionKey, new SessionController(sessionData));
		return true;
	}
}
