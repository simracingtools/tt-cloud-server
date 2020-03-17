package de.bausdorf.simcacing.tt;

import java.util.ArrayList;

import de.bausdorf.simcacing.tt.model.SessionData;

public class TestHelper {

	public static SessionData createSessionData(String sessionTime, double maxFuel) {
		return SessionData.builder()
				.teamName("FBPTeamRED")
				.sessionType("Race")
				.sessionTime(sessionTime)
				.sessionLaps("unlimitad")
				.sessionId("FBPTeamRED@615423#727234#nuerburgring combined#2")
				.maxFuel(maxFuel)
				.carName("bmw z4")
				.trackName("nuerburgring combined")
				.laps(new ArrayList<>())
				.stints(new ArrayList<>())
				.build();
	}
}
