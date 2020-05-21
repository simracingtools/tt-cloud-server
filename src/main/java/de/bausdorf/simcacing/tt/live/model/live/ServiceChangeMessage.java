package de.bausdorf.simcacing.tt.live.model.live;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@ToString
public class ServiceChangeMessage {
	private String teamId;
	private String checkId;
	private boolean checked;
}
