package de.bausdorf.simcacing.tt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pitstop {

	private int stint;
	private int lap;
	private String driver;
	private double enterPits;
	private double stopMoving;
	private double startMoving;
	private double exitPits;
	private double serviceFlags;
	private double repairLeft;
	private double optRepairLeft;
	private double towTime;

}
