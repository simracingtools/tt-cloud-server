package de.bausdorf.simcacing.tt.planning.model;

import java.time.Duration;
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
public class Estimation {

	public static final String TOD_FROM = "todFrom";
	public static final String AVG_LAP_TIME = "avgLapTime";
	public static final String AVG_FUEL_PER_LAP = "avgFuelPerLap";

	private LocalDateTime todFrom;
	private IRacingDriver driver;
	private Duration avgLapTime;
	private Double avgFuelPerLap;

	public String getDriverName() {
		return driver.getName();
	}

	public LocalTime getTodTime() {
		return todFrom.toLocalTime();
	}

	public void setTodTime(LocalTime time) {
		todFrom = LocalDateTime.of(todFrom.toLocalDate(), time);
	}

	public LocalDate getTodDate() {
		return todFrom.toLocalDate();
	}

	public void setTodDate(LocalDate date) {
		todFrom = LocalDateTime.of(date, todFrom.toLocalTime());
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put(TOD_FROM, todFrom.toString());
		map.put(AVG_LAP_TIME, avgLapTime.toString());
		map.put(AVG_FUEL_PER_LAP, avgFuelPerLap);
		return map;
	}
}
