package de.bausdorf.simcacing.tt.planning.model;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 - 2021 bausdorf engineering
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
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.bausdorf.simcacing.tt.schedule.model.RaceEvent;
import de.bausdorf.simcacing.tt.schedule.RaceEventRepository;
import de.bausdorf.simcacing.tt.util.AbstractIntegrationTest;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class RaceEventIT extends AbstractIntegrationTest {

	public static final String SERIES = "2021-1#iEEC";
	public static final String NAME = "iEEC";
	public static final String SEASON = "2021-1";

	@Autowired
	RaceEventRepository eventRepository;

	@Test
	void eventCrudTest() {
		RaceEvent event = createRaceEvent();

		eventRepository.save(event);

		RaceEvent foundEvent = eventRepository
				.findRaceEventBySeriesAndSeasonAndName(SERIES, SEASON, NAME).orElse(null);

		eventRepository.delete(foundEvent);
	}

	private RaceEvent createRaceEvent() {
		return RaceEvent.builder()
				.series(SERIES)
				.name(NAME)
				.season(SEASON)
				.carIds(Arrays.asList("1", "2", "3"))
				.raceSessionOffset(Duration.ofMinutes(13).toString())
				.sessionDateTime(OffsetDateTime.now())
				.simDateTime(LocalDateTime.now())
				.eventId(1L)
				.build();
	}
}
