package de.bausdorf.simcacing.tt.planning.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ScheduleEntry {

	public static final String FROM = "from";
	public static final String STATUS = "status";

	private LocalDateTime from;
	private IRacingDriver driver;
	private ScheduleDriverOptionType status;

	public String getDriverName() {
		return driver.getName();
	}

	public LocalTime getFromTime() {
		return from.toLocalTime();
	}

	public void setFromTime(LocalTime time) {
		from = LocalDateTime.of(from.toLocalDate(), time);
	}

	public LocalDate getFromDate() {
		return from.toLocalDate();
	}

	public void setFromDate(LocalDate date) {
		from = LocalDateTime.of(date, from.toLocalTime());
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put(FROM, from.toString());
		map.put(STATUS, status.name());
		return map;
	}
}
