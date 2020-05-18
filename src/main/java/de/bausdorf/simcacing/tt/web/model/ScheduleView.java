package de.bausdorf.simcacing.tt.web.model;

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
import java.time.ZonedDateTime;

import de.bausdorf.simcacing.tt.planning.model.ScheduleDriverOptionType;
import de.bausdorf.simcacing.tt.util.TimeTools;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleView {
	private ZonedDateTime validFrom;
	private ScheduleDriverOptionType status;

	public LocalDate getValidFromDate() {
		return validFrom.toLocalDate();
	}

	public void setValidFromDate(LocalDate date) {
		if (validFrom == null) {
			validFrom = ZonedDateTime.of(date, LocalTime.MIN, TimeTools.GMT);
		} else {
			validFrom = ZonedDateTime.of(date, validFrom.toLocalTime(), validFrom.getZone());
		}
	}

	public LocalTime getValidFromTime() {
		return validFrom.toLocalTime();
	}

	public void setValidFromTime(LocalTime time) {
		if (validFrom == null) {
			validFrom = ZonedDateTime.of(LocalDate.MIN, time, TimeTools.GMT);
		} else {
			validFrom = ZonedDateTime.of(validFrom.toLocalDate(), time, validFrom.getZone());
		}
	}
}
