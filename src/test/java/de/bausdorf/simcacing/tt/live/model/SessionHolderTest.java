package de.bausdorf.simcacing.tt.live.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import de.bausdorf.simcacing.tt.live.impl.SessionHolder;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class SessionHolderTest {

	@Autowired
	SessionHolder container;

	Map<String, Object> sessionMessage;

	@BeforeEach
	void createSessionMessage() {
		sessionMessage = new HashMap<>();
		sessionMessage.put("maxFuel", 120.0D);
		sessionMessage.put("sessionTime", "0.0792385731");
		sessionMessage.put("sessionLaps", "unlimited");
	}

}
