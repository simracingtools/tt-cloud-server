package de.bausdorf.simcacing.tt.model;

import de.bausdorf.simcacing.tt.clientapi.ClientData;
import lombok.*;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncData implements ClientData {

	private String clientId;
	private LocalTime sessionTime;
	boolean isInCar;
}
