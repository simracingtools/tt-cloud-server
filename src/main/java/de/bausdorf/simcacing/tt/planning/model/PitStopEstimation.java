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
import java.util.EnumMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Deprecated
public class PitStopEstimation {

	private Map<PitStopServiceType, Duration> serviceDurations;
	private Duration approachPits;
	private Duration leavePits;

	public static PitStopEstimation defaultPitStopEstimation() {
		PitStopEstimation estimation = PitStopEstimation.builder()
				.approachPits(Duration.ofSeconds(15))
				.leavePits(Duration.ofSeconds(5))
				.build();

		EnumMap<PitStopServiceType, Duration> defaultServiceDurations = new EnumMap<>(PitStopServiceType.class);
		defaultServiceDurations.put(PitStopServiceType.WS, Duration.ofSeconds(2));
		defaultServiceDurations.put(PitStopServiceType.TYRES, Duration.ofSeconds(20));
		defaultServiceDurations.put(PitStopServiceType.FUEL, Duration.ofSeconds(25));
		estimation.setServiceDurations(defaultServiceDurations);

		return estimation;
	}

	public Duration getServiceDuration() {
		Duration serviceDuration = Duration.ZERO;
		for( Duration d : serviceDurations.values() ) {
			serviceDuration = serviceDuration.plus(d);
		}
		return serviceDuration;
	}

	public Duration getOverallDuration() {
		return approachPits.plus(getServiceDuration()).plus(leavePits);
	}
}
