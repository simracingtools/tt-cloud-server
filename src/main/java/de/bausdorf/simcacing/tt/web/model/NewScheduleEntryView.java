package de.bausdorf.simcacing.tt.web.model;

import java.time.LocalTime;

import de.bausdorf.simcacing.tt.planning.model.ScheduleDriverOptionType;
import lombok.Data;

@Data
public class NewScheduleEntryView {
	private String planId;
	private String driverId;
	private LocalTime timeFrom;
	private ScheduleDriverOptionType status;

	public NewScheduleEntryView(String planId) {
		this.planId = planId;
		this.status = ScheduleDriverOptionType.OPEN;
	}
}
