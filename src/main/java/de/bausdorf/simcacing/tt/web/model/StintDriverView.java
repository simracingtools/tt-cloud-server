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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.bausdorf.simcacing.tt.planning.model.PitStop;
import de.bausdorf.simcacing.tt.planning.model.PitStopServiceType;
import de.bausdorf.simcacing.tt.planning.model.RacePlanParameters;
import de.bausdorf.simcacing.tt.planning.model.Stint;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StintDriverView {
	private String planId;
	private List<String> stintDrivers;
	private List<String> styles;
	private List<Boolean> fuel;
	private List<Boolean> ws;
	private List<Boolean> tyres;

	public StintDriverView(RacePlanParameters planParameters) {
		this.planId = planParameters.getId();
		this.stintDrivers = planParameters.getStints().stream()
				.map(Stint::getDriverName)
				.collect(Collectors.toList());
		this.styles = new ArrayList<>();
		this.fuel = new ArrayList<>();
		this.ws = new ArrayList<>();
		this.tyres = new ArrayList<>();
		for (Stint stint : planParameters.getStints()) {
			PitStop pitstop = stint.getPitStop().orElse(null);
			if (pitstop != null) {
				fuel.add(pitstop.getService().contains(PitStopServiceType.FUEL));
				ws.add(pitstop.getService().contains(PitStopServiceType.WS));
				tyres.add(pitstop.getService().contains(PitStopServiceType.TYRES));
			} else {
				fuel.add(false);
				ws.add(false);
				tyres.add(false);
			}
		}
	}

	public List<PitStopServiceType> getPitService(int index) {
		List<PitStopServiceType> serviceList = new ArrayList<>();
		if (index < ws.size() && Boolean.TRUE.equals(ws.get(index))) {
				serviceList.add(PitStopServiceType.WS);
		}
		if (index < fuel.size() && Boolean.TRUE.equals(fuel.get(index))) {
				serviceList.add(PitStopServiceType.FUEL);
		}
		if (index < tyres.size() && Boolean.TRUE.equals(tyres.get(index))) {
				serviceList.add(PitStopServiceType.TYRES);
		}
		return serviceList;
	}
}
