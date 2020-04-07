package de.bausdorf.simcacing.tt.planning.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RacePlanTest {

	@Test
	public void calculate_race_plan() {
		Estimation estimation = Estimation.builder()
				.avgFuelPerLap(2.2)
				.avgLapTime(Duration.ofMinutes(1).plusSeconds(45))
				.driverName(RacePlan.COMMON_ESTIMATION_KEY)
				.build();

		RacePlan plan = RacePlan.createRacePlanTemplate(
				"0815",
				Duration.ofHours(6),
				ZonedDateTime.of(LocalDateTime.of(2020, 4, 28, 13, 0), ZoneId.of("UTC")),
				115.5,
				3,
				estimation
				);

		plan.getCurrentRacePlan().stream().forEach(
				s -> log.info(s.toString())
		);
	}
}
