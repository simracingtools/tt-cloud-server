package de.bausdorf.simcacing.tt.planning.persistence;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 bausdorf engineering
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.time.*;
import java.util.ArrayList;
import java.util.List;

import de.bausdorf.simcacing.tt.planning.RacePlan;
import de.bausdorf.simcacing.tt.planning.ScheduleDriverOptionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class RacePlanTest {

	private static final LocalDate START_DATE = LocalDate.parse("2020-01-01");
	private static final OffsetDateTime START_TIME = OffsetDateTime.of(START_DATE, LocalTime.parse("13:00"),
			ZoneId.of("GMT").getRules().getOffset(LocalDateTime.of(START_DATE, LocalTime.parse("13:00"))));
	private static final LocalDateTime START_TOD = LocalDateTime.of(START_DATE, LocalTime.parse("15:00"));

	PlanParameters planParameters;
	Roster roster;

	@BeforeEach
	public void setup() {
		planParameters = PlanParameters.builder()
				.avgFuelPerLap(16.8)
				.maxCarFuel(115.5)
				.avgLapTime(Duration.ofMinutes(8).plusSeconds(45))
				.avgPitLaneTime(Duration.ofMinutes(1))
				.carId(55L)
				.id("47110815")
				.name("Testplan")
				.raceDuration(Duration.ofHours(6))
				.sessionStartDateTime(START_TIME)
				.greenFlagOffsetTime(Duration.ZERO)
				.todStartTime(START_TOD)
				.teamId(4711L)
				.trackId(252L)
				.stints(new ArrayList<>())
				.build();

		roster = new Roster();
		roster.getDrivers().add(IRacingDriver.builder()
				.name("Robert")
				.id("11111")
				.build());
		roster.getDrivers().add(IRacingDriver.builder()
				.name("Dave")
				.id("22222")
				.build());
		roster.getDrivers().add(IRacingDriver.builder()
				.name("Sascha")
				.id("33333")
				.build());
		roster.getDriverEstimations().add(buildEstimation("Robert",
				Duration.ofMinutes(8).plusSeconds(45), 15.1,
				LocalDateTime.of(START_DATE, LocalTime.parse("15:00:00"))));
		roster.getDriverEstimations().add(buildEstimation("Robert",
				Duration.ofMinutes(9).plusSeconds(0), 14.9,
				LocalDateTime.of(START_DATE, LocalTime.parse("17:00:00"))));
		roster.getDriverAvailability().add(buildOpenScheduleEntry("Robert", START_TIME));
		roster.getDriverAvailability().add(buildOpenScheduleEntry("Dave", START_TIME));
		roster.getDriverAvailability().add(buildOpenScheduleEntry("Sascha", START_TIME));

		planParameters.setRoster(roster);
	}

	@Test
	void calculate_race_plan() {

		RacePlan plan = RacePlan.createRacePlanTemplate(planParameters);
		planParameters.setStints(plan.getCurrentRacePlan());

		plan.getCurrentRacePlan().stream().forEach(
				s -> log.info(s.toString())
		);
	}

	@Test
	void calculate_race_with_drivers_set_plan() {
		RacePlan plan = prepareAssignedRacePlan();

		plan.getCurrentRacePlan().stream().forEach(
				s -> log.info("{} - {}",s.getTodStartTime(), s.getDriverName())
		);
	}

	@Test
	void calculate_for_time_in_race() {
		RacePlan plan = prepareAssignedRacePlan();

		log.info("Base plan:");
		plan.getCurrentRacePlan().stream().forEach(
				s -> log.info("{} - {}",s.getTodStartTime(), s.getDriverName())
		);

		List<Stint> stints = plan.calculateLiveStints(
				START_TIME.plusHours(2).plusMinutes(30),
				START_TOD.plusHours(2).plusMinutes(30),
				START_TIME.plusHours(6));

		log.info("In-race plan:");
		stints.stream().forEach(
				s -> log.info("{} - {}",s.getTodStartTime(), s.getDriverName())
		);
	}

	@Test
	void calculate_for_different_session_start_time() {
		RacePlan plan = RacePlan.createRacePlanTemplate(planParameters);

		log.info("Base plan:");
		plan.getCurrentRacePlan().stream().forEach(
				s -> log.info("{} - {}",s.getTodStartTime(), s.getEndTime())
		);

		List<Stint> stints = plan.calculateLiveStints(
				planParameters.getSessionStartDateTime().plusHours(2).plusMinutes(30),
				planParameters.getSessionStartDateTime().plusHours(2).plusMinutes(30).toLocalDateTime(),
				planParameters.getSessionStartDateTime().plusHours(6));

		log.info("Changed start time plan:");
		stints.stream().forEach(
				s -> log.info("{} - {}",s.getTodStartTime(), s.getEndTime())
		);
	}

	private Estimation buildEstimation(String driverName, Duration avgLapTime, double avgFuelPerLap, LocalDateTime fromToD) {
		return Estimation.builder()
				.driver(roster.getDrivers().stream().filter(d -> d.getName().equalsIgnoreCase(driverName)).findFirst().orElse(null))
				.avgLapTime(avgLapTime)
				.avgFuelPerLap(avgFuelPerLap)
				.todFrom(fromToD)
				.build();
	}

	private ScheduleEntry buildOpenScheduleEntry(String driverName, OffsetDateTime from) {
		return ScheduleEntry.builder()
				.driver(roster.getDrivers().stream().filter(d -> d.getName().equalsIgnoreCase(driverName)).findFirst().orElse(null))
				.fromTime(from)
				.status(ScheduleDriverOptionType.OPEN)
				.build();
	}

	private void assignDrivers() {
		for (int i = 0; i < planParameters.getStints().size(); i++) {
			Stint stint = planParameters.getStints().get(i);
			String driverName = roster.getDrivers().get(i % 3).getName();
			stint.setDriverName(driverName);
		}
	}

	private RacePlan prepareAssignedRacePlan() {
		RacePlan plan = RacePlan.createRacePlanTemplate(planParameters);
		planParameters.setStints(plan.getCurrentRacePlan());

		assignDrivers();
		plan.calculateLiveStints();
		return plan;
	}
}
