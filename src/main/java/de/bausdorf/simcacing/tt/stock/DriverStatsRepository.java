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

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.bausdorf.simcacing.tt.stock.model.DriverStats;
import de.bausdorf.simcacing.tt.stock.model.StatsEntry;
import de.bausdorf.simcacing.tt.util.CachedRepository;
import de.bausdorf.simcacing.tt.util.FirestoreDB;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DriverStatsRepository extends CachedRepository<DriverStats> {
	public static final String COLLECTION_NAME = "DriverStats";

	public DriverStatsRepository(@Autowired FirestoreDB db) {
		super(db);
	}

	@Override
	protected DriverStats fromMap(Map<String, Object> data) {
		if (data == null) {
			return null;
		}
		return new DriverStats(data);
	}

	@Override
	protected Map<String, Object> toMap(DriverStats object) {
		return object != null ? object.toMap() : null;
	}

	public Optional<DriverStats> findByDriverTrackCar(String driverId, String trackId, String carId) {
		return findById(DriverStats.buildId(driverId, trackId, carId));
	}

	public Optional<DriverStats> findById(String id) {
		return super.findByName(COLLECTION_NAME, id);
	}

	public void save(DriverStats stats) {
		super.save(COLLECTION_NAME, stats.id(), stats);
	}

	public void addStatsEntry(String driverId, String trackId, String carId, StatsEntry statsEntry) {
		Optional<DriverStats> stats = findByDriverTrackCar(driverId, trackId, carId);
		if (stats.isPresent()) {
			for (StatsEntry entry : stats.get().getStats()) {
				if (entry.equals(statsEntry)) {
					return;
				}
			}
			try {
				if (stats.get().getStats() != null) {
					stats.get().getStats().add(statsEntry);
				}
			} catch (UnsupportedOperationException e) {
				log.warn("Unsupported operation adding driver stats entry: {}", statsEntry);
			}
			save(stats.get());
		} else {
			save(DriverStats.builder()
					.driverId(driverId)
					.trackId(trackId)
					.carId(carId)
					.stats(Arrays.asList(statsEntry))
					.build()
			);
		}
	}

	public void delete(String id) {
		super.delete(COLLECTION_NAME, id);
	}
}
