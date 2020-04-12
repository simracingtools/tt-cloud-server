package de.bausdorf.simcacing.tt.stock;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.bausdorf.simcacing.tt.stock.model.IRacingTeam;
import de.bausdorf.simcacing.tt.util.FirestoreDB;
import de.bausdorf.simcacing.tt.util.TeamtacticsServerProperties;
import de.bausdorf.simcacing.tt.util.TimeCachedRepository;

@Component
public class TeamRepository extends TimeCachedRepository<IRacingTeam> {
	public static final String COLLECTION_NAME = "iRacingTeams";

	public TeamRepository(@Autowired FirestoreDB db, @Autowired TeamtacticsServerProperties config) {
		super(db, config.getTeamRepositoryCacheMinutes());
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
}
