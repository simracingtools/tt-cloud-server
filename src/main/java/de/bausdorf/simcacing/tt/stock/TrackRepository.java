package de.bausdorf.simcacing.tt.stock;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.bausdorf.simcacing.tt.util.CacheEntry;
import de.bausdorf.simcacing.tt.util.FirestoreDB;
import de.bausdorf.simcacing.tt.stock.model.IRacingTrack;
import de.bausdorf.simcacing.tt.util.CachedRepository;

@Component
public class TrackRepository extends CachedRepository<IRacingTrack> {

	public static final String COLLECTION_NAME = "iRacingTracks";

	@Override
	protected IRacingTrack fromMap(Map<String, Object> data) {
		return IRacingTrack.builder()
				.id((String)data.get(IRacingTrack.ID))
				.name((String)data.get(IRacingTrack.NAME))
				.cost((String)data.get(IRacingTrack.COST))
				.nightLighting((String)data.get(IRacingTrack.NIGHT_LIGHTING))
				.nominalLapTime((String)data.get(IRacingTrack.NOMINAL_LAP_TIME))
				.surface((String)data.get(IRacingTrack.SURFACE))
				.trackType((String)data.get(IRacingTrack.TRACK_TYPE))
				.build();
	}

	@Override
	protected Map<String, Object> toMap(IRacingTrack object) {
		return object.toMap();
	}

	public Optional<IRacingTrack> findById(String id) {
		return super.findByName(COLLECTION_NAME, id);
	}

	public void save(IRacingTrack track) {
		super.save(COLLECTION_NAME, track.getId(), track);
	}

	public TrackRepository(@Autowired FirestoreDB db) {
		super(db);
	}

	public List<IRacingTrack> loadAll(boolean fromCache) {
		if (fromCache && !cache.isEmpty() ) {
			return cache.values().stream()
					.map(CacheEntry::getContent)
					.sorted(Comparator.comparing(IRacingTrack::getName))
					.collect(Collectors.toList());
		}
		List<IRacingTrack> allCars = super.loadAll(COLLECTION_NAME);
		allCars.stream().forEach(s -> putToCache(s.getId(), s));
		return allCars.stream()
				.sorted(Comparator.comparing(IRacingTrack::getName))
				.collect(Collectors.toList());
	}
}
