package de.bausdorf.simcacing.tt.planning.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.bausdorf.simcacing.tt.planning.RacePlanRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class RacePlanTest {

	@Autowired
	RacePlanRepository planRepository;

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
				.sessionStartTime(LocalDateTime.of(LocalDate.now(), LocalTime.parse("13:00:00")))
				.greenFlagOffsetTime(LocalTime.MIN)
				.todStartTime(LocalDateTime.of(LocalDate.now(), LocalTime.parse("15:00:00")))
				.teamId("4711")
				.trackId("252")
				.build();

		RacePlan plan = RacePlan.createRacePlanTemplate(params);
		params.setStints(plan.getCurrentRacePlan());

		plan.getCurrentRacePlan().stream().forEach(
				s -> log.info(s.toString())
		);

		params.setId("testplan");
		planRepository.save(params);
	}

	@Test
	void loadRacePlan() {
		Optional<RacePlanParameters> planParameters = planRepository.findById("testplan");
		if( planParameters.isPresent() ) {
			planParameters.get().getStints().stream().forEach(s -> log.info(s.toString()));
		}
	}
}
