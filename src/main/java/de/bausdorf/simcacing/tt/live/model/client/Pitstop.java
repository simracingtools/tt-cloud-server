package de.bausdorf.simcacing.tt.live.model.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pitstop {

	private int stint;
	private int lap;
	private String driver;
	private LocalTime enterPits;
	private LocalTime stopMoving;
	private LocalTime startMoving;
	private LocalTime exitPits;
	private long serviceFlags;
	private Duration repairTime;
	private Duration optRepairTime;
	private Duration towTime;

	public boolean isComplete() {
		return (enterPits != null && exitPits != null);
	}

	public Duration getPitstopDuration() {
		if( enterPits != null && exitPits != null ) {
			return Duration.between(enterPits, exitPits);
		}
		return Duration.ZERO;
	}

	public Duration getPitstopServiceTime() {
		if( stopMoving != null && startMoving != null ) {
			return Duration.between(stopMoving, startMoving);
		}
		return Duration.ZERO;
	}

	public boolean update(EventData event) {
		switch( event.getTrackLocationType() ) {
			case APPROACHING_PITS:
				if( enterPits == null ) {
					enterPits = event.getSessionTime();
				}
				if( startMoving == null && stopMoving != null ) {
					startMoving = event.getSessionTime();
				}
				updateRepairAndTowTimes(event);
				break;
			case PIT_STALL:
				if( stopMoving == null ) {
					stopMoving = event.getSessionTime();
				}
				updateRepairAndTowTimes(event);
				break;
			case ONTRACK:
				if( exitPits != null ) {
					// Pitstop already completed
					break;
				} else {
					exitPits = event.getSessionTime();
					return true;
				}
			case OFF_WORLD:
			case OFFTRACK:
				if( exitPits != null ) {
					// Pitstop already completed
					break;
				} else {
					updateRepairAndTowTimes(event);
				}
		}
		return false;
	}

	private void updateRepairAndTowTimes(EventData event) {
		if( optRepairTime == null && !event.getOptRepairTime().isZero()) {
			optRepairTime = event.getOptRepairTime();
		}
		if( repairTime == null && !event.getRepairTime().isZero() ) {
			repairTime = event.getRepairTime();
		}
		if( towTime == null && !event.getTowingTime().isZero()) {
			towTime = event.getTowingTime();
		}
	}
}
