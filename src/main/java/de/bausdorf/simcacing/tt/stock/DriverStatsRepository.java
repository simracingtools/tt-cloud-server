package de.bausdorf.simcacing.tt.stock;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.bausdorf.simcacing.tt.stock.model.DriverStats;
import de.bausdorf.simcacing.tt.stock.model.StatsEntry;
import de.bausdorf.simcacing.tt.util.CachedRepository;
import de.bausdorf.simcacing.tt.util.FirestoreDB;

@Component
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
			stats.get().getStats().add(statsEntry);
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
