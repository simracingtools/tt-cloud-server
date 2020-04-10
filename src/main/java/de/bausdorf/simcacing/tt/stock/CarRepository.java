package de.bausdorf.simcacing.tt.stock;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.bausdorf.simcacing.tt.util.FirestoreDB;
import de.bausdorf.simcacing.tt.stock.model.IRacingCar;
import de.bausdorf.simcacing.tt.util.CachedRepository;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CarRepository extends CachedRepository<IRacingCar> {
	public static final String COLLECTION_NAME = "iRacingCars";

	@Override
	protected IRacingCar fromMap(Map<String, Object> data) {
		return IRacingCar.builder()
				.name((String)data.get(IRacingCar.NAME))
				.id((String)data.get(IRacingCar.ID))
				.build();
	}

	@Override
	protected Map<String, Object> toMap(IRacingCar object) {
		return object.toMap();
	}

	public CarRepository(@Autowired FirestoreDB db) {
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
			return cache.values().stream().map(s -> s.getContent()).collect(Collectors.toList());
		}
		List<IRacingCar> allCars = super.loadAll(COLLECTION_NAME);
		allCars.stream().forEach(s -> putToCache(s.getId(), s));
		return allCars;
	}
}
