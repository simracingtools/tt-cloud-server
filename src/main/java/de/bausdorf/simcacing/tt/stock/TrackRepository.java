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

import java.util.*;
import java.util.stream.Collectors;

import de.bausdorf.simcacing.tt.iracing.IRacingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.bausdorf.simcacing.tt.stock.model.IRacingTrack;

@Component
public class TrackRepository extends StockDataRepository {

	public TrackRepository(@Autowired IRacingClient db) {
		super(db);
	}

	public Optional<IRacingTrack> findById(String id) {

		return Arrays.stream(stockDataCache.getTracks()).filter(t -> t.getTrackId() == Long.parseLong(id))
				.findFirst()
				.map(track -> IRacingTrack.builder()
						.id(track.getTrackId().toString())
						.name(track.getTrackName() + " - " + track.getConfigName())
						.trackType(track.getTrackTypes()[0].getTrackType())
						.nightLighting(track.getNightLighting().toString())
						.nominalLapTime(track.getNominalLapTime().toString())
						.cost("")
						.surface("")
						.build());
	}

	public List<IRacingTrack> loadAll() {
		return Arrays.stream(stockDataCache.getTracks())
				.map(track -> IRacingTrack.builder()
						.id(track.getTrackId().toString())
						.name(track.getTrackName() + (track.getConfigName() != null ? " - " + track.getConfigName() : ""))
						.trackType(track.getTrackTypes()[0].getTrackType())
						.nightLighting(track.getNightLighting().toString())
						.nominalLapTime(track.getNominalLapTime().toString())
						.cost("")
						.surface("")
						.build())
				.sorted(Comparator.comparing(IRacingTrack::getName))
				.collect(Collectors.toList());
	}
}
