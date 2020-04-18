package de.bausdorf.simcacing.tt.planning.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RacePlanTest {

	@Test
	public void calculate_race_plan() {
		RacePlanParameters params = RacePlanParameters.builder()
				.avgFuelPerLap(2.2)
				.maxCarFuel(115.5)
				.avgLapTime(Duration.ofMinutes(1).plusSeconds(45))
				.avgPitStopTime(Duration.ofMinutes(1))
				.carId("55")
				.driverCount(3)
				.id("47110815")
				.name("Testplan")
				.raceDuration(Duration.ofHours(24))
				.sessionStartTime(LocalTime.parse("13:00:00"))
				.teamId("4711")
				.trackId("252")
				.build();

		RacePlan plan = RacePlan.createRacePlanTemplate(params);

		plan.getCurrentRacePlan().stream().forEach(
				s -> log.info(s.toString())
		);
	}
}
