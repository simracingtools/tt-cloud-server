package de.bausdorf.simcacing.tt.live.impl.transformers;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;

import de.bausdorf.simcacing.tt.live.clientapi.ClientMessage;
import de.bausdorf.simcacing.tt.live.clientapi.MessageConstants.EventData;
import de.bausdorf.simcacing.tt.live.clientapi.MessageConstants.LapData;
import de.bausdorf.simcacing.tt.live.clientapi.MessageConstants.RunData;
import de.bausdorf.simcacing.tt.live.clientapi.MessageConstants.SessionData;
import de.bausdorf.simcacing.tt.live.clientapi.MessageProcessor;
import de.bausdorf.simcacing.tt.live.clientapi.MessageTransformer;
import de.bausdorf.simcacing.tt.live.clientapi.MessageType;

@Component
public class ClientMessageV130Transformer extends MessageTransformer {

	public ClientMessageV130Transformer(@Autowired MessageProcessor processor) {
		super(processor);
	}

	@Override
	public String supportedMessageVersion() {
		return "1.30";
	}

	@Override
	public ClientMessage transform(ClientMessage message) {
		if (message.getType() == MessageType.EVENT) {
			message.getPayload().put(EventData.SERVICE_FLAGS, 0);
		} else if (message.getType() == MessageType.LAP) {
			message.getPayload().put(LapData.DRIVER_ID, "");
		}
		return message;
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
		registerTransformer();
	}
}
