package de.bausdorf.simcacing.tt.clientapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeamTacticsClientService {

	public static final String KEY_TYPE = "type";

	private static final List<String> acceptedVersions;

	static {
		acceptedVersions = new ArrayList<String>() {
			{
				add("1.02");
			}
		};
	}

	@PostMapping("/clientmessage")
	public Map<String, Object> receiveClientData(Map<String, Object> clientMessage) {

		ClientMessage cm = this.validateMessage(clientMessage);
		return null;
	}

	private ClientMessage validateMessage(Map<String, Object> clientMessage) {

		String messageType = (String)clientMessage.get(KEY_TYPE);
		String clientVersion = (String)clientMessage.get("version");
		if( clientVersion == null ) {
			throw new IllegalArgumentException("No message version");
		} else {
			if( !acceptedVersions.contains(clientVersion)) {
				throw new IllegalArgumentException("Client version " + clientVersion + " not accepted");
			}
		}
		return ClientMessage.builder()
				.type(MessageType.fromJsonKey(messageType))
				.version(clientVersion)
				.sessionId((String)clientMessage.get("sessionid"))
				.teamid("teamid")
				.payload((Map<String, Object>)clientMessage.get("payload"))
				.build();
	}
}
