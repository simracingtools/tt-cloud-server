package de.bausdorf.simcacing.tt.clientapi;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientMessage {

	private MessageType type;
	private String version;
	private String sessionId;
	private String teamid;
	private Map<String, Object> payload;
}
