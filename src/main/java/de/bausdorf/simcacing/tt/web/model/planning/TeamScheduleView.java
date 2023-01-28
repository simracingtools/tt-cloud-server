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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.bausdorf.simcacing.tt.planning.persistence.ScheduleEntry;
import lombok.Data;

@Data
public class TeamScheduleView {
	private List<DriverScheduleView> teamSchedule;
	private String planId;

	public TeamScheduleView(String planId) {
		this.planId = planId;
		this.teamSchedule = new ArrayList<>();
	}

	public void addDriverScheduleView(ScheduleEntry scheduleEntry) {
		Optional<DriverScheduleView> existingDriverView = teamSchedule.stream()
				.filter(view -> view.getDriverId().equalsIgnoreCase(scheduleEntry.getDriver().getId()))
				.findFirst();
		ScheduleView scheduleView = ScheduleView.builder()
				.status(scheduleEntry.getStatus())
				.validFrom(scheduleEntry.getFromTime().toLocalDateTime())
				.build();
		existingDriverView.ifPresentOrElse(
				view -> view.getScheduleEntries().add(scheduleView),
				() -> teamSchedule.add(DriverScheduleView.builder()
								.driverName(scheduleEntry.getDriver().getName())
								.driverId(scheduleEntry.getDriver().getId())
								.validated(scheduleEntry.getDriver().isValidated())
								.scheduleEntries(new ArrayList<>(List.of(scheduleView)))
								.build())
		);
	}
}
