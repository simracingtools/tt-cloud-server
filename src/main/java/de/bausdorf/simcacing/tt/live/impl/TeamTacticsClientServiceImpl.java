package de.bausdorf.simcacing.tt.live.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.bausdorf.simcacing.tt.live.clientapi.*;
import de.bausdorf.simcacing.tt.web.security.TtClientRegistrationRepository;
import de.bausdorf.simcacing.tt.web.security.TtUser;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TeamTacticsClientServiceImpl implements TeamTacticsClientService {

	public static final String KEY_TYPE = "type";

	private static final List<String> acceptedVersions = new ArrayList<>();

	static {
		acceptedVersions.add("1.20");
		acceptedVersions.add("1.30");
	}

	private final MessageProcessor processor;

	private final TtClientRegistrationRepository clientRepository;

	private final Map<String, String> tokenCache;

	public TeamTacticsClientServiceImpl(@Autowired MessageProcessor processor,
			@Autowired TtClientRegistrationRepository clientRepository) {
		this.clientRepository = clientRepository;
		this.processor = processor;
		this.tokenCache = new HashMap<>();
	}

	@Override
	@PostMapping("/clientmessage")
	public String receiveClientData(@RequestBody Map<String, Object> clientMessage,
			@RequestHeader("x-teamtactics-token") Optional<String> clientAccessToken) {
		if (!clientAccessToken.isPresent()) {
			log.warn("No x-teamtactics-token header");
			return "TOKEN_ERROR";
		}
		try {
			ClientMessage msg = this.validateClientMessage(clientMessage, clientAccessToken.get());
			processMessage(msg);
			return msg.getType().name();
		} catch (InvalidClientMessageException e) {
			log.warn(e.getMessage());
		}
		return "VALIDATION_ERROR";
	}

	private ClientMessage validateClientMessage(Map<String, Object> clientMessage, String accessToken) {
		String clientId = (String)clientMessage.get(MessageConstants.Message.CLIENT_ID);
		validateAccessToken(accessToken, clientId);

		String messageType = (String)clientMessage.get(KEY_TYPE);
		String clientVersion = (String)clientMessage.get(MessageConstants.Message.VERSION);
		String sessionId = (String)clientMessage.get(MessageConstants.Message.SESSION_ID);
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

	private void validateAccessToken(String accessToken, String clientId) {
		String authorizedClientId = tokenCache.get(accessToken);
		if (authorizedClientId == null) {
			List<TtUser> users = clientRepository.findByAccessToken(accessToken);
			if (users.isEmpty()) {
				throw new InvalidClientMessageException("No token for client id " + clientId);
			}
			for (TtUser user : users) {
				if (user.getClientMessageAccessToken().equalsIgnoreCase(accessToken)
						&& user.getIRacingId().equalsIgnoreCase(clientId)) {
					authorizedClientId = clientId;
					tokenCache.put(accessToken, authorizedClientId);
					return;
				}
			}
			tokenCache.put(accessToken, "NONE");
			throw new InvalidClientMessageException("Token could not be validated on client id " + clientId);
		} else {
			if (!authorizedClientId.equalsIgnoreCase(clientId)) {
				throw new InvalidClientMessageException("Token could not be validated on client id " + clientId);
			}
		}
	}

	private void processMessage(ClientMessage msg) {
		try {
			processor.processMessage(msg);
		} catch (InvalidClientMessageException e) {
			log.warn(e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("{}: {}", e.getMessage(), msg);
			StackTraceElement[] trace = e.getStackTrace();
			log.error("{}({}:{})", trace[0].getMethodName(), trace[0].getClassName(), trace[0].getLineNumber());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
