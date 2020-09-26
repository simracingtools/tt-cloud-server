package de.bausdorf.simcacing.tt.live.model.live;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TyreDataView {
	private List<String> outerWear;
	private List<String> middleWear;
	private List<String> innerWear;
}
