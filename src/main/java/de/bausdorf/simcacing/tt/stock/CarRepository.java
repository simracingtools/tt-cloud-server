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
import de.bausdorf.simcacing.tt.util.MongoDB;
import de.bausdorf.simcacing.tt.stock.model.IRacingCar;
import de.bausdorf.simcacing.tt.util.CachedRepository;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CarRepository extends CachedRepository<IRacingCar> {
	public static final String COLLECTION_NAME = "iRacingCars";

	@Override
	protected IRacingCar fromMap(Map<String, Object> data) {
		if( data == null ) {
			return null;
		}
		return IRacingCar.builder()
				.name((String)data.get(IRacingCar.NAME))
				.id((String)data.get(IRacingCar.ID))
				.maxFuel(doubleFromNumber(data.get(IRacingCar.MAX_FUEL)))
				.unit((String)data.get(IRacingCar.UNIT))
				.build();
	}

	@Override
	protected Map<String, Object> toMap(IRacingCar object) {
		return object.toMap();
	}

	public CarRepository(@Autowired MongoDB db) {
		super(db);
	}

	public void save(IRacingCar car) {
		super.save(COLLECTION_NAME, car.getId(), car);
	}

	public Optional<IRacingCar> findByName(String id) {
		return super.findByName(COLLECTION_NAME, id);
	}

	public List<IRacingCar> loadAll(boolean fromCache) {
		if (fromCache && !cache.isEmpty() ) {
			return cache.values().stream()
					.map(CacheEntry::getContent)
					.sorted(Comparator.comparing(IRacingCar::getName))
					.collect(Collectors.toList());
		}
		List<IRacingCar> allCars = super.loadAll(COLLECTION_NAME);
		allCars.stream().forEach(s -> putToCache(s.getId(), s));
		return allCars.stream()
				.sorted(Comparator.comparing(IRacingCar::getName))
				.collect(Collectors.toList());
	}

	private double doubleFromNumber(Object number) {
		try {
			return (Double)number;
		} catch(ClassCastException e) {
			log.info("read long as double");
			return (Long)number;
		}
	}
}
