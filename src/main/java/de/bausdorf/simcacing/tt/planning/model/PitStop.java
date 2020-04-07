package de.bausdorf.simcacing.tt.planning.model;

import java.time.Duration;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PitStop {

	private List<PitStopServiceType> service;
	private Duration serviceDuration;
	private Duration overallDuration;
}
