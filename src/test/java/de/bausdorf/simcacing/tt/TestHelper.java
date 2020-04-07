package de.bausdorf.simcacing.tt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.bausdorf.simcacing.tt.live.impl.ModelFactory;
import de.bausdorf.simcacing.tt.live.model.SessionData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestHelper {

	public static SessionData createSessionData(String sessionTime, double maxFuel) {
		return SessionData.builder()
				.teamName("FBPTeamRED")
				.sessionType("Race")
				.sessionTime(sessionTime)
				.sessionLaps("unlimitad")
				.sessionId(ModelFactory.parseClientSessionId("FBPTeamRED@615423#727234#2"))
				.maxCarFuel(maxFuel)
				.carName("bmw z4")
				.trackName("nuerburgring combined")
				.build();
	}

	public static List<String> getJsonMessages(String type) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("/messagedata/" + type)))) {
			List<String> messages = new ArrayList<>();
			while( br.ready() ) {
				messages.add(br.readLine());
			}
			return messages;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
}
