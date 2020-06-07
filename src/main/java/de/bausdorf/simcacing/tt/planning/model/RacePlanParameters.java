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

import de.bausdorf.simcacing.tt.planning.PlanningTools;
import de.bausdorf.simcacing.tt.stock.DriverRepository;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.util.TimeTools;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RacePlanParameters {

	public static final IRacingDriver NN_DRIVER = IRacingDriver.builder()
			.name("N.N")
			.id("0")
			.build();

	public static final String NAME = "name";
	public static final String TEAM_ID = "teamId";
	public static final String TRACK_ID = "trackId";
	public static final String CAR_ID = "carId";
	public static final String RACE_DURATION = "raceDuration";
	public static final String SESSION_START_TIME = "sessionStartTime";
	public static final String ID = "id";
	public static final String AGV_LAP_TIME = "agvLapTime";
	public static final String AVG_PIT_STOP_TIME = "avgPitStopTime";
	public static final String AVG_FUEL_PER_LAP = "avgFuelPerLap";
	public static final String MAX_CAR_FUEL = "maxCarFuel";
	public static final String TOD_START_TIME = "todStartTime";
	public static final String GREEN_FLAG_OFFSET_TIME = "greenFlagOffsetTime";
	public static final String STINTS = "stints";
	public static final String ROSTER = "roster";

	private String id;
	private String name;
	private String teamId;
	private String trackId;
	private String carId;
	private Duration raceDuration;
	private ZonedDateTime sessionStartTime;
	private LocalDateTime todStartTime;
	private Duration greenFlagOffsetTime;
	private Duration avgLapTime;
	private Duration avgPitLaneTime;
	private Double avgFuelPerLap;
	private Double maxCarFuel;
	private List<Stint> stints;
	private Roster roster;

	public RacePlanParameters(RacePlanParameters other, ZoneId displayZoneId) {
		this.name = other.name;
		this.id = other.id;
		this.teamId = other.teamId;
		this.trackId = other.trackId;
		this.carId = other.carId;
		this.raceDuration = other.raceDuration;
		this.sessionStartTime = other.sessionStartTime.withZoneSameInstant(displayZoneId);
		this.todStartTime = other.todStartTime;
		this.greenFlagOffsetTime = other.greenFlagOffsetTime;
		this.avgLapTime = other.avgLapTime;
		this.avgPitLaneTime = other.avgPitLaneTime;
		this.avgFuelPerLap = other.avgFuelPerLap;
		this.maxCarFuel = other.maxCarFuel;
		this.stints = new ArrayList<>();
		for (Stint stint : other.stints) {
			this.stints.add(Stint.builder()
					.todStartTime(stint.getTodStartTime())
					.driverName(stint.getDriverName())
					.startTime(stint.getStartTime().withZoneSameInstant(displayZoneId))
					.endTime(stint.getEndTime().withZoneSameInstant(displayZoneId))
					.laps(stint.getLaps())
					.refuelAmount(stint.getRefuelAmount())
					.pitStop(stint.getPitStop())
					.build());
		}
		this.roster = new Roster(other.roster, displayZoneId);
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put(ID, id);
		map.put(NAME, name);
		map.put(TEAM_ID, teamId);
		map.put(TRACK_ID, trackId);
		map.put(CAR_ID, carId);
		map.put(RACE_DURATION, raceDuration.toString());
		map.put(SESSION_START_TIME, sessionStartTime.toString());
		map.put(AGV_LAP_TIME, avgLapTime.toString());
		map.put(AVG_PIT_STOP_TIME, avgPitLaneTime.toString());
		map.put(AVG_FUEL_PER_LAP, avgFuelPerLap);
		map.put(MAX_CAR_FUEL, maxCarFuel);
		map.put(TOD_START_TIME, todStartTime.toString());
		map.put(GREEN_FLAG_OFFSET_TIME, greenFlagOffsetTime.toString());
		if (stints != null && !stints.isEmpty()) {
			Map<String, Object> stintMap = new HashMap<>();
			stints.forEach(s -> stintMap.put(Integer.toUnsignedString(stints.indexOf(s)), s.toMap()));
			map.put(STINTS, stintMap);
		}
		if (roster != null) {
			map.put(ROSTER, roster.toMap());
		}
		return map;
	}

	public void addDriver(IRacingDriver driver) {
		if (roster == null) {
			roster = new Roster();
		}
		roster.addDriver(driver);

		ScheduleEntry scheduleEntry = ScheduleEntry.builder()
				.driver(driver)
				.from(sessionStartTime)
				.status(ScheduleDriverOptionType.OPEN)
				.build();
		roster.addScheduleEntry(scheduleEntry);
	}

	public Estimation getGenericEstimation() {
		return Estimation.builder()
				.todFrom(todStartTime)
				.avgFuelPerLap(avgFuelPerLap)
				.avgLapTime(avgLapTime)
				.build();
	}

	public IRacingDriver getDriverByName(String name) {
		if (roster != null) {
			return roster.getDriverByName(name);
		}
		return null;
	}

	public List<IRacingDriver> getAvailableDrivers(Stint stint) {
		if (roster != null ) {
			return roster.getAvailableDrivers(stint.getStartTime()).stream()
					.filter(s -> roster.getDriverStatusAt(s.getId(), stint.getEndTime()) != ScheduleDriverOptionType.BLOCKED)
					.collect(Collectors.toList());
		}
		return Collections.singletonList(NN_DRIVER);
	}

	public List<IRacingDriver> getAllDrivers() {
		return roster != null ? roster.getDrivers() : new ArrayList<>();
	}

	public List<String> getAllDriverIds() {
		return roster != null ? roster.getDrivers().stream().map(IRacingDriver::getId).collect(Collectors.toList()) : new ArrayList<>();
	}

	public void setAllDriverIds(List<String> driverIds) {
		if (roster == null ) {
			roster = new Roster();
		}
		for (String driverId : driverIds) {
			if (!roster.hasDriverId(driverId)) {
				IRacingDriver driver = IRacingDriver.builder()
						.id(driverId)
						.build();

				roster.addScheduleEntry(ScheduleEntry.builder()
						.driver(driver)
						.from(sessionStartTime)
						.status(ScheduleDriverOptionType.OPEN)
						.build()
				);
			}
		}
	}

	public Estimation getDriverEstimationAt(IRacingDriver driver, LocalDateTime todDateTime) {
		if (roster != null) {
			Estimation estimation = roster.getDriverEstimationAt(driver, todDateTime);
			if (estimation != null) {
				return estimation;
			}
		}
		return getGenericEstimation();
	}

	public List<Estimation> getDriverEstimations(String driverId) {
		if (roster != null) {
			return roster.getDriverEstimations().get(driverId);
		}
		return new ArrayList<>();
	}

	public ScheduleDriverOptionType getDriverStatusAt(String driverId, ZonedDateTime time) {
		if (roster != null ) {
			return roster.getDriverStatusAt(driverId, time);
		}
		return ScheduleDriverOptionType.UNSCHEDULED;
	}

	public List<ScheduleEntry> getDriverSchedule(String driverId) {
		if (roster != null) {
			return roster.getDriverAvailability().get(driverId);
		}
		return new ArrayList<>();
	}

	public Estimation getDriverNameEstimationAt(String driverName, LocalDateTime todDateTime) {
		if (roster != null) {
			IRacingDriver driver = roster.getDriverByName(driverName);
			if (driver != null) {
				return getDriverEstimationAt(driver, todDateTime);
			}
		}
		return getGenericEstimation();
	}

	public LocalDate getSessionStartDate() {
		return sessionStartTime.toLocalDate();
	}

	public void setSessionStartDate(LocalDate date) {
		if( sessionStartTime == null ) {
			sessionStartTime = ZonedDateTime.of(date, LocalTime.MIN, TimeTools.GMT);
		} else {
			sessionStartTime = ZonedDateTime.of(date, sessionStartTime.toLocalTime(), sessionStartTime.getZone());
		}
	}

	public LocalTime getStartLocalTime() {
		return sessionStartTime.toLocalTime();
	}

	public void setStartLocalTime(LocalTime time) {
		if( sessionStartTime == null ) {
			sessionStartTime = ZonedDateTime.of(LocalDate.MIN, time, TimeTools.GMT);
		} else {
			sessionStartTime = ZonedDateTime.of(sessionStartTime.toLocalDate(), time, sessionStartTime.getZone());
		}
	}

	public ZoneId getTimeZone() {
		return sessionStartTime.getZone();
	}

	public void setTimeZone(ZoneId zone) {
		if (sessionStartTime == null) {
			sessionStartTime = ZonedDateTime.of(LocalDateTime.MIN, zone);
		} else {
			sessionStartTime = ZonedDateTime.of(sessionStartTime.toLocalDateTime(), zone);
		}
	}

	public LocalDate getTodStartDate() {
		return todStartTime.toLocalDate();
	}

	public void setTodStartDate(LocalDate date) {
		if( todStartTime == null ) {
			todStartTime = LocalDateTime.of(date, LocalTime.MIN);
		} else {
			todStartTime = LocalDateTime.of(date, todStartTime.toLocalTime());
		}
	}

	public LocalTime getStartTodTime() {
		return todStartTime.toLocalTime();
	}

	public void setStartTodTime(LocalTime time) {
		if( todStartTime == null ) {
			todStartTime = LocalDateTime.of(LocalDate.MIN, time);
		} else {
			todStartTime = LocalDateTime.of(todStartTime.toLocalDate(), time);
		}
	}

	public void updateData(RacePlanParameters update) {
		if (update.getAvgFuelPerLap() > 0) {
			avgFuelPerLap = update.getAvgFuelPerLap();
		}
		if (update.getAvgLapTime() != null) {
			avgLapTime = update.getAvgLapTime();
		}
		if (update.getGreenFlagOffsetTime() != null) {
			greenFlagOffsetTime = update.getGreenFlagOffsetTime();
		}
		if (update.getMaxCarFuel() > 0) {
			maxCarFuel = update.getMaxCarFuel();
		}
		if (update.getRaceDuration() != null) {
			raceDuration = update.getRaceDuration();
		}
		if (update.getSessionStartTime() != null) {
			updateSessionStartTime(update.getSessionStartTime());
		}
		if (update.getTeamId() != null) {
			teamId = update.getTeamId();
		}
		if (update.getTodStartTime() != null) {
			todStartTime = update.getTodStartTime();
		}
		if (update.getAvgPitLaneTime() != null) {
			avgPitLaneTime = update.getAvgPitLaneTime();
			PlanningTools.updatePitLaneDurations(avgPitLaneTime.dividedBy(2L), avgPitLaneTime.dividedBy(2L), this);
		}
		if (update.getCarId() != null) {
			carId = update.getCarId();
		}
		if (update.getTrackId() != null) {
			trackId = update.getTrackId();
		}
		if (update.getName() != null) {
			name = update.getName();
		}
		if (update.getRoster() != null) {
			mergeRoster(update.getRoster());
		}
	}

	public void updateDrivers(DriverRepository driverRepository) {
		if (roster == null ) return;

		for (IRacingDriver driver : roster.getDrivers()) {
			if (driver.getName() == null) {
				Optional<IRacingDriver> repoDriver = driverRepository.findById(driver.getId());
				repoDriver.ifPresent(iRacingDriver -> roster.updateDriverData(iRacingDriver));
			}
		}
	}

	public void shiftSessionStartTime(ZonedDateTime newSessionStart) {
		Duration timeShift = Duration.between(sessionStartTime, newSessionStart);
		updateSessionStartTime(newSessionStart);
		for (Stint stint : stints) {
			stint.setStartTime(stint.getStartTime().plus(timeShift));
			stint.setEndTime(stint.getEndTime().plus(timeShift));
		}
	}

	private void updateSessionStartTime(ZonedDateTime newStartTime) {
		if (roster != null) {
			for (List<ScheduleEntry> schedule : roster.getDriverAvailability().values()) {
				if (!schedule.isEmpty() && schedule.get(0).getFrom().isEqual(sessionStartTime)) {
					schedule.get(0).setFrom(newStartTime.withZoneSameInstant(sessionStartTime.getZone()));
				}
			}
		}
		sessionStartTime = newStartTime.withZoneSameInstant(sessionStartTime.getZone());
	}

	private void mergeRoster(Roster other) {
		// Check driver removal
		List<IRacingDriver> driversToRemove = new ArrayList<>();
		for (IRacingDriver driver : roster.getDrivers()) {
			if (!other.containsDriverId(driver.getId())) {
				driversToRemove.add(driver);
			}
		}
		for (IRacingDriver driver : driversToRemove) {
			roster.removeDriver(driver);
		}
		// Add and update existing
		List<IRacingDriver> driversToAdd = new ArrayList<>();
		for (IRacingDriver driver : other.getDrivers()) {
			if (!roster.containsDriverId(driver.getId())) {
				driversToAdd.add(driver);
			}
		}
		for (IRacingDriver driver : driversToAdd) {
			addDriver(driver);
		}
	}
}
