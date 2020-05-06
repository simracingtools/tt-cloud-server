package de.bausdorf.simcacing.tt.live.model.client;

import java.util.ArrayList;
import java.util.List;

public enum ServiceFlagType {
	FUEL(16, 'F'),
	TYRES(15, 'T'),
	WS(32, 'W');

	int bits;
	char code;

	ServiceFlagType(int bits, char shortCode) {
		this.bits = bits;
		this.code = shortCode;
	}

	public char shortCode() {
		return code;
	}

	public static List<ServiceFlagType> ofBitmask(int bitmask) {
		List<ServiceFlagType> list = new ArrayList<>();
		for (ServiceFlagType type : values()) {
			if ((type.bits & bitmask) > 0) {
				list.add(type);
			}
		}
		return list;
	}
}
