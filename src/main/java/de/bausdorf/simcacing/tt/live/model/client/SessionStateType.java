package de.bausdorf.simcacing.tt.live.model.client;

public enum SessionStateType {
	INVALID(0),
	GET_IN_CAR(1),
	WARM_UP(2),
	PARADE_LAPS(3),
	RACING(4),
	CHECKERED(5),
	COOL_DOWN(6);

	private final int code;

	SessionStateType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static SessionStateType ofCode(int stateCode) {
		switch(stateCode) {
			case 0: return INVALID;
			case 1: return GET_IN_CAR;
			case 2: return WARM_UP;
			case 3: return PARADE_LAPS;
			case 4: return RACING;
			case 5: return CHECKERED;
			case 6: return COOL_DOWN;
			default: throw new IllegalArgumentException("Code " + stateCode + " is an invalid session state");
		}
	}
}
