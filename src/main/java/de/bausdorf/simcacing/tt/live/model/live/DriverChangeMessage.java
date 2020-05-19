package de.bausdorf.simcacing.tt.live.model.live;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@ToString
public class DriverChangeMessage {
	private String teamId;
	private String selectId;
	private String driverName;
}
