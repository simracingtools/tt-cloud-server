package de.bausdorf.simcacing.tt.planning.model;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Estimation {

	private String driverName;
	private Duration avgLapTime;
	private Double avgFuelPerLap;

}
