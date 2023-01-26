package de.bausdorf.simcacing.tt.live.model.iracing;

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
            Telemetry telemetry = messageReader.convertPayload(clientMessage, "telemetry", Telemetry.class);
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
            SessionData sessionData = messageReader.convertPayload(clientMessage, "sessionData", SessionData.class);
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
            SessionData sessionData = messageReader.convertPayload(clientMessage, null, SessionData.class);
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
                    SessionData sessionData = messageReader.convertPayload(clientMessage, null, SessionData.class);
                    assertNotNull(sessionData);
                    sessionDataCount++;
                    List<SessionData.Session> sessions = DataTools.lastSessionFirst(sessionData.getSessionInfo());
                    log.info("Current Session {}: lap {} standing {}", sessions.get(0).getSessionNum(), sessions.get(0).getResultsLapsComplete(), sessions.get(0).getResultsPositions());
                } else if (clientMessage.getType() == MessageType.TELEMETRY) {
                    Telemetry telemetry = messageReader.convertPayload(clientMessage, "telemetry", Telemetry.class);
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
