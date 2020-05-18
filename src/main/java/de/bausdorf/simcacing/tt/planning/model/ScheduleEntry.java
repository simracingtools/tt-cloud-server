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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ScheduleEntry {

	public static final String FROM = "from";
	public static final String STATUS = "status";

	private ZonedDateTime from;
	private IRacingDriver driver;
	private ScheduleDriverOptionType status;

	public String getDriverName() {
		return driver.getName();
	}

	public LocalTime getFromTime() {
		return from.toLocalTime();
	}

	public void setFromTime(LocalTime time) {
		from = ZonedDateTime.of(from.toLocalDate(), time, from.getZone());
	}

	public LocalDate getFromDate() {
		return from.toLocalDate();
	}

	public void setFromDate(LocalDate date) {
		from = ZonedDateTime.of(date, from.toLocalTime(), from.getZone());
	}

	public ZoneId getFromZoneId() {
		return from.getZone();
	}

	public void setFromZone(ZoneId zone) {
		from = ZonedDateTime.of(from.toLocalDateTime(), zone);
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put(FROM, from.toString());
		map.put(STATUS, status.name());
		return map;
	}
}
