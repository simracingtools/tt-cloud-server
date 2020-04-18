package de.bausdorf.simcacing.tt.planning.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ScheduleEntry {

	private LocalDateTime start;
	private LocalDateTime end;
	private IRacingDriver driver;
	private ScheduleDriverOptionType status;

}
