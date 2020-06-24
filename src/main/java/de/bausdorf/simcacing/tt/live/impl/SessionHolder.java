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

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import de.bausdorf.simcacing.tt.live.clientapi.*;
import de.bausdorf.simcacing.tt.live.model.client.*;
import de.bausdorf.simcacing.tt.live.model.live.DriverChangeMessage;
import de.bausdorf.simcacing.tt.live.model.live.EventDataView;
import de.bausdorf.simcacing.tt.live.model.live.LapDataView;
import de.bausdorf.simcacing.tt.live.model.live.LiveClientMessage;
import de.bausdorf.simcacing.tt.live.model.live.PitstopDataView;
import de.bausdorf.simcacing.tt.live.model.live.RunDataView;
import de.bausdorf.simcacing.tt.live.model.live.ServiceChangeMessage;
import de.bausdorf.simcacing.tt.live.model.live.SessionDataView;
import de.bausdorf.simcacing.tt.live.model.live.SyncDataView;
import de.bausdorf.simcacing.tt.planning.PlanningTools;
import de.bausdorf.simcacing.tt.planning.RacePlanRepository;
import de.bausdorf.simcacing.tt.planning.model.PitStopServiceType;
import de.bausdorf.simcacing.tt.planning.model.RacePlan;
import de.bausdorf.simcacing.tt.planning.model.RacePlanParameters;
import de.bausdorf.simcacing.tt.stock.DriverRepository;
import de.bausdorf.simcacing.tt.stock.DriverStatsRepository;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.stock.model.StatsEntry;
import de.bausdorf.simcacing.tt.util.TeamtacticsServerProperties;
import de.bausdorf.simcacing.tt.util.TimeTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class SessionHolder implements MessageProcessor, ApplicationListener<ApplicationReadyEvent> {

	private static final String LIVE_PREFIX = "/live/";
	public static final String TABLE_SUCCESS = "table-success";
	public static final String TABLE_DANGER = "table-danger";

	private final TeamtacticsServerProperties config;
	private final SessionMap data;
	private final EnumMap<MessageType, MessageValidator> validators;
	private final Map<String, MessageTransformer> transformers;
	private final Map<String, String> liveTopics;
	private final SimpMessagingTemplate messagingTemplate;
	final DriverRepository driverRepository;
	final ActiveSessionRepository sessionRepository;
	final RacePlanRepository planRepository;
	final DriverStatsRepository driverStatsRepository;

	public SessionHolder(@Autowired SimpMessagingTemplate messagingTemplate,
			@Autowired DriverRepository driverRepository,
			@Autowired ActiveSessionRepository sessionRepository,
			@Autowired RacePlanRepository planRepository,
			@Autowired DriverStatsRepository driverStatsRepository,
			@Autowired TeamtacticsServerProperties config) {
		this.data = new SessionMap();
		this.validators = new EnumMap<>(MessageType.class);
		this.transformers = new HashMap<>();
		this.liveTopics = new HashMap<>();
		this.messagingTemplate = messagingTemplate;
		this.driverRepository = driverRepository;
		this.planRepository = planRepository;
		this.config = config;
		this.sessionRepository = sessionRepository;
		this.driverStatsRepository = driverStatsRepository;
	}

	@Override
	public void registerMessageValidator(MessageValidator validator) {
		log.info("Registering validator for " + validator.supportedMessageType().name());
		validators.put(validator.supportedMessageType(), validator);
	}

	@Override
	public void registerMessageTransformer(MessageTransformer transformer) {
		log.info("Register client message transformer for version " + transformer.supportedMessageVersion());
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
				controller = getSessionController(sessionKey);
				controller.setLastUpdate(System.currentTimeMillis());
				processLapData(controller, controller.getSessionData().getSessionId().toString(), (LapData)clientData);
				break;
			case RUN_DATA:
				controller = getSessionController(sessionKey);
				controller.setLastUpdate(System.currentTimeMillis());
				processRunData(controller, message.getClientId(), (RunData)clientData);
				break;
			case EVENT:
				controller = getSessionController(sessionKey);
				controller.setLastUpdate(System.currentTimeMillis());
				processEventData(controller, (EventData)clientData);
				break;
			case SYNC:
				controller = getSessionController(sessionKey);
				controller.setLastUpdate(System.currentTimeMillis());
				controller.updateSyncData((SyncData)clientData);
				sendSyncData((SyncData)clientData, sessionKey.getSessionId().getSubscriptionId());
				break;
			case SESSION_INFO:
				SessionData sessionData = (SessionData)clientData;
				if( !data.putSession(sessionKey, sessionData)) {
					log.warn("Session {} already exists", sessionKey);
					break;
				}
				controller = data.get(sessionKey);
				controller.setRacePlan(selectRacePlan(sessionData, sessionKey.getTeamId(), controller.getSessionRegistered()));
				sessionRepository.save(controller);
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
			LapDataView dataView = LapDataView.getLapDataView(clientData, controller);
			log.debug("Send lap data: {}", dataView);
			if (dataView != null) {
				messagingTemplate.convertAndSend(LIVE_PREFIX + subscriptionId + "/lapdata",
						dataView);
			}
		}
	}

	public void sendPitstopData(List<PitstopDataView> pitstopData, String teamId) {
		messagingTemplate.convertAndSend(LIVE_PREFIX + teamId + "/pitdata", pitstopData);
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
					.sessionTime(TimeTools.shortDurationString(runData.getSessionTime()))
					.flags(runData.getFlags().stream()
							.map(FlagType::name)
							.collect(Collectors.toList()))
					.driverName(driver.getName())
					.raceSessionTime(controller.getCurrentRaceSessionTime().format(DateTimeFormatter.ofPattern(TimeTools.HH_MM_SS)))
					.remainingSessionTime(TimeTools.shortDurationString(controller.getRemainingSessionTime()))
					.availableLaps(String.format("%.2f", availableLaps))
					.availableLapsCssClass(lapsCssClass)
					.flagCssClass(!runData.getFlags().isEmpty() ? runData.getFlags().get(0).cssClass() : "")
					.timeOfDay(runData.getSessionToD().format(DateTimeFormatter.ofPattern(TimeTools.HH_MM_SS)))
					.lapNo(Integer.toUnsignedString(runData.getLapNo()))
					.timeInLap(TimeTools.longDurationString(runData.getTimeInLap()))
					.localClock(ZonedDateTime.now().format(DateTimeFormatter.ofPattern(TimeTools.HH_MM_SS_XXX)))
					.build());
		}
	}

	public void sendEventData(EventData eventData, String teamId) {
		if (liveTopics.containsKey(teamId)) {
			messagingTemplate.convertAndSend(LIVE_PREFIX + teamId + "/eventdata", EventDataView.builder()
					.sessionTime(TimeTools.shortDurationString(eventData.getSessionTime()))
					.timeOfDay(eventData.getSessionToD().format(DateTimeFormatter.ofPattern(TimeTools.HH_MM_SS)))
					.trackLocation(eventData.getTrackLocationType().name())
					.trackLocationCssClass(eventData.getTrackLocationType().getCssClass())
					.build());
		}
	}

	public void sendSyncData(SyncData syncData, String teamId) {
		messagingTemplate.convertAndSend(LIVE_PREFIX + teamId + "/syncdata", SyncDataView.builder()
				.driverId(syncData.getClientId())
				.timestamp(TimeTools.shortDurationString(syncData.getSessionTime()))
				.stateCssClass(getSyncState(syncData.getSessionTime(), syncData.getSessionTime()))
				.inCarCssClass(syncData.isInCar() ? "table-info" : "")
				.build());
	}

	@MessageMapping("/liveclient")
	@SendToUser("/live/client-ack")
	public SessionDataView respondAck(LiveClientMessage message) {
		log.info("Connect message from {}: {}", message.getTeamId(), message.getText());
		String teamId = message.getTeamId();
		if (!liveTopics.containsKey(teamId)) {
			liveTopics.put(teamId, LIVE_PREFIX + teamId);
		}
		SessionDataView sessionDataView = SessionDataView.getSessionDataView(getSessionControllerBySubscriptionId(teamId));
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

	@MessageMapping("/driverchange")
	public void respondDriverChange(DriverChangeMessage message) {
		SessionController controller = getSessionControllerBySubscriptionId(message.getTeamId());
		log.debug("Driver change message: {}", message);
		int finishedStopsCount = controller.getPitStops().size();
		int pitstopListIndex = Integer.parseInt(message.getSelectId().split("-")[1]);
		try {
			de.bausdorf.simcacing.tt.planning.model.Stint planToModify =
					PlanningTools.stintToModify(controller, pitstopListIndex - finishedStopsCount);
			if (planToModify != null) {
				log.debug("Changing stint: {}", planToModify);
				planToModify.setDriverName(message.getDriverName());
			}

			List<PitstopDataView> viewToSend = PitstopDataView.getPitstopDataView(controller);
			log.debug("{}", viewToSend);
			sendPitstopData(viewToSend, message.getTeamId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@MessageMapping("/servicechange")
	public void respondServiceChange(ServiceChangeMessage message) {
		SessionController controller = getSessionControllerBySubscriptionId(message.getTeamId());
		log.debug("Service change message: {}", message);

		int finishedStopsCount = controller.getPitStops().size();
		String[] msgParts = message.getCheckId().split("-");
		int pitstopListIndex = Integer.parseInt(msgParts[1]);
		PitStopServiceType serviceType = PitStopServiceType.fromCheckId(msgParts[0]);

		if (serviceType != null) {
			de.bausdorf.simcacing.tt.planning.model.Stint planToModify =
					PlanningTools.stintToModify(controller, pitstopListIndex - finishedStopsCount);
			if (planToModify != null) {
				log.debug("Changing stint: {}", planToModify);
				if (message.isChecked()) {
					planToModify.addService(serviceType);
				} else {
					planToModify.removeService(serviceType);
				}

				List<PitstopDataView> viewToSend = PitstopDataView.getPitstopDataView(controller);
				log.debug("{}", viewToSend);
				sendPitstopData(viewToSend, message.getTeamId());
			}
		} else {
			log.warn("Unknown checkId: {}", msgParts[0]);
		}
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
			sessionRepository.delete(key.getSessionId().toString());
		}
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
		log.info("Loading active sessions from repository");
		List<SessionController> activeSessions = sessionRepository.loadAll();
		if (activeSessions.isEmpty()) {
			log.info("No active sessions to load");
			return;
		}
		for (SessionController controller : activeSessions) {
			SessionKey key = SessionKey.builder()
					.teamId(controller.getTeamId())
					.sessionId(controller.getSessionData().getSessionId())
					.build();
			log.info("Loaded session {}", controller.getSessionData().getSessionId());
			controller.setRacePlan(selectRacePlan(controller.getSessionData(), controller.getTeamId(), controller.getSessionRegistered()));
			data.put(key, controller);
		}
		PlanningTools.configureServiceDuration(config);
	}

	private void processLapData(SessionController controller, String sessionId, LapData lapData) {
		try {
			controller.addLap(lapData);
			sessionRepository.saveLap(sessionId, lapData);
			sendLapData(lapData, controller, controller.getSessionData().getSessionId().getSubscriptionId());
		} catch( DuplicateLapException e) {
			log.warn(e.getMessage());
		}
	}

	private void processRunData(SessionController controller, String clientId, RunData runData) {
		IRacingDriver driver = driverRepository.findById(clientId)
				.orElse(IRacingDriver.builder()
						.id("unknown")
						.name("N.N.")
						.validated(false)
						.build());
		if (!driver.getId().equalsIgnoreCase("unknown") && !driver.isValidated()) {
			driver.setValidated(true);
			driverRepository.save(driver);
		}
		if (controller.getCurrentDriver() == null || !controller.getCurrentDriver().getId().equalsIgnoreCase(driver.getId())) {
			controller.setCurrentDriver(driver);
			sessionRepository.saveCurrentDriver(controller.getSessionData().getSessionId().toString(), driver);
		}
		if (controller.updateRunData(runData) && controller.getRacePlan() != null) {
			// real greenFlagTime changed !
			controller.getRacePlan().getPlanParameters().setGreenFlagOffsetTime(controller.getGreenFlagTime());
		}
		String subscriptionId = controller.getSessionData().getSessionId().getSubscriptionId();
		sendRunData(runData, controller, subscriptionId, driver);

		SyncData syncData = SyncData.builder()
				.isInCar(true)
				.sessionTime(runData.getSessionTime())
				.clientId(clientId)
				.build();
		controller.updateSyncData(syncData);
		sendSyncData(syncData, subscriptionId);
	}

	private void processEventData(SessionController controller, EventData eventData) {
		String sessionId = controller.getSessionData().getSessionId().toString();
		if (controller.getCurrentTrackLocation() == null
				|| controller.getCurrentTrackLocation() != eventData.getTrackLocationType()) {
			sessionRepository.saveTrackLocation(sessionId, eventData.getTrackLocationType());
		}
		String subscriptionId = controller.getSessionData().getSessionId().getSubscriptionId();
		Stint lastStint = controller.processEventData(eventData);
		if (lastStint != null) {
			Pitstop pitstop = controller.getLastPitstop();
			driverStatsRepository.addStatsEntry(
					controller.getCurrentDriver().getId(),
					controller.getSessionData().getTrackId(),
					controller.getSessionData().getCarId(),
					StatsEntry.builder()
							.avgFuelPerLap(lastStint.getAvgFuelPerLap())
							.avgLapTime(lastStint.getAvgLapTime())
							.avgTrackTemp(lastStint.getAvgTrackTemp())
							.todStart(lastStint.getTodStart())
							.todEnd(lastStint.getTodEnd())
							.stintLaps(lastStint.getLaps())
							.pitstop(pitstop)
							.build()
			);
			sessionRepository.savePitstop(sessionId, pitstop, lastStint);
			controller.getLastRecordedLap().ifPresent(lapData -> sessionRepository.saveLap(sessionId, lapData));
			sendPitstopData(PitstopDataView.getPitstopDataView(controller), subscriptionId);
		}
		sendEventData(eventData, subscriptionId);
	}

	private RacePlan selectRacePlan(SessionData sessionData, String teamId, ZonedDateTime sessionRegisteredTime) {
		List<RacePlanParameters> planParameters;
		if (teamId.equalsIgnoreCase("0")) {
			planParameters = planRepository.findByFieldValue(RacePlanParameters.TRACK_ID, sessionData.getTrackId());
		} else {
			planParameters = planRepository.findByTeamIds(Collections.singletonList(teamId));
		}
		planParameters = planParameters.stream()
				.filter(s -> s.getTrackId().equalsIgnoreCase(sessionData.getTrackId()))
				.filter(s -> s.getCarId().equalsIgnoreCase(sessionData.getCarId()))
				.collect(Collectors.toList());
		if (planParameters.isEmpty()) {
			log.info("No race plan available for session {}", sessionData.getSessionId().toString());
		} else if (planParameters.size() > 1) {
			log.warn("More than one race plan available for session {}", sessionData.getSessionId().toString());
		} else {
			RacePlanParameters serverZonedRacePlan = new RacePlanParameters(planParameters.get(0), ZoneId.systemDefault());
			if (config.isShiftSessionStartTimeToNow()) {
				serverZonedRacePlan.shiftSessionStartTime(ZonedDateTime.now().minusMinutes(1));
			} else {
				serverZonedRacePlan.shiftSessionStartTime(sessionRegisteredTime);
			}
			log.info("Using race plan {}", serverZonedRacePlan.getId());
			return RacePlan.createRacePlanTemplate(serverZonedRacePlan);
		}
		return null;
	}

	private ClientData validateAndConvert(ClientMessage message) {
		MessageValidator validator = validators.get(message.getType());
		if( validator != null ) {
			return validator.validate(message);
		}
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

	private static String getSyncState(Duration lastSync, Duration currentSync) {
		if (lastSync.plusSeconds(10).getSeconds() > currentSync.getSeconds()) {
			return TABLE_SUCCESS;
		} else if (lastSync.plusSeconds(30).getSeconds() > currentSync.getSeconds()) {
			return "table-warning";
		}
		return TABLE_DANGER;
	}

	private static String fuelString(double fuelAmount) {
		return String.format("%.3f", fuelAmount).replace(",", ".");
	}
}
