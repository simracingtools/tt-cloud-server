package de.bausdorf.simcacing.tt.stock.model;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.bausdorf.simcacing.tt.util.FirestoreDB;
import de.bausdorf.simcacing.tt.util.TeamtacticsServerProperties;
import de.bausdorf.simcacing.tt.util.TimeCachedRepository;

@Component
public class DriverRepository extends TimeCachedRepository<IRacingDriver> {

	public DriverRepository(@Autowired FirestoreDB db, @Autowired TeamtacticsServerProperties config) {
		super(db, config.getDriverRepositoryCacheMinutes() * 60000);
	}

	@Override
	protected IRacingDriver fromMap(Map<String, Object> data) {
		return IRacingDriver.builder()
				.id((String)data.get(IRacingDriver.I_RACING_ID))
				.name((String)data.get(IRacingDriver.NAME))
				.build();
	}

	@Override
	protected Map<String, Object> toMap(IRacingDriver object) {
		return object.toMap();
	}
}
