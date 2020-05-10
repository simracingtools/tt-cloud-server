package de.bausdorf.simcacing.tt.live.model.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

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
	private List<ServiceFlagType> serviceFlags;
	private Duration repairTime;
	private Duration optRepairTime;
	private Duration towTime;
	private double fuelLeft;
	private double refuelAmount;

	public boolean isComplete() {
		return (enterPits != null && exitPits != null);
	}

	public Duration getRepairAndTowingTime() {
		Duration accumulatedDuration = Duration.ZERO;
		if (repairTime != null) {
			accumulatedDuration = accumulatedDuration.plus(repairTime);
		}
		if (optRepairTime != null) {
			accumulatedDuration = accumulatedDuration.plus(optRepairTime);
		}
		if (towTime != null) {
			accumulatedDuration = accumulatedDuration.plus(towTime);
		}
		return accumulatedDuration;
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

	public String getServiceFlagsString() {
		if (serviceFlags == null) {
			return "";
		}
		StringBuilder flags = new StringBuilder();
		for (ServiceFlagType flag : serviceFlags) {
			flags.append(flag.code).append(' ');
		}
		return flags.toString();
	}

	public boolean update(EventData event, double fuelLevel) {
		switch( event.getTrackLocationType() ) {
			case APPROACHING_PITS:
				if( enterPits == null ) {
					enterPits = LocalTime.now();
				}
				if( startMoving == null && stopMoving != null ) {
					startMoving = LocalTime.now();
				}
				updateRepairAndTowTimes(event);
				break;
			case PIT_STALL:
				if( stopMoving == null ) {
					stopMoving = LocalTime.now();
					serviceFlags = event.getServiceFlags();
					fuelLeft = fuelLevel;
				}
				updateRepairAndTowTimes(event);
				break;
			case ONTRACK:
				if( exitPits != null ) {
					// Pitstop already completed
					break;
				} else {
					exitPits = LocalTime.now();
					refuelAmount = fuelLevel - fuelLeft;
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
