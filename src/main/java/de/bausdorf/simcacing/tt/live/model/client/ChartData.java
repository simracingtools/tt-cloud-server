package de.bausdorf.simcacing.tt.live.model.client;

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
import java.util.Locale;

import de.bausdorf.simcacing.tt.util.TimeTools;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ChartData {
	private List<Integer> lapNos;
	private List<String> temps;
	private List<String> laps;

	public ChartData() {
		this.lapNos = new ArrayList<>();
		this.laps = new ArrayList<>();
		this.temps = new ArrayList<>();
	}

	public void addLap(LapData lap) {
		lapNos.add(lap.getNo());
		temps.add(String.format(Locale.US,"%.1f", lap.getTrackTemp()));
		laps.add(lap.getLapTime().getSeconds() > Duration.ZERO.getSeconds()
				? TimeTools.longDurationString(lap.getLapTime()) : "");
	}
}
