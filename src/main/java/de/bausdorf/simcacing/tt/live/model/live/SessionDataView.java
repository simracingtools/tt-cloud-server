package de.bausdorf.simcacing.tt.live.model.live;

import de.bausdorf.simcacing.tt.live.clientapi.ClientData;
import de.bausdorf.simcacing.tt.live.impl.SessionController;
import de.bausdorf.simcacing.tt.live.model.client.TrackLocationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionDataView implements ClientData {
	private String sessionId;
	private String teamName;
	private String carName;
	private String sessionDuration;
	private String maxCarFuel;
	private String trackName;
	private String sessionType;
	private LapDataView lastLapData;
	private String trackLocation;
	private String trackLocationCssClass;
}
