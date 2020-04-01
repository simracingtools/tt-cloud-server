package de.bausdorf.simcacing.tt.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
