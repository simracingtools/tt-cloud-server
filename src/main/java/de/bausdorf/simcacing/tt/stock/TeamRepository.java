package de.bausdorf.simcacing.tt.stock;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.bausdorf.simcacing.tt.stock.model.IRacingTeam;
import de.bausdorf.simcacing.tt.util.FirestoreDB;
import de.bausdorf.simcacing.tt.util.TeamtacticsServerProperties;
import de.bausdorf.simcacing.tt.util.TimeCachedRepository;

@Component
public class TeamRepository extends TimeCachedRepository<IRacingTeam> {

	public TeamRepository(@Autowired FirestoreDB db, @Autowired TeamtacticsServerProperties config) {
		super(db, config.getTeamRepositoryCacheMinutes());
	}

	@Override
	protected IRacingTeam fromMap(Map<String, Object> data) {
		return IRacingTeam.builder()
				.id((String)data.get(IRacingTeam.TEAM_ID))
				.name((String)data.get(IRacingTeam.NAME))
				.ownerId((String)data.get(IRacingTeam.OWNER_ID))
				.authorizedDriverIds((List<String>)data.get(IRacingTeam.AUTHORIZED_DRIVERS))
				.build();
	}

	@Override
	protected Map<String, Object> toMap(IRacingTeam object) {
		return object.toMap();
	}
}
