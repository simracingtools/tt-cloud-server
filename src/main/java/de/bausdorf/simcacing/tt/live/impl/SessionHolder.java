package de.bausdorf.simcacing.tt.live.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.bausdorf.simcacing.tt.live.clientapi.*;
import de.bausdorf.simcacing.tt.live.model.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SessionHolder implements MessageProcessor {

	@Data
	@AllArgsConstructor
	@EqualsAndHashCode
	@ToString
	public class SessionKey {
		private SessionIdentifier sessionId;
		private String teamId;
	}

	private Map<SessionKey, SessionController> data;

	private Map<MessageType, MessageValidator> validators;

	private AssumptionHolder assumptionHolder;

	public SessionHolder(@Autowired AssumptionHolder assumptionHolder) {
		this.assumptionHolder = assumptionHolder;
		this.data = new HashMap<>();
		this.validators = new HashMap<>();
	}

	@Override
	public void registerMessageValidator(MessageValidator validator) {
		log.info("Registering validator for " + validator.supportedMessageType().name());
		validators.put(validator.supportedMessageType(), validator);
	}

	@Override
	public void processMessage(ClientMessage message) {
		ClientData clientData = validateAndConvert(message);
		SessionKey sessionKey = new SessionKey(
				ModelFactory.parseClientSessionId(message.getSessionId()), message.getTeamId());
		SessionController controller;
		switch(message.getType()) {
			case LAP:
				controller = getSessionController(sessionKey);
				try {
					controller.addLap((LapData) clientData);
				} catch( DuplicateLapException e) {
					log.warn(e.getMessage());
				}
				break;
			case RUN_DATA:
				controller = getSessionController(sessionKey);
				controller.updateRunData((RunData)clientData);
				controller.updateSyncData(SyncData.builder()
						.isInCar(true)
						.sessionTime(((RunData)clientData).getSessionTime())
						.clientId(message.getClientId())
						.build());
				break;
			case EVENT:
				controller = getSessionController(sessionKey);
				controller.processEventData((EventData)clientData);
				break;
			case SYNC:
				controller = getSessionController(sessionKey);
				controller.updateSyncData((SyncData)clientData);
				break;
			case SESSION_INFO:
				SessionData sessionData = (SessionData)clientData;
				if( !addSession(sessionKey, sessionData))
					log.warn("Session {} already exists", sessionKey);
				break;
			default:
				break;
		}
	}

	public boolean addSession(SessionKey sessionKey, SessionData sessionData) {
		if( data == null ) {
			data = new HashMap<>();
		}
		if( !data.containsKey(sessionKey)) {
			data.put(sessionKey, new SessionController(sessionData, assumptionHolder));
			return true;
		}
		return false;
	}

	public SessionController getSessionController(SessionKey key) {
		if( data == null ) {
			data = new HashMap<>();
		}
		if( data.containsKey(key) ) {
			return data.get(key);
		}
		throw new IllegalArgumentException("No session " + key.getSessionId() + " for team " + key.getTeamId());
	}

	public Set<SessionKey> getAvailableSessions() {
		return data.keySet();
	}

	private ClientData validateAndConvert(ClientMessage message) {
		MessageValidator validator = validators.get(message.getType());
		if( validator != null ) {
			return validator.validate(message);
		}
		log.debug("No validator for message type " + message.getType().name());
		switch(message.getType()) {
			case LAP: return ModelFactory.fromLapMessage(message.getPayload());
			case EVENT: return ModelFactory.getFromEventMessage(message.getPayload());
			case RUN_DATA: return ModelFactory.fromRunDataMessage(message.getPayload());
			case SESSION_INFO: return ModelFactory.getFromSessionMessage(message.getPayload());
			case SYNC: return ModelFactory.getFromSyncMessage(message.getPayload());
			default: throw new InvalidClientMessageException("Unknown ClientMessage type");
		}
	}
}
