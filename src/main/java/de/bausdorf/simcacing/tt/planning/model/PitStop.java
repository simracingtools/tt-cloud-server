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
import java.util.ArrayList;
import java.util.List;

import de.bausdorf.simcacing.tt.planning.PlanningTools;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PitStop {

	protected static final List<PitStopServiceType> defaultService;

	static {
		defaultService = new ArrayList<>();
		defaultService.add(PitStopServiceType.FUEL);
		defaultService.add(PitStopServiceType.TYRES);
		defaultService.add(PitStopServiceType.WS);
	}

	private List<PitStopServiceType> service;
	private Duration approach;
	private Duration depart;

	private Duration calculateServiceDuration(double amountRefuel) {
		return PlanningTools.calculateServiceDuration(service, amountRefuel);
	}

	public void addService(PitStopServiceType serviceType) {
		if (!service.contains(serviceType) ) {
			service.add(serviceType);
		}
	}

	public void removeService(PitStopServiceType serviceType) {
		service.remove(serviceType);
	}

	public Duration getOverallDuration(double amountRefuel) {
		return Duration.ZERO
				.plus(approach)
				.plus(calculateServiceDuration(amountRefuel))
				.plus(depart);
	}

	public static PitStop defaultPitStop() {
		return PitStop.builder()
				.approach(Duration.ofSeconds(10))
				.service(new ArrayList<>(defaultService))
				.depart(Duration.ofSeconds(5))
				.build();
	}
}
