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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.bausdorf.simcacing.tt.stock.model.IRacingTeam;
import de.bausdorf.simcacing.tt.util.FirestoreDB;
import de.bausdorf.simcacing.tt.util.MongoDB;
import de.bausdorf.simcacing.tt.util.TeamtacticsServerProperties;
import de.bausdorf.simcacing.tt.util.TimeCachedRepository;

@Component
public class TeamRepository extends TimeCachedRepository<IRacingTeam> {
	public static final String COLLECTION_NAME = "iRacingTeams";

	public TeamRepository(@Autowired MongoDB db, @Autowired TeamtacticsServerProperties config) {
		super(db, config.getTeamRepositoryCacheMinutes() * 60000);
	}

	@Override
	protected IRacingTeam fromMap(Map<String, Object> data) {
		if( data == null )
			return null;

		return IRacingTeam.builder()
				.id((String)data.get(IRacingTeam.TEAM_ID))
				.name((String)data.get(IRacingTeam.TEAM_NAME))
				.ownerId((String)data.get(IRacingTeam.OWNER_ID))
				.authorizedDriverIds((List<String>)data.get(IRacingTeam.AUTHORIZED_DRIVERS))
				.teamAdminIds(data.get(IRacingTeam.TEAM_ADMINS) != null
				 		? (List<String>)data.get(IRacingTeam.TEAM_ADMINS) : new ArrayList<>())
				.build();
	}

	@Override
	protected Map<String, Object> toMap(IRacingTeam object) {
		return object.toMap();
	}

	public void save(IRacingTeam object) {
		super.save(COLLECTION_NAME, object.getId(), object);
	}
	public void delete(String id) { super.delete(COLLECTION_NAME, id); }

	public Optional<IRacingTeam> findById(String id) {
		return super.findByName(COLLECTION_NAME, id);
	}

	public List<IRacingTeam> findByOwnerId(String ownerId) {
		return super.findByFieldValue(COLLECTION_NAME, IRacingTeam.OWNER_ID, ownerId);
	}

	public List<IRacingTeam> findByAuthorizedDrivers(String driverId) {
		return super.findByArrayContains(COLLECTION_NAME, IRacingTeam.AUTHORIZED_DRIVERS, driverId);
	}

	public List<IRacingTeam> findByTeamAdmins(String driverId) {
		return super.findByArrayContains(COLLECTION_NAME, IRacingTeam.TEAM_ADMINS, driverId);
	}
}
