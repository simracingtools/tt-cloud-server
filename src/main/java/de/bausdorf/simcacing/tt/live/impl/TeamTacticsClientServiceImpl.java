package de.bausdorf.simcacing.tt.live.impl;

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

import java.util.Map;
import java.util.Optional;

import de.bausdorf.simcacing.tt.live.clientapi.*;
import de.bausdorf.simcacing.tt.live.impl.transformers.ClientMessageReader;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TeamTacticsClientServiceImpl implements TeamTacticsClientService {

	private final MessageProcessor processor;
	private final ClientMessageReader messageReader;

	public TeamTacticsClientServiceImpl(@Autowired MessageProcessor processor,
										@Autowired ClientMessageReader messageReader) {
		this.processor = processor;
		this.messageReader = messageReader;
	}

	@Override
	@PostMapping(value = "/clientmessage")
	public String receiveClientData(@RequestBody String clientString,
			@RequestHeader("x-teamtactics-token") Optional<String> clientAccessToken) {

		// Maybe Json is escaped - so remove escape characters
		Map<String, Object> clientMessage = messageReader.readClientMessage(clientString.replace("\\", ""));

		if (clientAccessToken.isEmpty()) {
			log.warn("No x-teamtactics-token header");
			return "TOKEN_ERROR";
		}
		try {
			ClientMessage msg = messageReader.validateClientMessage(clientMessage, clientAccessToken.get());
			if (msg.getType() != MessageType.PING) {
				processMessage(msg);
			}
			return msg.getType().name();
		} catch (InvalidClientMessageException e) {
			log.warn(e.getMessage());
			return "VALIDATION_ERROR";
		} catch (UnauthorizedAccessException e) {
			log.warn(e.getMessage());
			return "AUTHORIZATION_ERROR";
		} catch (UnsupportedClientException e) {
			log.warn(e.getMessage());
			return "UNSUPPORTED_CLIENT";
		}
	}



	private void processMessage(ClientMessage msg) {
		try {
			processor.processMessage(msg);
		} catch (InvalidClientMessageException e) {
			log.warn(e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("{}: {}", e.getMessage(), msg);
			StackTraceElement[] trace = e.getStackTrace();
			log.error("{}({}:{})", trace[0].getMethodName(), trace[0].getClassName(), trace[0].getLineNumber());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
