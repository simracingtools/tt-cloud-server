package de.bausdorf.simcacing.tt.live.model.iracing;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 - 2023 bausdorf engineering
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

import de.bausdorf.simcacing.tt.iracing.model.SessionData;
import de.bausdorf.simcacing.tt.iracing.model.Telemetry;
import de.bausdorf.simcacing.tt.live.clientapi.ClientMessage;
import de.bausdorf.simcacing.tt.live.clientapi.MessageType;
import de.bausdorf.simcacing.tt.live.impl.transformers.ClientMessageReader;
import de.bausdorf.simcacing.tt.util.DataTools;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.*;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestPropertySource("file:./application.properties")
@SpringBootTest
@Slf4j
class JsonTest {

    @Autowired
    private ClientMessageReader messageReader;

    @Test
    void testReadTelemetry() {
        try (InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("telemetry.json")))) {
            ClientMessage clientMessage = messageReader.convertClientMessage(reader);
            assertNotNull(clientMessage);
            Telemetry telemetry = messageReader.convertPayload(clientMessage, Telemetry.class);
            assertNotNull(telemetry);
            assertNotEquals(0.0D, telemetry.getSessionTime());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
    @Test
    void testReadSessionData() {
        try (InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("sessiondata1.json")))) {
            ClientMessage clientMessage = messageReader.convertClientMessage(reader);
            assertNotNull(clientMessage);
            SessionData sessionData = messageReader.convertPayload(clientMessage, SessionData.class);
            assertNotNull(sessionData);
            assertNotNull(sessionData.getSessionInfo());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
    @Test
    void testReadSessionDataPartial() {
        try (InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("sessiondata.json")))) {
            ClientMessage clientMessage = messageReader.convertClientMessage(reader);
            assertNotNull(clientMessage);
            SessionData sessionData = messageReader.convertPayload(clientMessage, SessionData.class);
            assertNotNull(sessionData);
            assertNotNull(sessionData.getSessionInfo());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    @Test
    void testReadMqttLog() {
        try (BufferedReader reader = new BufferedReader(new FileReader("mqttlog.txt"))) {
            String line = reader.readLine();
            int sessionDataCount = 0;
            int telemetryCount = 0;
            long start = System.currentTimeMillis();
            while (line != null) {
                int delimPos = line.indexOf('#');
                ClientMessage clientMessage = messageReader.convertClientMessage(line.substring(delimPos + 1));
                assertNotNull(clientMessage);

                if (clientMessage.getType() == MessageType.SESSION_DATA) {
                    SessionData sessionData = messageReader.convertPayload(clientMessage, SessionData.class);
                    assertNotNull(sessionData);
                    sessionDataCount++;
                    List<SessionData.Session> sessions = DataTools.lastSessionFirst(sessionData.getSessionInfo());
                    int driverCarIdx = sessionData.getDriverInfo().getDriverCarIdx();
                    SessionData.Driver driver = sessionData.getDriverInfo().getDrivers().get(driverCarIdx);
                    log.info("Current Session {}: lap {} driver {}", sessions.get(0).getSessionNum(), sessions.get(0).getResultsLapsComplete(), driver.getUserName());
                } else if (clientMessage.getType() == MessageType.TELEMETRY) {
                    Telemetry telemetry = messageReader.convertPayload(clientMessage, Telemetry.class);
                    assertNotNull(telemetry);
                    telemetryCount++;
                }
                line = reader.readLine();
            }
            long duration = System.currentTimeMillis() - start;
            log.info("SessionData: {}, Telemetry: {}, readTime: {} s", sessionDataCount, telemetryCount, duration/1000);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
