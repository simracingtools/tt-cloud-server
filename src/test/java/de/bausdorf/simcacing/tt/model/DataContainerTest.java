package de.bausdorf.simcacing.tt.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class DataContainerTest {

	@Autowired
	DataContainer container;

	Map<String, Object> sessionMessage;

	@BeforeEach
	void createSessionMessage() {
		sessionMessage = new HashMap<>();
		sessionMessage.put("maxFuel", new Double(120.0D));
		sessionMessage.put("sessionTime", "0.0792385731");
		sessionMessage.put("sessionLaps", "unlimited");
	}

	@Test
	void addGetFirstSession() {
		container.addSession("47110815", "aSessionId", sessionMessage);

		SessionData sd = container.getSession("47110815", "aSessionId");

		assertThat(sd).isNotNull();
	}

	@Test
	void addGetSecondSession() {
		container.addSession("47110815", "aSessionId", sessionMessage);
		container.addSession("47110815", "aSecondSessionId", sessionMessage);

		SessionData sd = container.getSession("47110815", "aSecondSessionId");

		assertThat(sd).isNotNull();
	}
}
