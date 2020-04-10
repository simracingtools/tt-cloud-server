package de.bausdorf.simcacing.tt.stock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.stock.model.IRacingTeam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
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
	@Disabled
	public void crudTestDrivers() {

		driverRepository.save(driver);

		Optional<IRacingDriver> fromRepo = driverRepository.findById(driver.getId());
		assertThat(fromRepo.isPresent()).isTrue();

		driverRepository.delete(driver.getId());

		fromRepo = driverRepository.findById(driver.getId());
		assertThat(fromRepo.isPresent()).isFalse();
	}

	@Test
	@Disabled
	public void crudTestTeams() {

		teamRepository.save(team);

		Optional<IRacingTeam> fromRepo = teamRepository.findById(team.getId());
		assertThat(fromRepo.isPresent()).isTrue();

		teamRepository.delete(team.getId());

		fromRepo = teamRepository.findById(team.getId());
		assertThat(fromRepo.isPresent()).isFalse();
	}
}