package de.bausdorf.simcacing.tt.stock.model;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class IRacingDriver {

	public static final String I_RACING_ID = "iRacingId";
	public static final String NAME = "Name";

	private String name;
	private String id;

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put(I_RACING_ID, id);
		map.put(NAME, name);
		return map;
	}
}
