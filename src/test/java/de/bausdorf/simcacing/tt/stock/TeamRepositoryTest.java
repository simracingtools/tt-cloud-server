package de.bausdorf.simcacing.tt.stock;

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

import static org.assertj.core.api.Assertions.assertThat;

import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.stock.model.IRacingTeam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
//@EnableJpaRepositories(basePackages = {"de.bausdorf.simracing.tt.stock"})
class TeamDriverRepositoryTest {

	@Autowired
	TeamRepository teamRepository;

	@Autowired
	DriverRepository driverRepository;

	IRacingDriver driver;
	IRacingTeam team;

	@BeforeEach
	public void setup() {
		driver = IRacingDriver.builder()
				.id("47110815")
				.name("John Doe")
				.build();

		team = IRacingTeam.builder()
				.id("47110815")
				.name("Jedi")
				.build();
	}

	@Test
	@Disabled("Manual test")
	void crudTestDrivers() {

		driverRepository.save(driver);

		Optional<IRacingDriver> fromRepo = driverRepository.findById(driver.getId());
		assertThat(fromRepo).isPresent();

		driverRepository.delete(driver);

		fromRepo = driverRepository.findById(driver.getId());
		assertThat(fromRepo).isNotPresent();
	}

	@Test
	@Disabled("Manual test")
	void crudTestTeams() {

		teamRepository.save(team);

		Optional<IRacingTeam> fromRepo = teamRepository.findById(team.getId());
		assertThat(fromRepo).isPresent();

		teamRepository.deleteById(team.getId());

		fromRepo = teamRepository.findById(team.getId());
		assertThat(fromRepo).isNotPresent();
	}
}
