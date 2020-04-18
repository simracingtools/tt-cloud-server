package de.bausdorf.simcacing.tt.planning.model;

import lombok.Getter;

@Getter
public enum PitStopServiceType {
	WS("W", 5),
	TYRES("T", 15),
	FUEL("F", 20),
	FR("FR", 0);

	private String code;
	private long seconds;

	private PitStopServiceType(String code, long seconds) {
		this.code = code;
		this.seconds = seconds;
	}

	public static PitStopServiceType ofCode(String c) {
		switch(c) {
			case "W": return WS;
			case "T": return TYRES;
			case "F": return FUEL;
			case "FR": return FR;
			default: throw new IllegalArgumentException("Unknown PitStopServiceType code " + c);
		}
	}
}
