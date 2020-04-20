package de.bausdorf.simcacing.tt.stock.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class IRacingDriver {

	public static final String I_RACING_ID = "iRacingId";
	public static final String NAME = "Name";
	public static final String VALIDATED = "validated";

	private String name;
	private String id;
	private boolean validated;

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put(I_RACING_ID, id);
		map.put(NAME, name);
		map.put(VALIDATED, validated);
		return map;
	}

	public List<String> toNameIdList() {
		List<String> list = new ArrayList<>();
		list.add(name);
		list.add(id);
		return list;
	}
}
