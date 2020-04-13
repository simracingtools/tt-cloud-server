package de.bausdorf.simcacing.tt.stock.model;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IRacingCar {

	public static final String NAME = "Name";
	public static final String ID = "ID";
	public static final String MAX_FUEL = "maxFuel";
	public static final String UNIT = "unit";

	private String id;
	private String name;
	private double maxFuel;
	private String unit;

	public Map<String, Object> toMap() {
		Map<String, Object> carMap = new HashMap<>();
		carMap.put(NAME, name);
		carMap.put(ID, id);
		carMap.put(MAX_FUEL, maxFuel);
		carMap.put(UNIT, unit);
		return carMap;
	}
}
