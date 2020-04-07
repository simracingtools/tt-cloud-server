package de.bausdorf.simcacing.tt.stock;

import java.util.Map;
import java.util.Optional;

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
		super.save(COLLECTION_NAME, car.getName(), car);
	}

	public Optional<IRacingCar> findByName(String name) {
		return super.findByName(COLLECTION_NAME, name);
	}
}
