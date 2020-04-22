package de.bausdorf.simcacing.tt.web.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
public class DriverScheduleView {
	private String driverName;
	private String driverId;
	private boolean validated;
	private List<ScheduleView> scheduleEntries;

	public DriverScheduleView() {
		this.scheduleEntries = new ArrayList<>();
	}
}
