package de.bausdorf.simcacing.tt.live.model.client;

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

import de.bausdorf.simcacing.tt.util.MapTools;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Pitstop {

	public static final String STINT_NO = "stintNo";
	public static final String LAP = "lap";
	public static final String DRIVER_NAME = "driverName";
	public static final String ENTER_PITS = "enterPits";
	public static final String STOP_MOVING = "stopMoving";
	public static final String START_MOVING = "startMoving";
	public static final String EXIT_PITS = "exitPits";
	public static final String SERVICE_FLAGS = "serviceFlags";
	public static final String REPAIR_TIME = "repairTime";
	public static final String OPT_REPAIR_TIME = "optRepairTime";
	public static final String TOW_TIME = "towTime";
	public static final String FUEL_LEFT = "fuelLeft";
	public static final String REFUEL_AMOUNT = "refuelAmount";

	private int stint;
	private int lap;
	private String driver;
	private LocalTime enterPits;
	private LocalTime stopMoving;
	private LocalTime startMoving;
	private LocalTime exitPits;
	private List<ServiceFlagType> serviceFlags;
	private Duration repairTime;
	private Duration optRepairTime;
	private Duration towTime;
	private double fuelLeft;
	private double refuelAmount;

	public Pitstop(Map<String, Object> data) {
		this.stint = MapTools.intFromMap(STINT_NO, data);
		this.lap = MapTools.intFromMap(LAP, data);
		this.driver = MapTools.stringFromMap(DRIVER_NAME, data);
		this.enterPits = MapTools.timeFromMap(ENTER_PITS, data);
		this.stopMoving = MapTools.timeFromMap(STOP_MOVING, data);
		this.startMoving = MapTools.timeFromMap(START_MOVING, data);
		this.exitPits = MapTools.timeFromMap(EXIT_PITS, data);
		fillFlagListFromStringList(MapTools.stringListFromMap(SERVICE_FLAGS, data));
		this.repairTime = MapTools.durationFromMap(REPAIR_TIME, data);
		this.optRepairTime = MapTools.durationFromMap(OPT_REPAIR_TIME, data);
		this.towTime = MapTools.durationFromMap(TOW_TIME, data);
		this.fuelLeft = MapTools.doubleFromMap(FUEL_LEFT, data);
		this.refuelAmount = MapTools.doubleFromMap(REFUEL_AMOUNT, data);
	}

	public Map<String, Object> toMap() {
		Map<String, Object> data = new HashMap<>();

		data.put(STINT_NO, stint);
		data.put(LAP, lap);
		data.put(DRIVER_NAME, driver);
		data.put(ENTER_PITS, enterPits != null ? enterPits.toString() : null);
		data.put(STOP_MOVING, stopMoving != null ? stopMoving.toString() : null);
		data.put(START_MOVING, startMoving != null ? startMoving.toString() : null);
		data.put(EXIT_PITS, exitPits != null ? exitPits.toString() : null);
		data.put(SERVICE_FLAGS, serviceFlags != null ?
				serviceFlags.stream().map(ServiceFlagType::name).collect(Collectors.toList()): null);
		data.put(REPAIR_TIME, repairTime != null ? repairTime.toString() : null);
		data.put(OPT_REPAIR_TIME, optRepairTime != null ? optRepairTime.toString() : null);
		data.put(TOW_TIME, towTime != null ? towTime.toString() : null);
		data.put(FUEL_LEFT, fuelLeft);
		data.put(REFUEL_AMOUNT, refuelAmount);

		return data;
	}

	public boolean isComplete() {
		return (enterPits != null && exitPits != null);
	}

	public Duration getRepairAndTowingTime() {
		Duration accumulatedDuration = Duration.ZERO;
		if (repairTime != null) {
			accumulatedDuration = accumulatedDuration.plus(repairTime);
		}
		if (optRepairTime != null) {
			accumulatedDuration = accumulatedDuration.plus(optRepairTime);
		}
		if (towTime != null) {
			accumulatedDuration = accumulatedDuration.plus(towTime);
		}
		return accumulatedDuration;
	}

	public Duration getPitstopDuration() {
		if( enterPits != null && exitPits != null ) {
			return Duration.between(enterPits, exitPits);
		}
		return Duration.ZERO;
	}

	public Duration getPitstopServiceTime() {
		if( stopMoving != null && startMoving != null ) {
			return Duration.between(stopMoving, startMoving);
		}
		return Duration.ZERO;
	}

	public String getServiceFlagsString() {
		if (serviceFlags == null) {
			return "";
		}
		StringBuilder flags = new StringBuilder();
		for (ServiceFlagType flag : serviceFlags) {
			flags.append(flag.code).append(' ');
		}
		return flags.toString();
	}

	public Duration getApproachDuration() {
		if (stopMoving != null && enterPits != null) {
			return Duration.between(enterPits, stopMoving);
		}
		return Duration.ZERO;
	}

	public Duration getDepartDuration() {
		if (startMoving != null && exitPits != null) {
			return Duration.between(startMoving, exitPits);
		}
		return Duration.ZERO;
	}

	public boolean update(EventData event, double fuelLevel) {
		switch( event.getTrackLocationType() ) {
			case APPROACHING_PITS:
				if( enterPits == null ) {
					enterPits = LocalTime.now();
				}
				if( startMoving == null && stopMoving != null ) {
					startMoving = LocalTime.now();
				}
				updateRepairAndTowTimes(event);
				break;
			case PIT_STALL:
				if( stopMoving == null ) {
					stopMoving = LocalTime.now();
					serviceFlags = event.getServiceFlags();
					fuelLeft = fuelLevel;
				}
				updateRepairAndTowTimes(event);
				break;
			case ONTRACK:
				if( exitPits != null ) {
					// Pitstop already completed
					break;
				} else {
					exitPits = LocalTime.now();
					refuelAmount = fuelLevel - fuelLeft;
					return true;
				}
			case OFF_WORLD:
			case OFFTRACK:
				if( exitPits != null ) {
					// Pitstop already completed
					break;
				} else {
					updateRepairAndTowTimes(event);
				}
		}
		return false;
	}

	private void updateRepairAndTowTimes(EventData event) {
		if( optRepairTime == null && !event.getOptRepairTime().isZero()) {
			optRepairTime = event.getOptRepairTime();
		}
		if( repairTime == null && !event.getRepairTime().isZero() ) {
			repairTime = event.getRepairTime();
		}
		if( towTime == null && !event.getTowingTime().isZero()) {
			towTime = event.getTowingTime();
		}
	}

	private void fillFlagListFromStringList(List<String> names) {
		if (names != null) {
			serviceFlags = new ArrayList<>();
			for (String name : names) {
				serviceFlags.add(ServiceFlagType.valueOf(name));
			}
		}
	}
}
