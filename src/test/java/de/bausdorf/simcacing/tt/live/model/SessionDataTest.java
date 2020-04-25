package de.bausdorf.simcacing.tt.live.model;

import static org.assertj.core.api.Assertions.assertThat;

import de.bausdorf.simcacing.tt.live.model.client.SessionData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import de.bausdorf.simcacing.tt.TestHelper;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class SessionDataTest {

	private SessionData sessionData;

	@BeforeEach
	void prepareSessionData() {
		sessionData = TestHelper.createSessionData("0.75", 120.0D);
	}


	@Test
	void addStintLaps() {

	}
}
