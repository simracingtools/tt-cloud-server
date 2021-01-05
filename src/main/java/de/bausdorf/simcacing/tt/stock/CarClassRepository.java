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

import de.bausdorf.simcacing.tt.stock.model.IRacingCarClass;
import de.bausdorf.simcacing.tt.util.CacheEntry;
import de.bausdorf.simcacing.tt.util.CachedRepository;
import de.bausdorf.simcacing.tt.util.FirestoreDB;
import de.bausdorf.simcacing.tt.util.MapTools;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CarClassRepository extends CachedRepository<IRacingCarClass> {
	public static final String COLLECTION_NAME = "iRacingCarsClasses";

	@Override
	protected IRacingCarClass fromMap(Map<String, Object> data) {
		if( data == null ) {
			return null;
		}
		return IRacingCarClass.builder()
				.name(MapTools.stringFromMap(IRacingCarClass.NAME, data))
				.id(MapTools.stringFromMap(IRacingCarClass.ID, data))
				.carIds(MapTools.stringListFromMap(IRacingCarClass.CAR_IDS, data))
				.build();
	}

	@Override
	protected Map<String, Object> toMap(IRacingCarClass object) {
		return object.toMap();
	}

	public CarClassRepository(@Autowired FirestoreDB db) {
		super(db);
	}

	public void save(IRacingCarClass carClass) {
		super.save(COLLECTION_NAME, carClass.getId(), carClass);
	}

	public Optional<IRacingCarClass> findByName(String id) {
		return super.findByName(COLLECTION_NAME, id);
	}

	public List<IRacingCarClass> loadAll(boolean fromCache) {
		if (fromCache && !cache.isEmpty() ) {
			return cache.values().stream()
					.map(CacheEntry::getContent)
					.sorted(Comparator.comparing(IRacingCarClass::getName))
					.collect(Collectors.toList());
		}
		List<IRacingCarClass> allCars = super.loadAll(COLLECTION_NAME);
		allCars.forEach(s -> putToCache(s.getId(), s));
		return allCars.stream()
				.sorted(Comparator.comparing(IRacingCarClass::getName))
				.collect(Collectors.toList());
	}
}
