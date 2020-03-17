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
	void addSessionLap() {
		Map<String, Object> lap = new HashMap<>();
		lap.put("lap", 1);
		lap.put("stintCount", 1);
		lap.put("stintLap", 1);
		lap.put("fuelLevel", 116.1D);
		lap.put("laptime", 0.0231D);
		lap.put("driver", "Driver Name");
		lap.put("sessionTime", 0.0891263D);
		lap.put("trackTemp", 27.8D);

		sessionData.addLap(lap);

		Stint stint = sessionData.getCurrentStint();

		assertThat(stint).isNotNull();
	}

	@Test
	void addStintLaps() {

	}
}
