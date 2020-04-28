package de.bausdorf.simcacing.tt.live.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.bausdorf.simcacing.tt.live.clientapi.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TeamTacticsClientServiceImpl implements TeamTacticsClientService {

	public static final String KEY_TYPE = "type";

	private static final List<String> acceptedVersions = new ArrayList<>();

	static {
		acceptedVersions.add("1.20");
	}

	private MessageProcessor processor;

	public TeamTacticsClientServiceImpl(MessageProcessor processor) {
		this.processor = processor;
	}

	@Override
	@PostMapping("/clientmessage")
	public String receiveClientData(@RequestBody Map<String, Object> clientMessage) {
		ClientMessage msg = this.validateClientMessage(clientMessage);
		try {
			processor.processMessage(msg);
		} catch (Exception e) {
			log.error("{}: {}", e.getMessage(), msg);
		}
		return msg.getType().name();
	}

	private ClientMessage validateClientMessage(Map<String, Object> clientMessage) {
		String messageType = (String)clientMessage.get(KEY_TYPE);
		String clientVersion = (String)clientMessage.get(MessageConstants.Message.VERSION);
		String sessionId = (String)clientMessage.get(MessageConstants.Message.SESSION_ID);
		String clientId = (String)clientMessage.get(MessageConstants.Message.CLIENT_ID);
		String teamId = (String)clientMessage.get(MessageConstants.Message.TEAM_ID);

		if( messageType == null ) {
			throw new InvalidClientMessageException("No message type in message");
		}
		if( clientVersion == null ) {
			throw new InvalidClientMessageException("No message version");
		} else {
			if( !acceptedVersions.contains(clientVersion)) {
				throw new InvalidClientMessageException("Client version " + clientVersion + " not accepted");
			}
		}
		if( sessionId == null ) {
			throw new InvalidClientMessageException("Message without session id");
		}
		if( clientId == null ) {
			throw new InvalidClientMessageException("Message without client id");
		}

		try {
			return ClientMessage.builder()
					.type(MessageType.fromJsonKey(messageType))
					.version(clientVersion)
					.sessionId(sessionId)
					.teamId(teamId)
					.clientId(clientId)
					.payload((Map<String, Object>) clientMessage.get(MessageConstants.Message.PAYLOAD))
					.build();
		} catch( Exception e ) {
			throw new InvalidClientMessageException(e.getMessage());
		}
	}
}
