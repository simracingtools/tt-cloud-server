package de.bausdorf.simcacing.tt.stock.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.bausdorf.simcacing.tt.stock.CachedRepository;
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

	private String id;
	private String name;

	public Map<String, Object> toMap() {
		Map<String, Object> carMap = new HashMap<>();
		carMap.put(NAME, name);
		carMap.put(ID, id);
		return carMap;
	}
}
