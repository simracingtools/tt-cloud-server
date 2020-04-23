package de.bausdorf.simcacing.tt.web.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class TeamEstimationView {
	private List<DriverEstimationView> teamEstimations;
	private String planId;

	public TeamEstimationView(String planId) {
		this.planId = planId;
		this.teamEstimations = new ArrayList<>();
	}
}
