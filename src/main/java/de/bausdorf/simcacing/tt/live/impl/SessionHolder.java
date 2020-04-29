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
import de.bausdorf.simcacing.tt.live.model.live.SyncDataView;
import de.bausdorf.simcacing.tt.stock.DriverRepository;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class SessionHolder implements MessageProcessor {
	public static final String LIVE_PREFIX = "/live/";

	private final SessionMap data;

	private final EnumMap<MessageType, MessageValidator> validators;

	private final Map<String, String> liveTopics;

	private final SimpMessagingTemplate messagingTemplate;

	final DriverRepository driverRepository;

	public SessionHolder(@Autowired SimpMessagingTemplate messagingTemplate,
			@Autowired DriverRepository driverRepository) {
		this.data = new SessionMap();
		this.validators = new EnumMap<>(MessageType.class);
		this.liveTopics = new HashMap<>();
		this.messagingTemplate = messagingTemplate;
		this.driverRepository = driverRepository;
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
				try {
					controller = getSessionController(sessionKey);
					controller.addLap((LapData) clientData);
				} catch( DuplicateLapException e) {
					log.warn(e.getMessage());
				}
				break;
			case RUN_DATA:
				controller = getSessionController(sessionKey);
				IRacingDriver driver = driverRepository.findById(message.getClientId())
						.orElse(IRacingDriver.builder()
								.id("unknown")
								.name("N.N.")
								.validated(false)
								.build());
				controller.setCurrentDriver(driver);
				controller.updateRunData((RunData)clientData);
				SyncData syncData = SyncData.builder()
						.isInCar(true)
						.sessionTime(((RunData)clientData).getSessionTime())
						.clientId(message.getClientId())
						.build();
				controller.updateSyncData(syncData);
				sendSyncData(syncData, controller.getHeartbeats().values(), sessionKey.getSessionId().getSubscriptionId());

				sendRunData((RunData)clientData, sessionKey.getSessionId().getSubscriptionId(), driver);
				break;
			case EVENT:
				controller = getSessionController(sessionKey);
				controller.processEventData((EventData)clientData);
				break;
			case SYNC:
				controller = getSessionController(sessionKey);
				controller.updateSyncData((SyncData)clientData);
				sendSyncData((SyncData)clientData, controller.getHeartbeats().values(), sessionKey.getSessionId().getSubscriptionId());
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

	public void sendRunData(RunData runData, String teamId, IRacingDriver driver) {
		if (liveTopics.containsKey(teamId)) {
			messagingTemplate.convertAndSend(LIVE_PREFIX + teamId + "/rundata", RunDataView.builder()
					.fuelLevel(runData.getFuelLevel())
					.fuelLevelStr(String.format("%.3f", runData.getFuelLevel()))
					.sessionTime(runData.getSessionTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
					.flags(runData.getFlags().stream()
							.map(FlagType::name)
							.collect(Collectors.toList()))
					.driverName(driver.getName())
					.build());
		}
	}

	public void sendSyncData(SyncData syncData, Collection<SyncData> teamSync, String teamId) {
		List<SyncDataView> syncDataViews = new ArrayList<>();
		for (SyncData sync : teamSync) {
			syncDataViews.add(SyncDataView.builder()
					.driverId(sync.getClientId())
					.timestamp(sync.getSessionTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
					.stateCssClass(getSyncState(sync.getSessionTime(), syncData.getSessionTime()))
					.inCarCssClass(sync.getClientId().equalsIgnoreCase(syncData.getClientId()) ? "table-info" : "")
					.build());
		}
		messagingTemplate.convertAndSend(LIVE_PREFIX + teamId + "/syncdata", syncDataViews);
	}

	@MessageMapping("/liveclient")
	@SendToUser("/live/client-ack")
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

	private static String getSyncState(LocalTime lastSync, LocalTime currentSync) {
		if (lastSync.plusSeconds(10).isAfter(currentSync)) {
			return "table-success";
		} else if (lastSync.plusSeconds(30).isAfter(currentSync)) {
			return "table-warning";
		}
		return "table-danger";
	}
}
