package de.bausdorf.simcacing.tt.planning.model;

import de.bausdorf.simcacing.tt.stock.DriverRepository;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
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
	private LocalDateTime sessionStartTime;
	private LocalDateTime todStartTime;
	private LocalTime greenFlagOffsetTime;
	private Duration avgLapTime;
	private Duration avgPitStopTime;
	private Double avgFuelPerLap;
	private Double maxCarFuel;
	private List<Stint> stints;
	private Roster roster;

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
		map.put(AVG_PIT_STOP_TIME, avgPitStopTime.toString());
		map.put(AVG_FUEL_PER_LAP, avgFuelPerLap);
		map.put(MAX_CAR_FUEL, maxCarFuel);
		map.put(TOD_START_TIME, todStartTime.toString());
		map.put(GREEN_FLAG_OFFSET_TIME, greenFlagOffsetTime.toString());
		if (stints != null && !stints.isEmpty()) {
			Map<String, Object> stintMap = new HashMap<>();
			stints.stream().forEach(s -> stintMap.put(Integer.toUnsignedString(stints.indexOf(s)), s.toMap()));
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

	public List<IRacingDriver> getAvailableDrivers(LocalDateTime forTime) {
		if (roster != null ) {
			return roster.getAvailableDrivers(forTime);
		}
		return Arrays.asList(NN_DRIVER);
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
			return roster.getDriverEstimationAt(driver, todDateTime);
		}
		return null;
	}

	public List<Estimation> getDriverEstimations(String driverId) {
		if (roster != null) {
			return roster.getDriverEstimations().get(driverId);
		}
		return new ArrayList<>();
	}

	public ScheduleDriverOptionType getDriverStatusAt(String driverId, LocalDateTime time) {
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
				Estimation estimation = roster.getDriverEstimationAt(driver, todDateTime);
				return estimation != null ? estimation : getGenericEstimation();
			}
		}
		return getGenericEstimation();
	}

	public LocalDate getSessionStartDate() {
		return sessionStartTime.toLocalDate();
	}

	public void setSessionStartDate(LocalDate date) {
		if( sessionStartTime == null ) {
			sessionStartTime = LocalDateTime.of(date, LocalTime.MIN);
		} else {
			sessionStartTime = LocalDateTime.of(date, sessionStartTime.toLocalTime());
		}
	}

	public LocalTime getStartLocalTime() {
		return sessionStartTime.toLocalTime();
	}

	public void setStartLocalTime(LocalTime time) {
		if( sessionStartTime == null ) {
			sessionStartTime = LocalDateTime.of(LocalDate.MIN, time);
		} else {
			sessionStartTime = LocalDateTime.of(sessionStartTime.toLocalDate(), time);
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
			sessionStartTime = update.getSessionStartTime();
		}
		if (update.getTeamId() != null) {
			teamId = update.getTeamId();
		}
		if (update.getTodStartTime() != null) {
			todStartTime = update.getTodStartTime();
		}
		if (update.getAvgPitStopTime() != null) {
			avgPitStopTime = update.getAvgPitStopTime();
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
		if (update.getStints() != null) {
			stints = update.getStints();
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
				if (repoDriver.isPresent()) {
					roster.updateDriverData(repoDriver.get());
				}
			}
		}
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
			roster.addScheduleEntry(ScheduleEntry.builder()
					.driver(driver)
					.from(sessionStartTime)
					.status(ScheduleDriverOptionType.OPEN)
					.build());
		}
	}
}
