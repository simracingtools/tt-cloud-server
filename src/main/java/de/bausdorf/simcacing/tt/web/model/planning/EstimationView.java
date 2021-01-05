package de.bausdorf.simcacing.tt.web.model.planning;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EstimationView {
	private LocalDateTime validFrom;
	private Duration avgLapTime;
	private Double avgFuelPerLap;

	public LocalDate getValidFromDate() {
		return validFrom.toLocalDate();
	}

	public void setValidFromDate(LocalDate date) {
		if (validFrom == null) {
			validFrom = LocalDateTime.of(date, LocalTime.MIN);
		} else {
			validFrom = LocalDateTime.of(date, validFrom.toLocalTime());
		}
	}

	public LocalTime getValidFromTime() {
		return validFrom.toLocalTime();
	}

	public void setValidFromTime(LocalTime time) {
		if (validFrom == null) {
			validFrom = LocalDateTime.of(LocalDate.MIN, time);
		} else {
			validFrom = LocalDateTime.of(validFrom.toLocalDate(), time);
		}
	}
}
