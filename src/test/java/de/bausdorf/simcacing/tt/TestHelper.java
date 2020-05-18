package de.bausdorf.simcacing.tt;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 bausdorf engineering
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.bausdorf.simcacing.tt.live.impl.ModelFactory;
import de.bausdorf.simcacing.tt.live.model.client.SessionData;
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
