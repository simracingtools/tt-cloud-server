package de.bausdorf.simcacing.tt.web;

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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import de.bausdorf.simcacing.tt.planning.PlanningTools;
import de.bausdorf.simcacing.tt.planning.RacePlanRepository;
import de.bausdorf.simcacing.tt.planning.model.PitStopServiceType;
import de.bausdorf.simcacing.tt.planning.model.RacePlanParameters;
import de.bausdorf.simcacing.tt.planning.model.Stint;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.util.TimeTools;
import de.bausdorf.simcacing.tt.web.model.planning.DriverChangeMessage;
import de.bausdorf.simcacing.tt.web.model.planning.PlanningClientMessage;
import de.bausdorf.simcacing.tt.web.model.planning.ServiceChangeMessage;
import de.bausdorf.simcacing.tt.web.model.planning.StintDriverView;
import de.bausdorf.simcacing.tt.web.model.planning.StintView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class StintPlanController {

	private static final String[] DRIVER_COLORS = {
			"background-color: rgb(235,100,100);color: rgb(249,246,246);",
			"background-color: rgb(100,235,100);",
			"background-color: rgb(59,59,255);color: rgb(249,246,246);",
			"background-color: rgb(230,100,220);color: rgb(249,246,246);",
			"background-color: rgb(255,182,141);",
			"background-color: rgb(225,217,29);",
			"background-color: rgb(29,225,213);",
			"background-color: rgb(29,225,178);",
			"background-color: rgb(116,119,118);",
			"background-color: rgb(181,204,196);",
	};

	private final SimpMessagingTemplate messagingTemplate;
	private final RacePlanRepository planRepository;

	public StintPlanController(@Autowired SimpMessagingTemplate messagingTemplate,
			@Autowired RacePlanRepository planRepository) {
		this.messagingTemplate = messagingTemplate;
		this.planRepository = planRepository;
	}

	@MessageMapping("/driverplanchange")
	@SendTo("")
	public void changeDriverPlanning(DriverChangeMessage message) {
		Optional<RacePlanParameters> racePlanParameters = planRepository.findById(message.getPlanId());
		if (racePlanParameters.isPresent() ) {
			int stintListIndex = Integer.parseInt(message.getSelectId().split("-")[1]);
			try {
				Stint selectedStint = racePlanParameters.get().getStints().get(stintListIndex);
				selectedStint.setDriverName(message.getDriverName());

				recalculateSaveAndSend(racePlanParameters.get(), message.getPlanId());
			} catch (IndexOutOfBoundsException e) {
				log.error("Selected stint index {} is out of bounds", stintListIndex);
			}
		}

	}

	@MessageMapping("/serviceplanchange")
	public void changeServicePlanning(ServiceChangeMessage message) {
		Optional<RacePlanParameters> racePlanParameters = planRepository.findById(message.getPlanId());
		if (racePlanParameters.isPresent() ) {
			String[] msgParts = message.getCheckId().split("-");
			int stintListIndex = Integer.parseInt(msgParts[1]);
			PitStopServiceType serviceType = PitStopServiceType.fromCheckId(msgParts[0]);
			try {
				Stint selectedStint = racePlanParameters.get().getStints().get(stintListIndex);
				if (message.isChecked()) {
					selectedStint.addService(serviceType);
				} else {
					selectedStint.removeService(serviceType);
				}

				recalculateSaveAndSend(racePlanParameters.get(), message.getPlanId());
			} catch (IndexOutOfBoundsException e) {
				log.error("Selected stint index {} is out of bounds", stintListIndex);
			}
		}
	}

	@MessageMapping("/planclient")
	@SendToUser("/plan/client-ack")
	public List<StintView> clientConnected(PlanningClientMessage message) {
		Optional<RacePlanParameters> racePlanParameters = planRepository.findById(message.getPlanId());
		return racePlanParameters.map(this::createStintViewList).orElseGet(ArrayList::new);
	}

	public void sendPitstopData(List<StintView> stintData, String planId) {
		messagingTemplate.convertAndSend("/plan/" + planId + "/stinttable", stintData);
	}

	private void recalculateSaveAndSend(RacePlanParameters racePlanParameters, String planId) {
		PlanningTools.recalculateStints(racePlanParameters);
		planRepository.save(racePlanParameters);
		sendPitstopData(createStintViewList(racePlanParameters), planId);
	}

	private List<StintView> createStintViewList(RacePlanParameters racePlanParameters) {
		List<StintView> stintsViews = new ArrayList<>();

		// Select driver colors
		Map<String, String> driverColors = new HashMap<>();
		int colorIndex = 0;
		for (IRacingDriver driver : racePlanParameters.getAllDrivers()) {
			driverColors.put(driver.getName(), DRIVER_COLORS[colorIndex++ % 9]);
		}

		for (Stint stint : racePlanParameters.getStints()) {
			StintView stintView = StintView.builder()
					.selectedDriver(StintDriverView.builder()
							.driverName(stint.getDriverName())
							.colorStyle(driverColors.get(stint.getDriverName()))
							.build())
					.startLocal(stint.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
					.startToD(stint.getTodStartTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
					.laps(Integer.toUnsignedString(stint.getLaps()))
					.duration(TimeTools.shortDurationString(stint.getStintDuration(true)))
					.availableDrivers(racePlanParameters.getAvailableDrivers(stint).stream()
							.map(s -> StintDriverView.builder()
									.driverName(s.getName())
									.driverId(s.getId())
									.colorStyle(driverColors.get(s.getName()))
									.availableStyle(racePlanParameters.getDriverStatusAt(s.getId(), stint.getEndTime()).cssClassName())
									.build())
							.collect(Collectors.toList())
					)
					.lastStint(racePlanParameters.getStints().indexOf(stint) == (racePlanParameters.getStints().size()-1))
					.refuel(stint.hasService(PitStopServiceType.FUEL))
					.changeTyres(stint.hasService(PitStopServiceType.TYRES))
					.clearWindshield(stint.hasService(PitStopServiceType.WS))
					.build();

			stintsViews.add(stintView);
		}
		return stintsViews;
	}
}
