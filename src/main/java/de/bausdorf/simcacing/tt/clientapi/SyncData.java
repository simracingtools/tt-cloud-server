package de.bausdorf.simcacing.tt.clientapi;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SyncData {

	private String irid;
	private double sessionTime;
	boolean isInCar;
	PitstopData pitstopData;
}
