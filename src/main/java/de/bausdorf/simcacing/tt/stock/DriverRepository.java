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

import java.util.Map;
import java.util.Optional;

import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.bausdorf.simcacing.tt.util.FirestoreDB;
import de.bausdorf.simcacing.tt.util.MongoDB;
import de.bausdorf.simcacing.tt.util.TeamtacticsServerProperties;
import de.bausdorf.simcacing.tt.util.TimeCachedRepository;

@Component
public class DriverRepository extends TimeCachedRepository<IRacingDriver> {
	public static final String COLLECTION_NAME = "iRacingDrivers";

	public DriverRepository(@Autowired MongoDB db, @Autowired TeamtacticsServerProperties config) {
		super(db, config.getDriverRepositoryCacheMinutes() * 60000);
	}

	@Override
	protected IRacingDriver fromMap(Map<String, Object> data) {
		if( data == null )
			return null;

		return IRacingDriver.builder()
				.id((String)data.get(IRacingDriver.I_RACING_ID))
				.name((String)data.get(IRacingDriver.NAME))
				.validated((Boolean)data.get(IRacingDriver.VALIDATED))
				.build();
	}

	@Override
	protected Map<String, Object> toMap(IRacingDriver object) {
		return object.toMap();
	}

	public void save(IRacingDriver driver) {
		super.save(COLLECTION_NAME, driver.getId(), driver);
	}

	public Optional<IRacingDriver> findById(String id) {
		return super.findByName(COLLECTION_NAME, id);
	}

	public void delete(String id) {
		super.delete(COLLECTION_NAME, id);
	}
}
