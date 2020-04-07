package de.bausdorf.simcacing.tt.live.clientapi;

import com.fasterxml.jackson.annotation.JsonValue;


public enum MessageType {
	LAP(MessageConstants.MessageType.LAPDATA_NAME),
	SESSION_INFO(MessageConstants.MessageType.SESSION_INFO_NAME),
	RUN_DATA(MessageConstants.MessageType.RUN_DATA_NAME),
	EVENT(MessageConstants.MessageType.EVENTDATA_NAME),
	SYNC(MessageConstants.MessageType.SYNCDATA_NAME);

	private String jsonKey;

	private MessageType(String name) {
		this.jsonKey = name;
	}

	@JsonValue
	public String getJsonKey() {
		return jsonKey;
	}

	public static MessageType fromJsonKey(String key) {
		if( key == null ) {
			throw new IllegalArgumentException("Invalid message type null");
		}
		switch(key.toLowerCase()) {
			case MessageConstants.MessageType.LAPDATA_NAME: return LAP;
			case MessageConstants.MessageType.SESSION_INFO_NAME: return SESSION_INFO;
			case MessageConstants.MessageType.RUN_DATA_NAME: return RUN_DATA;
			case MessageConstants.MessageType.EVENTDATA_NAME: return EVENT;
			case MessageConstants.MessageType.SYNCDATA_NAME: return SYNC;
			default:
				throw new IllegalArgumentException("Invalid message type \"" + key + "\"");
		}
	}

}
