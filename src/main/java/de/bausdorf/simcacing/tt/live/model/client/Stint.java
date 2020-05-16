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

import lombok.*;

import java.time.Duration;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class Stint {

	private final int no;
	private int laps;
	@Setter
	private String driver;
	@Setter
	private Duration avgLapTime;
	@Setter
	private double avgFuelPerLap;
	@Setter
	private double lastLapFuel;
	@Setter
	private Duration lastLaptime;
	@Setter
	private double availableLaps;
	@Setter
	private double maxLaps;
	@Setter
	private Duration currentStintDuration;
	@Setter
	private Duration expectedStintDuration;
	@Setter
	private LocalTime todStart;
	@Setter
	private LocalTime todEnd;
	@Setter
	private double avgTrackTemp;

	public Duration addStintDuration(Duration toAdd) {
		if( currentStintDuration == null ) {
			currentStintDuration = Duration.ZERO;
		}
		currentStintDuration = currentStintDuration.plus(toAdd);
		return currentStintDuration;
	}

	public int increaseLapCount() {
		return ++laps;
	}
}
