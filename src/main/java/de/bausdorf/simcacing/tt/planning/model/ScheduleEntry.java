package de.bausdorf.simcacing.tt.planning.model;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ScheduleEntry {

	private LocalTime start;
	private LocalTime end;
	private String driverName;
	private boolean blocked;
}
