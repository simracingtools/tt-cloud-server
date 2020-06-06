package de.bausdorf.simcacing.tt.web.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class StintView {
	private StintDriverView selectedDriver;
	private List<StintDriverView> availableDrivers;
	private String startLocal;
	private String startToD;
	private String laps;
	private String duration;
	private boolean refuel;
	private boolean changeTyres;
	private boolean clearWindshield;
	private boolean lastStint;
}
