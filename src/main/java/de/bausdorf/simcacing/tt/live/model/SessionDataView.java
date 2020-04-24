package de.bausdorf.simcacing.tt.live.model;

import de.bausdorf.simcacing.tt.live.clientapi.ClientData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Optional;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionDataView implements ClientData {
	private String sessionId;
	private String teamName;
	private String teamId;
	private String carName;
	private String sessionDuration;
	private String maxCarFuel;
	private String trackName;
	private String sessionType;
}
