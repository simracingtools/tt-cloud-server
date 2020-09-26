package de.bausdorf.simcacing.tt.live.clientapi;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 bausdorf engineering
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.fasterxml.jackson.annotation.JsonValue;


public enum MessageType {
	PING(MessageConstants.MessageType.PING_NAME),
	LAP(MessageConstants.MessageType.LAPDATA_NAME),
	SESSION_INFO(MessageConstants.MessageType.SESSION_INFO_NAME),
	RUN_DATA(MessageConstants.MessageType.RUN_DATA_NAME),
	EVENT(MessageConstants.MessageType.EVENTDATA_NAME),
	TYRES(MessageConstants.MessageType.TYRES_NAME),
	SYNC(MessageConstants.MessageType.SYNCDATA_NAME);

	private final String jsonKey;

	MessageType(String name) {
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
		switch(key) {
			case MessageConstants.MessageType.LAPDATA_NAME: return LAP;
			case MessageConstants.MessageType.SESSION_INFO_NAME: return SESSION_INFO;
			case MessageConstants.MessageType.RUN_DATA_NAME: return RUN_DATA;
			case MessageConstants.MessageType.EVENTDATA_NAME: return EVENT;
			case MessageConstants.MessageType.SYNCDATA_NAME: return SYNC;
			case MessageConstants.MessageType.TYRES_NAME: return TYRES;
			case MessageConstants.MessageType.PING_NAME: return PING;
			default:
				throw new IllegalArgumentException("Invalid message type \"" + key + "\"");
		}
	}

}
