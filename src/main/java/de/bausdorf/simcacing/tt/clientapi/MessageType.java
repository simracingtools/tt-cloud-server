package de.bausdorf.simcacing.tt.clientapi;

public enum MessageType {
	SYNCDATA("syncData"),
	LAPDATA("lapdata"),
	PITSTOP("pitstop"),
	SESSIONINFO("sessionInfo"),
	RUNDATA("runData");

	private String jsonKey;

	private MessageType(String name) {
		this.jsonKey = name;
	}

	public String getJsonKey() {
		return jsonKey;
	}

	public static MessageType fromJsonKey(String key) {
		if( key == null ) {
			throw new IllegalArgumentException("Invalid message type null");
		}
		switch(key.toLowerCase()) {
			case "syncdata": return SYNCDATA;
			case "lapdata": return LAPDATA;
			case "pitstop": return PITSTOP;
			case "sessioninfo": return SESSIONINFO;
			case "rundata": return RUNDATA;
			default:
				throw new IllegalArgumentException("Invalid message type \"" + key + "\"");
		}
	}
}
