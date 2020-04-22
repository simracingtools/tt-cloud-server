package de.bausdorf.simcacing.tt.planning.model;

import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
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
