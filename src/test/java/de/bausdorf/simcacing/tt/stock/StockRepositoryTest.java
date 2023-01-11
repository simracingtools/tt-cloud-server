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

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.bausdorf.simcacing.tt.stock.model.IRacingCar;
import de.bausdorf.simcacing.tt.stock.model.IRacingTrack;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class StockRepositoryTest {

	@Autowired
	CarRepository carRepository;

	@Autowired
	TrackRepository trackRepository;

	@Test
	@Disabled
	void loadAllTracksFromRepository() {
		long loadStart = System.currentTimeMillis();
		List<IRacingTrack> allTracks = trackRepository.loadAll();
		long loadEnd = System.currentTimeMillis();
		assertThat(allTracks).isNotEmpty();
		log.info("Loading of {} tracks took {} ms", allTracks.size(), loadEnd - loadStart);

		allTracks.clear();

		loadStart = System.currentTimeMillis();
		allTracks = trackRepository.loadAll();
		loadEnd = System.currentTimeMillis();
		assertThat(allTracks).isNotEmpty();
		log.info("Loading of {} tracks took {} ms", allTracks.size(), loadEnd - loadStart);
	}

	@Test
	@Disabled
	void loadAllCarsFromRepository() {
		long loadStart = System.currentTimeMillis();
		List<IRacingCar> allTracks = carRepository.loadAll();
		long loadEnd = System.currentTimeMillis();
		assertThat(allTracks).isNotEmpty();
		log.info("Loading of {} cars took {} ms", allTracks.size(), loadEnd - loadStart);

		allTracks.clear();

		loadStart = System.currentTimeMillis();
		allTracks = carRepository.loadAll();
		loadEnd = System.currentTimeMillis();
		assertThat(allTracks).isNotEmpty();
		log.info("Loading of {} cars took {} ms", allTracks.size(), loadEnd - loadStart);
	}
}
