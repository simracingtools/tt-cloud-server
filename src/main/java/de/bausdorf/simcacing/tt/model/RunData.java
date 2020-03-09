package de.bausdorf.simcacing.tt.model;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RunData {

	private double sessionTime;
	private double fuelLevel;
	private String[] flags;
	private double estLapTime;

	public boolean isGreenFlag() {
		return flags.length > 0 ? flags[0].equalsIgnoreCase("GREEN") : false;
	}
}
