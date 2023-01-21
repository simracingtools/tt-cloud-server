package de.bausdorf.simcacing.tt.live.impl;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@Slf4j
public class TeamTacticsClientServiceImpl implements TeamTacticsClientService {

	public static final String KEY_TYPE = "type";

	private static final List<String> acceptedVersions = new ArrayList<>();

	static {
		acceptedVersions.add("1.31");
		acceptedVersions.add("1.32");
		acceptedVersions.add("1.33");
		acceptedVersions.add("1.34");
	}

	private final MessageProcessor processor;

	private final TtClientRegistrationRepository clientRepository;

	private final Map<String, String> tokenCache;

	private final ObjectMapper objectMapper;

	public TeamTacticsClientServiceImpl(@Autowired MessageProcessor processor,
			@Autowired TtClientRegistrationRepository clientRepository) {
		this.clientRepository = clientRepository;
		this.processor = processor;
		this.tokenCache = new HashMap<>();
		this.objectMapper = new ObjectMapper();
	}

	@Override
	@PostMapping(value = "/clientmessage")
	public String receiveClientData(@RequestBody String clientString,
			@RequestHeader("x-teamtactics-token") Optional<String> clientAccessToken) {

		// Maybe Json is escaped - so remove escape characters
		Map<String, Object> clientMessage = readClientMessage(clientString.replace("\\", ""));

		if (clientAccessToken.isEmpty()) {
			log.warn("No x-teamtactics-token header");
			return "TOKEN_ERROR";
		}
		try {
			ClientMessage msg = this.validateClientMessage(clientMessage, clientAccessToken.get());
			if (msg.getType() != MessageType.PING) {
				processMessage(msg);
			}
			return msg.getType().name();
		} catch (InvalidClientMessageException e) {
			log.warn(e.getMessage());
			return "VALIDATION_ERROR";
		} catch (UnauthorizedAccessException e) {
			log.warn(e.getMessage());
			return "AUTHORIZATION_ERROR";
		} catch (UnsupportedClientException e) {
			log.warn(e.getMessage());
			return "UNSUPPORTED_CLIENT";
		}
	}

	private Map<String, Object> readClientMessage(String clientString) {
		try {
			// Maybe json string is quoted - so remove quotes if present
			if (clientString.startsWith("\"") && clientString.endsWith("\"")) {
				clientString = clientString.substring(1, clientString.length() - 1);
			}
			TypeReference<HashMap<String, Object>> typeRef
					= new TypeReference<HashMap<String, Object>>() {};
			return objectMapper.readValue(clientString, typeRef);
		} catch (JsonProcessingException e) {
			throw new InvalidClientMessageException(e.getMessage());
		}
	}

	private ClientMessage validateClientMessage(Map<String, Object> clientMessage, String accessToken) {
		String clientId = (String)clientMessage.get(MessageConstants.Message.CLIENT_ID);
		if (clientId == null) {
			clientId = (String)clientMessage.get("client_id");
			if (clientId == null) {
				log.warn("no clientId in {}", clientMessage);
			}
		}
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
				throw new UnsupportedClientException("Client version " + clientVersion + " not accepted");
			}
		}
		if( clientId == null ) {
			throw new InvalidClientMessageException("Message without client id");
		}
		if( sessionId == null ) {
			throw new InvalidClientMessageException("Message without session id");
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
				throw new UnauthorizedAccessException("No token for client id " + clientId);
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
			throw new UnauthorizedAccessException("Token could not be validated on client id " + clientId);
		} else {
			if (!authorizedClientId.equalsIgnoreCase(clientId)) {
				throw new UnauthorizedAccessException("Token could not be validated on client id " + clientId);
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
