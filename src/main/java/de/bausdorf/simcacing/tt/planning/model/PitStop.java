package de.bausdorf.simcacing.tt.planning.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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
	private Duration serviceDuration;
	private Duration approach;
	private Duration depart;
	private Duration repairTime;

	private Duration calculateServiceDuration() {
		this.serviceDuration = Duration.ZERO;
		service.stream().forEach(s -> serviceDuration = serviceDuration.plusSeconds(s.getSeconds()));
		return serviceDuration;
	}

	public void addService(PitStopServiceType serviceType) {
		if (!service.contains(serviceType) ) {
			service.add(serviceType);
			calculateServiceDuration();
		}
	}

	public void removeService(PitStopServiceType serviceType) {
		if (service.contains(serviceType)) {
			service.remove(serviceType);
			calculateServiceDuration();
		}
	}

	public Duration getOverallDuration() {
		return Duration.ZERO
				.plus(approach)
				.plus((serviceDuration == null) ? calculateServiceDuration() : serviceDuration)
				.plus(repairTime)
				.plus(depart);
	}

	public static PitStop defaultPitStop() {
		return PitStop.builder()
				.approach(Duration.ofSeconds(10))
				.repairTime(Duration.ZERO)
				.service(defaultService)
				.depart(Duration.ofSeconds(5))
				.build();
	}
}
