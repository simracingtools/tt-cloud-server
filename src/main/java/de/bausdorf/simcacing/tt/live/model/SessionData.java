package de.bausdorf.simcacing.tt.live.model;

import java.time.LocalTime;
import java.util.Optional;

import de.bausdorf.simcacing.tt.live.clientapi.ClientData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SessionData implements ClientData {

	private final static String UNLIMITED = "unlimited";

	private SessionIdentifier sessionId;
	private String teamName;
	private String carName;
	private String sessionLaps;
	private String sessionTime;
	private double maxCarFuel;
	private String trackName;
	private String sessionType;

	public Optional<LocalTime> getSessionDuration() {
		if( UNLIMITED.equalsIgnoreCase(sessionTime) ) {
			return Optional.empty();
		}
		return Optional.of(LocalTime.ofSecondOfDay(Long.parseLong(sessionTime)));
	}

	public Optional<Integer> getSessionMaxLaps() {
		if( UNLIMITED.equalsIgnoreCase(sessionLaps) ) {
			return Optional.empty();
		}
		return Optional.of(Integer.parseInt(sessionTime));
	}
}
