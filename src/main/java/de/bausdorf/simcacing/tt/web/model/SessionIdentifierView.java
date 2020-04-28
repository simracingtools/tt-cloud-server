package de.bausdorf.simcacing.tt.web.model;

import de.bausdorf.simcacing.tt.live.model.client.SessionIdentifier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SessionIdentifierView {

	private String teamName;
	private String sessionId;
	private String sessionKey;
	private String teamId;

	public SessionIdentifierView(String teamId, SessionIdentifier identifier) {
		this.teamId = teamId;
		this.teamName = identifier.getTeamName();
		this.sessionId = identifier.getSubscriptionId();
		this.sessionKey = identifier.toString();
	}
}
