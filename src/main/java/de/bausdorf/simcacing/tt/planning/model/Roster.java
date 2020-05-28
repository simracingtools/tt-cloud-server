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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.util.TimeTools;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Roster {

	public static final String NAME = "name";
	public static final String SCHEDULE = "schedule";
	public static final String ESTIMATIONS = "estimations";

	List<IRacingDriver> drivers;
	Map<String, List<ScheduleEntry>> driverAvailability;
	Map<String, List<Estimation>> driverEstimations;

	public Roster() {
		this.drivers = new ArrayList<>();
		this.driverAvailability = new HashMap<>();
		this.driverEstimations = new HashMap<>();
	}

	public Roster(Roster other, ZoneId displayZoneId) {
		this();
		this.drivers.addAll(other.drivers);
		this.driverEstimations.putAll(other.driverEstimations);
		for (Map.Entry<String, List<ScheduleEntry>> entry : other.getDriverAvailability().entrySet()) {
			List<ScheduleEntry> scheduleEntryList = new ArrayList<>();
			for (ScheduleEntry scheduleEntry : entry.getValue()) {
				scheduleEntryList.add(ScheduleEntry.builder()
						.status(scheduleEntry.getStatus())
						.driver(scheduleEntry.getDriver())
						.from(scheduleEntry.getFrom().withZoneSameInstant(displayZoneId))
						.build());
			}
			this.driverAvailability.put(entry.getKey(), scheduleEntryList);
		}
	}

	public Roster(Map<String, Object> data) {
		this();
		if( data == null ) {
			return;
		}
		for (Map.Entry<String,Object> mapEntry : data.entrySet()) {
			Map<String, Object> driverMap = (Map<String, Object>)mapEntry.getValue();
			IRacingDriver driver = IRacingDriver.builder()
					.id(mapEntry.getKey())
					.name((String)driverMap.get(NAME))
					.build();
			drivers.add(driver);

			Map<String, Object> estimationsMap = (Map<String, Object>)driverMap.get(ESTIMATIONS);
			if (estimationsMap != null) {
				List<Estimation> estimationList = new ArrayList<>();
				for (Map.Entry<String, Object> estimationMapEntry : estimationsMap.entrySet()) {
					Map<String, Object> estimationMap = (Map<String, Object>) estimationMapEntry.getValue();
					Estimation estimation = Estimation.builder()
							.avgFuelPerLap((Double) estimationMap.get(Estimation.AVG_FUEL_PER_LAP))
							.avgLapTime(Duration.parse((String) estimationMap.get(Estimation.AVG_LAP_TIME)))
							.driver(driver)
							.todFrom(LocalDateTime.parse(estimationMapEntry.getKey()))
							.build();
					estimationList.add(estimation);
					estimationList = sortEstimations(estimationList);
					driverEstimations.put(mapEntry.getKey(), estimationList);
				}
			}


			Map<String, Object> availabilitiesMap = (Map<String, Object>)driverMap.get(SCHEDULE);
			if (availabilitiesMap != null) {
				List<ScheduleEntry> availabilityList = new ArrayList<>();
				for (Map.Entry<String, Object> availabilityMapEntry : availabilitiesMap.entrySet()) {
					Map<String, Object> availabilityMap = (Map<String, Object>) availabilityMapEntry.getValue();
					ScheduleEntry entry = ScheduleEntry.builder()
							.driver(driver)
							.from(TimeTools.zonedDateTimeFromString(availabilityMapEntry.getKey()))
							.status(ScheduleDriverOptionType.valueOf((String) availabilityMap.get(ScheduleEntry.STATUS)))
							.build();
					availabilityList.add(entry);
				}
				availabilityList = sortScheduleEntries(availabilityList);
				driverAvailability.put(mapEntry.getKey(), availabilityList);
			}
		}
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		for (IRacingDriver driver : drivers) {
			Map<String, Object> driverMap = new HashMap<>();

			Map<String, Object> availabilityMap = new HashMap<>();
			List<ScheduleEntry> driverSchedule = driverAvailability.get(driver.getId());
			if( driverSchedule != null ) {
				for (ScheduleEntry entry : driverSchedule) {
					availabilityMap.put(entry.getFrom().toString(), entry.toMap());
				}
				driverMap.put(SCHEDULE, availabilityMap);
			}

			Map<String, Object> estimationMap = new HashMap<>();
			List<Estimation> driverEstimationsList = driverEstimations.get(driver.getId());
			if( driverEstimationsList != null ) {
				for (Estimation estimation : driverEstimationsList) {
					estimationMap.put(estimation.getTodFrom().toString(), estimation.toMap());
				}
				driverMap.put(ESTIMATIONS, estimationMap);
			}
			driverMap.put(NAME, driver.getName());
			map.put(driver.getId(), driverMap);
		}
		return map;
	}

	public void addDriver(IRacingDriver driver) {
		if (!drivers.contains(driver)) {
			drivers.add(driver);
		}
	}

	public void removeDriver(IRacingDriver driver) {
		if (!drivers.contains(driver)) {
			return;
		}
		drivers.remove(driver);
		driverAvailability.remove(driver.getId());
		driverEstimations.remove(driver.getId());
	}

	public boolean containsDriverId(String driverId) {
		for (IRacingDriver driver : drivers) {
			if (driver.getId().equalsIgnoreCase(driverId)) {
				return true;
			}
		}
		return false;
	}

	public void updateDriverData(IRacingDriver driverToUpdate) {
		for (IRacingDriver driver : drivers) {
			if (driver.getId().equalsIgnoreCase(driverToUpdate.getId())) {
				driver.setName(driverToUpdate.getName());
				driver.setValidated(driverToUpdate.isValidated());
			}

			List<Estimation> driverEstimationList = driverEstimations.get(driver.getId());
			if (driverEstimationList != null) {
				driverEstimationList.forEach(s -> s.setDriver(driver));
			}

			List<ScheduleEntry> driverScheduleList = driverAvailability.get(driver.getId());
			if (driverScheduleList != null) {
				driverScheduleList.forEach(s -> s.setDriver(driver));
			}
		}
	}

	public boolean hasDriverId(String driverId) {
		if (driverId == null) {
			return false;
		}
		for (IRacingDriver driver : drivers) {
			if (driverId.equalsIgnoreCase(driver.getId())) {
				return true;
			}
		}
		return false;
	}

	public void addEstimation(Estimation estimation) {
		IRacingDriver driver = estimation.getDriver();
		addDriver(driver);

		List<Estimation> estimationList;
		if (driverEstimations.containsKey(driver.getId())) {
			estimationList = driverEstimations.get(driver.getId());
		} else {
			estimationList = new ArrayList<>();
			driverEstimations.put(driver.getId(), estimationList);
		}
		estimationList.add(estimation);
		sortEstimations(estimationList);
	}

	public void addScheduleEntry(ScheduleEntry entry) {
		IRacingDriver driver = entry.getDriver();
		addDriver(driver);

		List<ScheduleEntry> schedule;
		if (driverAvailability.containsKey(driver.getId())) {
			schedule = driverAvailability.get(driver.getId());
		} else {
			schedule = new ArrayList<>();
			driverAvailability.put(driver.getId(), schedule);
		}
		schedule.add(entry);
		sortScheduleEntries(schedule);
	}

	public Estimation getDriverEstimationAt(IRacingDriver driver, LocalDateTime todDateTime) {
		if (driverEstimations == null ) {
			return null;
		}
		List<Estimation> estimationList = driverEstimations.get(driver.getId());
		if (estimationList == null) {
			return null;
		}
		Estimation last = null;
		for (Estimation estimation : estimationList) {
			if (estimation.getTodFrom().isAfter(todDateTime)) {
				break;
			}
			last = estimation;
		}
		return last;
	}

	public List<IRacingDriver> getAvailableDrivers(ZonedDateTime forTime) {
		List<IRacingDriver> availableDrivers = new ArrayList<>();
		for (Map.Entry<String, List<ScheduleEntry>> scheduleMapEntry : driverAvailability.entrySet()) {
			ScheduleEntry last = null;
			for (ScheduleEntry scheduleEntry : scheduleMapEntry.getValue()) {
				if (scheduleEntry.getFrom().isAfter(forTime)) {
					break;
				}
				last = scheduleEntry;
			}
			if( last != null && last.getStatus() != ScheduleDriverOptionType.BLOCKED) {
				availableDrivers.add(getDriverById(scheduleMapEntry.getKey()));
			}
		}
		return availableDrivers;
	}

	public ScheduleDriverOptionType getDriverStatusAt(String driverId, ZonedDateTime time) {
		List<ScheduleEntry> scheduleEntryList = driverAvailability.get(driverId);
		if (scheduleEntryList != null ) {
			ScheduleEntry last = null;
			for (ScheduleEntry scheduleEntry : scheduleEntryList) {
				if (scheduleEntry.getFrom().isAfter(time)) {
					break;
				}
				last = scheduleEntry;
			}
			return last != null ? last.getStatus() : ScheduleDriverOptionType.UNSCHEDULED;
		}
		return ScheduleDriverOptionType.UNSCHEDULED;
	}

	public IRacingDriver getDriverByName(String name) {
		for (IRacingDriver driver : drivers) {
			if (driver.getName().equalsIgnoreCase(name)) {
				return driver;
			}
		}
		return null;
	}

	public IRacingDriver getDriverById(String id) {
		for (IRacingDriver driver : drivers) {
			if (driver.getId().equalsIgnoreCase(id)) {
				return driver;
			}
		}
		return null;
	}

	private List<ScheduleEntry> sortScheduleEntries(List<ScheduleEntry> schedule) {
		return schedule.stream().sorted(Comparator.comparing(ScheduleEntry::getFrom)).collect(Collectors.toList());
	}

	private List<Estimation> sortEstimations(List<Estimation> estimations) {
		return estimations.stream().sorted(Comparator.comparing(Estimation::getTodFrom)).collect(Collectors.toList());
	}
}
