package de.bausdorf.simcacing.tt.web.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DriverEstimationView {
	private String driverName;
	private String driverId;
	private boolean validated;
	private List<EstimationView> estimationEntries;

	public DriverEstimationView() {
		this.estimationEntries = new ArrayList<>();
	}
}
