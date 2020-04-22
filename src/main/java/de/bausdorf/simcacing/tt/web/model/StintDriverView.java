package de.bausdorf.simcacing.tt.web.model;

import java.util.List;

import lombok.Data;

@Data
public class StintDriverView {
	private String planId;
	private List<String> stintDrivers;

	public StintDriverView(String planId, List<String> names) {
		this.planId = planId;
		this.stintDrivers = names;
	}
}
