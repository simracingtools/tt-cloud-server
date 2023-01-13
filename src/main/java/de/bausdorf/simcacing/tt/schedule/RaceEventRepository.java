package de.bausdorf.simcacing.tt.schedule;

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

import de.bausdorf.simcacing.tt.schedule.model.RaceEvent;
import org.springframework.data.repository.CrudRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface RaceEventRepository extends CrudRepository<RaceEvent, String> {

	List<RaceEvent> findAllBySessionDateTimeAfter(OffsetDateTime dateTime);
	List<RaceEvent> findAllBySeriesAndSessionDateTimeAfter(String series, OffsetDateTime date);

	Optional<RaceEvent> findRaceEventBySeriesAndSeasonAndName(String series, String season, String name);
	Optional<RaceEvent> findByEventId(Long eventId);

}
