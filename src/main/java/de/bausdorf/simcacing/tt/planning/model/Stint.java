package de.bausdorf.simcacing.tt.planning.model;

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
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import de.bausdorf.simcacing.tt.planning.PlanningTools;
import de.bausdorf.simcacing.tt.util.TimeTools;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@Builder
@ToString
public class Stint {

	public static final String DRIVER_NAME = "driverName";
	public static final String TOD_START_TIME = "todStartTime";
	public static final String START_TIME = "startTime";
	public static final String END_TIME = "endTime";
	public static final String REFUEL_AMOUNT = "refuelAmount";
	public static final String LAPS = "laps";
	public static final String PITSTOP_SERVICE = "pitstopService";

	private String driverName;
	private LocalDateTime todStartTime;
	private ZonedDateTime startTime;
	private ZonedDateTime endTime;
	private double refuelAmount;
	private int laps;
	private List<PitStopServiceType> service;
	private boolean lastStint;


	public Duration getStintDuration(boolean includePitStopTimes) {
		if (startTime != null && endTime != null) {
			if (!includePitStopTimes) {
				return Duration.between(startTime, endTime).minus(PlanningTools.calculateServiceDuration(service, refuelAmount));
			}
			return Duration.between(startTime, endTime);
		}
		return Duration.ZERO;
	}

	public String getStintDurationString(boolean includePitStopTimes) {
		return TimeTools.shortDurationString(getStintDuration(includePitStopTimes));
	}

	public String getStartTimeString() {
		return getStartTime().format(DateTimeFormatter.ofPattern(TimeTools.HH_MM_SS));
	}

	public String getTodStartTimeString() {
		return getTodStartTime().format(DateTimeFormatter.ofPattern(TimeTools.HH_MM_SS));
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();

		map.put(DRIVER_NAME, driverName);
		map.put(TOD_START_TIME, todStartTime.toString());
		map.put(START_TIME, startTime.toString());
		map.put(END_TIME, endTime.toString());
		map.put(REFUEL_AMOUNT, refuelAmount);
		map.put(LAPS, laps);
		map.put(PITSTOP_SERVICE, service != null ?
				service.stream().map(Enum::name).collect(Collectors.toList()) : null
		);

		return map;
	}

	public String shortInfo() {
		StringBuilder out = new StringBuilder()
				.append(startTime.toString()).append(": ")
				.append(driverName).append(" (")
				.append(laps).append(") ")
				.append(getStintDuration(true));
		return out.toString();
	}
}
