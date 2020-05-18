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
