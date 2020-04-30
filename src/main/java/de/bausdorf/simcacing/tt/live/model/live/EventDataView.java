package de.bausdorf.simcacing.tt.live.model.live;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class EventDataView {
	private String trackLocation;
	private String trackLocationCssClass;
	private String sessionTime;
	private String timeOfDay;
}
