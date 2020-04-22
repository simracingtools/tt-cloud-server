package de.bausdorf.simcacing.tt.web.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class TeamScheduleView {
	private List<DriverScheduleView> teamSchedule;
	private String planId;

	public TeamScheduleView(String planId) {
		this.planId = planId;
		this.teamSchedule = new ArrayList<>();
	}
}
