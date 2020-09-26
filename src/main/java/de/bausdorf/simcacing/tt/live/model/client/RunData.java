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
import java.time.LocalTime;
import java.util.List;

import de.bausdorf.simcacing.tt.live.clientapi.ClientData;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RunData implements ClientData {

	private Duration sessionTime;
	private LocalTime sessionToD;
	private double fuelLevel;
	private List<FlagType> flags;
	private Duration estLapTime;
	private int lapNo;
	private Duration timeInLap;
	private Duration sessionTimeRemaining;
	private int lapsRemaining;
	private SessionStateType sessionState;

	public boolean isGreenFlag() {
		return flags.contains(FlagType.GREEN);
	}
}
