package de.bausdorf.simcacing.tt.live.model.live;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SyncDataView {
	private String driverId;
	private String timestamp;
	private String stateCssClass;
	private String inCarCssClass;
}
