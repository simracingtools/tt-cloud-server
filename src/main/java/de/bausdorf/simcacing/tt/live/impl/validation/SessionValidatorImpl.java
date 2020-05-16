package de.bausdorf.simcacing.tt.live.impl.validation;

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

import de.bausdorf.simcacing.tt.live.clientapi.*;
import de.bausdorf.simcacing.tt.live.impl.ModelFactory;
import de.bausdorf.simcacing.tt.live.model.client.SessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;

@Component
public class SessionValidatorImpl extends MessageValidator<SessionData> {

    public SessionValidatorImpl(@Autowired MessageProcessor clientService) {
        super(clientService);
    }

    @Override
    public MessageType supportedMessageType() {
        return MessageType.SESSION_INFO;
    }

    @Override
    public SessionData validate(ClientMessage sessionMessage) {

        String sessionTime = ModelFactory.stringOfNumberOrString(
                sessionMessage.getPayload().get(MessageConstants.SessionData.SESSION_DURATION));
        String sessionLaps = ModelFactory.stringOfNumberOrString(
                sessionMessage.getPayload().get(MessageConstants.SessionData.SESSION_LAPS));

        if( sessionTime == null || sessionLaps == null ) {
            throw new InvalidClientMessageException("sessionLaps or sessionTime has to be present");
        }

        try {
            return ModelFactory.getFromSessionMessage(sessionMessage.getPayload());
        } catch (Exception e) {
            throw new InvalidClientMessageException(e);
        }
    }
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        super.registerValidator();
    }
}
