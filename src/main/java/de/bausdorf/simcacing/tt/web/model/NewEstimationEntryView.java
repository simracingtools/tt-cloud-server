package de.bausdorf.simcacing.tt.web.model;

import java.time.Duration;
import java.time.LocalTime;

import lombok.Data;

@Data
public class NewEstimationEntryView {
	private String planId;
	private String driverId;
	private LocalTime timeFrom;
	private Duration avgLapTime;
	private Double avgFuelPerLap;

	public NewEstimationEntryView(String planId) {
		this.planId = planId;
	}
}
