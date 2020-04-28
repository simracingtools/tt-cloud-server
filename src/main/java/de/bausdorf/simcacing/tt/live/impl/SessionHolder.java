package de.bausdorf.simcacing.tt.live.impl;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import de.bausdorf.simcacing.tt.live.clientapi.*;
import de.bausdorf.simcacing.tt.live.model.client.*;
import de.bausdorf.simcacing.tt.live.model.live.LiveClientMessage;
import de.bausdorf.simcacing.tt.live.model.live.RunDataView;
import de.bausdorf.simcacing.tt.live.model.live.SessionDataView;
import de.bausdorf.simcacing.tt.stock.DriverRepository;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class SessionHolder implements MessageProcessor {
	public static final String LIVE_PREFIX = "/live/";

	private SessionMap data;

	private EnumMap<MessageType, MessageValidator> validators;

	private Map<String, String> liveTopics;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	DriverRepository driverRepository;

	public SessionHolder() {
		this.data = new SessionMap();
		this.validators = new EnumMap<>(MessageType.class);
		this.liveTopics = new HashMap<>();
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
				message.getTeamId(),
				ModelFactory.parseClientSessionId(message.getSessionId()));
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

				sendRunData((RunData)clientData, sessionKey.getSessionId().getSubscriptionId());
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
				if( !addSession(sessionKey, sessionData)) {
					log.warn("Session {} already exists", sessionKey);
				}
				sendSessionData(sessionData, sessionKey.getSessionId().getSubscriptionId());
				break;
			default:
				break;
		}
	}

	public boolean addSession(SessionKey sessionKey, SessionData sessionData) {
		return data.putSession(sessionKey, sessionData);
	}

	public SessionController getSessionController(SessionKey key) {
		if( data.containsKey(key) ) {
			return data.get(key);
		}
		throw new IllegalArgumentException("No session " + key.getSessionId() + " for team " + key.getTeamId());
	}

	public SessionController getSessionControllerBySubscriptionId(String subscriptionId) {
		for (Map.Entry<SessionKey, SessionController> entry : data.entrySet()) {
			if (entry.getKey().getSessionId().getSubscriptionId().equalsIgnoreCase(subscriptionId)) {
				return entry.getValue();
			}
		}
		return null;
	}

	public Set<SessionKey> getAvailableSessions() {
		return data.keySet();
	}

	public void sendSessionData(SessionData sessionData, String teamId) {
		if (liveTopics.containsKey(teamId)) {
			messagingTemplate.convertAndSend(LIVE_PREFIX + teamId + "/sessiondata", SessionDataView.builder()
					.carName(sessionData.getCarName())
					.trackName(sessionData.getTrackName())
					.sessionDuration(sessionData.getSessionDuration().orElse(LocalTime.MIN)
							.format(DateTimeFormatter.ofPattern("HH:mm")))
					.sessionType(sessionData.getSessionType())
					.teamName(sessionData.getTeamName())
					.sessionId(teamId)
					.maxCarFuel(String.format("%.3f", sessionData.getMaxCarFuel()))
					.build());
		}
	}

	public void sendRunData(RunData runData, String teamId) {
		if (liveTopics.containsKey(teamId)) {
			messagingTemplate.convertAndSend(LIVE_PREFIX + teamId + "/rundata", RunDataView.builder()
					.fuelLevel(runData.getFuelLevel())
					.fuelLevelStr(String.format("%.3f", runData.getFuelLevel()))
					.sessionTime(runData.getSessionTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
					.flags(runData.getFlags().stream()
							.map(FlagType::name)
							.collect(Collectors.toList()))
					.driverName(driverRepository.findById(runData.getClientId())
							.orElse(IRacingDriver.builder()
									.name("N.N.")
									.id(runData.getClientId())
									.validated(false)
									.build())
							.getName())
					.build());
		}
	}

	@MessageMapping("/liveclient")
	@SendTo("/live/client-ack")
	public SessionDataView respondAck(LiveClientMessage message) {
		log.info("Connect message from {}: {}", message.getTeamId(), message.getText());
		String teamId = message.getTeamId();
		if (!liveTopics.containsKey(teamId)) {
			liveTopics.put(teamId, LIVE_PREFIX + teamId);
		}
		SessionDataView sessionDataView = getSessionDataView(teamId);
		if (sessionDataView != null) {
			return sessionDataView;
		} else {
			log.warn("No session controller matching subscriptionId {}", teamId);
		}
		return SessionDataView.builder()
				.maxCarFuel("-.---")
				.sessionId("unknown session")
				.teamName("---")
				.sessionType("---")
				.sessionDuration("--.--.--")
				.trackName("---")
				.carName("---")
				.build();
	}

	private SessionDataView getSessionDataView(String subscriptionId) {
		SessionController controller = getSessionControllerBySubscriptionId(subscriptionId);
		if (controller != null) {
			return SessionDataView.builder()
					.carName(controller.getSessionData().getCarName())
					.trackName(controller.getSessionData().getTrackName())
					.sessionDuration(controller.getSessionData().getSessionDuration().orElse(LocalTime.MIN)
							.format(DateTimeFormatter.ofPattern("HH:mm")))
					.sessionType(controller.getSessionData().getSessionType())
					.teamName(controller.getSessionData().getTeamName())
					.sessionId(subscriptionId)
					.maxCarFuel(String.format("%.3f", controller.getSessionData().getMaxCarFuel()))
					.build();
		}
		return null;
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
