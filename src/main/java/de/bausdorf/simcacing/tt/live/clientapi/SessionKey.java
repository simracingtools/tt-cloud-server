package de.bausdorf.simcacing.tt.live.clientapi;

import de.bausdorf.simcacing.tt.live.model.SessionIdentifier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class SessionKey {
	private String teamId;
	private SessionIdentifier sessionId;
}