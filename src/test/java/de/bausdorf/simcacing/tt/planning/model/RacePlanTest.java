package de.bausdorf.simcacing.tt.planning.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.bausdorf.simcacing.tt.planning.RacePlanRepository;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
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

	@Test
	public void calculate_race_with_drivers_set_plan() {
		RacePlanParameters params = RacePlanParameters.builder()
				.avgFuelPerLap(15.2)
				.maxCarFuel(115.5)
				.avgLapTime(Duration.ofMinutes(8).plusSeconds(15))
				.avgPitStopTime(Duration.ofMinutes(1).plusSeconds(15))
				.carId("55")
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

		Roster roster = new Roster();
		roster.addDriver(IRacingDriver.builder()
				.name("Robert")
				.id("11111")
				.build());
		roster.addDriver(IRacingDriver.builder()
				.name("Dave")
				.id("22222")
				.build());
		roster.addDriver(IRacingDriver.builder()
				.name("Sascha")
				.id("33333")
				.build());
		roster.addDriver(IRacingDriver.builder()
				.name("Thorben")
				.id("44444")
				.build());
		roster.addDriver(IRacingDriver.builder()
				.name("Jan")
				.id("55555")
				.build());
		roster.addEstimation(Estimation.builder()
				.driver(roster.getDriverByName("Robert"))
				.avgLapTime(Duration.ofMinutes(8).plusSeconds(45))
				.avgFuelPerLap(15.1)
				.todFrom(LocalDateTime.of(LocalDate.now(), LocalTime.parse("15:00:00")))
				.build());
		roster.addEstimation(Estimation.builder()
				.driver(roster.getDriverByName("Robert"))
				.avgLapTime(Duration.ofMinutes(9).plusSeconds(00))
				.avgFuelPerLap(14.9)
				.todFrom(LocalDateTime.of(LocalDate.now(), LocalTime.parse("22:00:00")))
				.build());

		params.setRoster(roster);

		for (int i = 0; i < params.getStints().size(); i = i+2) {
			Stint stint = params.getStints().get(i);
			String driverName = roster.getDrivers().get(i % 5).getName();
			stint.setDriverName(driverName);
			if (i+1 < params.getStints().size()) {
				stint = params.getStints().get(i + 1);
				stint.setDriverName(driverName);
			}
		}

		plan.calculateStints();

		plan.getCurrentRacePlan().stream().forEach(
				s -> log.info(s.shortInfo())
		);

		params.setId("testplan");
		planRepository.save(params);
	}
}
