package de.bausdorf.simcacing.tt.stock.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IRacingCarClass {

	public static final String NAME = "Name";
	public static final String ID = "ID";
	public static final String CAR_IDS = "CarIds";

	private String id;
	private String name;
	private List<String> carIds;

	public Map<String, Object> toMap() {
		Map<String, Object> carClassMap = new HashMap<>();
		carClassMap.put(NAME, name);
		carClassMap.put(ID, id);
		carClassMap.put(CAR_IDS, carIds);
		return carClassMap;
	}
}
