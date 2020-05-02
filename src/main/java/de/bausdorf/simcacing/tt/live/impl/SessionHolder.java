package de.bausdorf.simcacing.tt.live.impl;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import de.bausdorf.simcacing.tt.live.clientapi.*;
import de.bausdorf.simcacing.tt.live.model.client.*;
import de.bausdorf.simcacing.tt.live.model.live.EventDataView;
import de.bausdorf.simcacing.tt.live.model.live.LapDataView;
import de.bausdorf.simcacing.tt.live.model.live.LiveClientMessage;
import de.bausdorf.simcacing.tt.live.model.live.RunDataView;
import de.bausdorf.simcacing.tt.live.model.live.SessionDataView;
import de.bausdorf.simcacing.tt.live.model.live.SyncDataView;
import de.bausdorf.simcacing.tt.stock.DriverRepository;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.util.TeamtacticsServerProperties;
import de.bausdorf.simcacing.tt.util.TimeTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class SessionHolder implements MessageProcessor {

	private static final String LIVE_PREFIX = "/live/";
	public static final String HH_MM_SS = "HH:mm:ss";
	public static final String TABLE_SUCCESS = "table-success";
	public static final String TABLE_DANGER = "table-danger";

	private final TeamtacticsServerProperties config;
	private final SessionMap data;
	private final EnumMap<MessageType, MessageValidator> validators;
	private final Map<String, MessageTransformer> transformers;
	private final Map<String, String> liveTopics;
	private final SimpMessagingTemplate messagingTemplate;
	final DriverRepository driverRepository;

	public SessionHolder(@Autowired SimpMessagingTemplate messagingTemplate,
			@Autowired DriverRepository driverRepository,
			@Autowired TeamtacticsServerProperties config) {
		this.data = new SessionMap();
		this.validators = new EnumMap<>(MessageType.class);
		this.transformers = new HashMap<>();
		this.liveTopics = new HashMap<>();
		this.messagingTemplate = messagingTemplate;
		this.driverRepository = driverRepository;
		this.config = config;
	}

	@Override
	public void registerMessageValidator(MessageValidator validator) {
		log.info("Registering validator for " + validator.supportedMessageType().name());
		validators.put(validator.supportedMessageType(), validator);
	}

	@Override
	public void registerMessageTransformer(MessageTransformer transformer) {
		log.info("Register transformer for version " + transformer.supportedMessageVersion());
		transformers.put(transformer.supportedMessageVersion(), transformer);
	}

	@Override
	public void processMessage(ClientMessage message) {
		ClientData clientData = validateAndConvert(transform(message));
		SessionKey sessionKey = new SessionKey(
				message.getTeamId(),
				ModelFactory.parseClientSessionId(message.getSessionId()));

		SessionController controller;
		switch(message.getType()) {
			case LAP:
				try {
					controller = getSessionController(sessionKey);
					controller.setLastUpdate(System.currentTimeMillis());
					controller.addLap((LapData) clientData);
					sendLapData((LapData)clientData, controller, sessionKey.getSessionId().getSubscriptionId());
				} catch( DuplicateLapException e) {
					log.warn(e.getMessage());
				}
				break;
			case RUN_DATA:
				controller = getSessionController(sessionKey);
				controller.setLastUpdate(System.currentTimeMillis());
				IRacingDriver driver = driverRepository.findById(message.getClientId())
						.orElse(IRacingDriver.builder()
								.id("unknown")
								.name("N.N.")
								.validated(false)
								.build());
				if (!driver.getId().equalsIgnoreCase("unknown") && !driver.isValidated()) {
					driver.setValidated(true);
					driverRepository.save(driver);
				}
				controller.setCurrentDriver(driver);
				controller.updateRunData((RunData)clientData);
				sendRunData((RunData)clientData, controller, sessionKey.getSessionId().getSubscriptionId(), driver);

				SyncData syncData = SyncData.builder()
						.isInCar(true)
						.sessionTime(((RunData)clientData).getSessionTime())
						.clientId(message.getClientId())
						.build();
				controller.updateSyncData(syncData);
				sendSyncData(syncData, controller.getHeartbeats().values(), sessionKey.getSessionId().getSubscriptionId());
				break;
			case EVENT:
				controller = getSessionController(sessionKey);
				controller.setLastUpdate(System.currentTimeMillis());
				controller.processEventData((EventData)clientData);
				sendEventData((EventData)clientData, sessionKey.getSessionId().getSubscriptionId());
				break;
			case SYNC:
				controller = getSessionController(sessionKey);
				controller.setLastUpdate(System.currentTimeMillis());
				controller.updateSyncData((SyncData)clientData);
				sendSyncData((SyncData)clientData, controller.getHeartbeats().values(), sessionKey.getSessionId().getSubscriptionId());
				break;
			case SESSION_INFO:
				SessionData sessionData = (SessionData)clientData;
				if( !data.putSession(sessionKey, sessionData)) {
					log.warn("Session {} already exists", sessionKey);
				}
				break;
			default:
				break;
		}
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

	private void sendLapData(LapData clientData, SessionController controller, String subscriptionId) {
		if (liveTopics.containsKey(subscriptionId)) {
			messagingTemplate.convertAndSend(LIVE_PREFIX + subscriptionId + "/lapdata",
					getLapDataView(clientData, controller));
		}
	}

	public void sendRunData(RunData runData, SessionController controller, String teamId, IRacingDriver driver) {
		if (liveTopics.containsKey(teamId)) {
			double availableLaps = controller.getAvailableLapsForFuelLevel(runData.getFuelLevel());
			String lapsCssClass = "table-info";
			if (availableLaps < 1.0D) {
				lapsCssClass = TABLE_DANGER;
			} else if (availableLaps < 3.0D) {
				lapsCssClass = "table-warning";
			}
			messagingTemplate.convertAndSend(LIVE_PREFIX + teamId + "/rundata", RunDataView.builder()
					.fuelLevel(runData.getFuelLevel())
					.fuelLevelStr(fuelString(runData.getFuelLevel()).replace(",", "."))
					.sessionTime(runData.getSessionTime().format(DateTimeFormatter.ofPattern(HH_MM_SS)))
					.flags(runData.getFlags().stream()
							.map(FlagType::name)
							.collect(Collectors.toList()))
					.driverName(driver.getName())
					.raceSessionTime(controller.getCurrentRaceSessionTime().format(DateTimeFormatter.ofPattern(HH_MM_SS)))
					.remainingSessionTime(TimeTools.shortDurationString(controller.getRemainingSessionTime()))
					.availableLaps(String.format("%.2f", availableLaps))
					.availableLapsCssClass(lapsCssClass)
					.flagCssClass(runData.getFlags().get(0).cssClass())
					.timeOfDay(runData.getSessionToD().format(DateTimeFormatter.ofPattern(HH_MM_SS)))
					.lapNo(Integer.toUnsignedString(runData.getLapNo()))
					.timeInLap(TimeTools.longDurationString(runData.getTimeInLap()))
					.build());
		}
	}

	public void sendEventData(EventData eventData, String teamId) {
		if (liveTopics.containsKey(teamId)) {
			messagingTemplate.convertAndSend(LIVE_PREFIX + teamId + "/eventdata", EventDataView.builder()
					.sessionTime(eventData.getSessionTime().format(DateTimeFormatter.ofPattern(HH_MM_SS)))
					.timeOfDay(eventData.getSessionToD().format(DateTimeFormatter.ofPattern(HH_MM_SS)))
					.trackLocation(eventData.getTrackLocationType().name())
					.trackLocationCssClass(eventData.getTrackLocationType().getCssClass())
					.build());
		}
	}

	public void sendSyncData(SyncData syncData, Collection<SyncData> teamSync, String teamId) {
		List<SyncDataView> syncDataViews = new ArrayList<>();
		String inCarCssClass = syncData.isInCar() ? "table-info" : "";
		for (SyncData sync : teamSync) {
			syncDataViews.add(SyncDataView.builder()
					.driverId(sync.getClientId())
					.timestamp(sync.getSessionTime().format(DateTimeFormatter.ofPattern(HH_MM_SS)))
					.stateCssClass(getSyncState(sync.getSessionTime(), syncData.getSessionTime()))
					.inCarCssClass(sync.getClientId().equalsIgnoreCase(syncData.getClientId()) ? inCarCssClass : "")
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

	private LapDataView getLapDataView(LapData clientData, SessionController controller) {
		if (clientData == null) {
			return null;
		}
		Optional<Stint> lastStint = controller.getLastStint();
		Duration stintAvgLapTime = Duration.ZERO;
		int stintLap = clientData.getNo();
		double stintAvgFuel = 0.0D;
		if (lastStint.isPresent()) {
			stintAvgFuel = lastStint.get().getAvgFuelPerLap();
			stintAvgLapTime = lastStint.get().getAvgLapTime();
			stintLap = lastStint.get().getLaps();
		}
		double avgFuelDelta = stintAvgFuel - clientData.getLastLapFuelUsage();
		String avgTimeDelta = TimeTools.longDurationDeltaString(stintAvgLapTime, clientData.getLapTime());
		return LapDataView.builder()
				.lapNo(Integer.toUnsignedString(clientData.getNo()))
				.lapsRemaining(Integer.toUnsignedString(controller.getRemainingLapCount()))
				.lastLapFuel(fuelString(clientData.getLastLapFuelUsage()))
				.lastLapTime(TimeTools.longDurationString(clientData.getLapTime()))
				.stintNo(lastStint.map(stint -> Integer.toUnsignedString(stint.getNo())).orElse("-"))
				.stintAvgLapTime(TimeTools.longDurationString(stintAvgLapTime))
				.stintAvgFuelPerLap(fuelString(stintAvgFuel))
				.stintAvgFuelDelta(fuelString(avgFuelDelta))
				.stintAvgFuelDeltaCssClass(avgFuelDelta < 0.0 ? TABLE_DANGER : TABLE_SUCCESS)
				.stintAvgTimeDelta(avgTimeDelta)
				.stintAvgTimeDeltaCssClass(avgTimeDelta.startsWith("-") ? TABLE_DANGER : TABLE_SUCCESS)
				.stintClock(TimeTools.shortDurationString(controller.getCurrentStintTime()))
				.stintRemainingTime(TimeTools.shortDurationString(controller.getRemainingStintTime()))
				.stintsRemaining(Integer.toUnsignedString(controller.getRemainingStintCount()))
				.stintLap(Integer.toUnsignedString(stintLap))
				.trackTemp(String.format("%.1f",clientData.getTrackTemp()) + "°C")
				.build();
	}

	private SessionDataView getSessionDataView(String subscriptionId) {
		SessionController controller = getSessionControllerBySubscriptionId(subscriptionId);
		TrackLocationType locationType = controller.getCurrentTrackLocation() == null
				? TrackLocationType.OFF_WORLD : controller.getCurrentTrackLocation();
		if (controller != null) {
			return SessionDataView.builder()
					.carName(controller.getSessionData().getCarName())
					.trackName(controller.getSessionData().getTrackName())
					.sessionDuration(controller.getSessionData().getSessionDuration().orElse(LocalTime.MIN)
							.format(DateTimeFormatter.ofPattern("HH:mm")))
					.sessionType(controller.getSessionData().getSessionType())
					.teamName(controller.getSessionData().getTeamName())
					.sessionId(subscriptionId)
					.maxCarFuel(fuelString(controller.getSessionData().getMaxCarFuel()))
					.lastLapData(getLapDataView(controller.getLastRecordedLap().orElse(null), controller))
					.trackLocation(locationType.name())
					.trackLocationCssClass(locationType.getCssClass())
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

	private ClientMessage transform(ClientMessage message) {
		MessageTransformer transformer = transformers.get(message.getVersion());
		if (transformer != null) {
			return transformer.transform(message);
		}
		return message;
	}

	private static String getSyncState(LocalTime lastSync, LocalTime currentSync) {
		if (lastSync.plusSeconds(10).isAfter(currentSync)) {
			return TABLE_SUCCESS;
		} else if (lastSync.plusSeconds(30).isAfter(currentSync)) {
			return "table-warning";
		}
		return TABLE_DANGER;
	}

	private static String fuelString(double fuelAmount) {
		return String.format("%.3f", fuelAmount).replace(",", ".");
	}

	@Scheduled(cron="0 0/30 * * * ?")
	public void removeInactiveSessions() {
		List<SessionKey> inactiveSessions = new ArrayList<>();
		long timeoutMillis = config.getInactiveSessionTimeoutMinutes() * 60000;
		for (Map.Entry<SessionKey, SessionController> activeSession : data.entrySet()) {
			if (activeSession.getValue().getLastUpdate() + timeoutMillis < System.currentTimeMillis()) {
				inactiveSessions.add(activeSession.getKey());
			}
		}
		if (inactiveSessions.isEmpty()) {
			log.info("No inactive sessions found");
		}
		for (SessionKey key : inactiveSessions) {
			log.info("Removing inactive session " + key.getSessionId().toString());
			data.remove(key);
		}
	}
}
