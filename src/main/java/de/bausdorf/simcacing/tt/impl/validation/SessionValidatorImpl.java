package de.bausdorf.simcacing.tt.impl.validation;

import de.bausdorf.simcacing.tt.clientapi.*;
import de.bausdorf.simcacing.tt.impl.ModelFactory;
import de.bausdorf.simcacing.tt.model.SessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionValidatorImpl implements MessageValidator<SessionData> {


    public SessionValidatorImpl(@Autowired MessageProcessor clientService) {
        clientService.registerMessageValidator(this);
    }

    @Override
    public MessageType supportedMessageType() {
        return MessageType.SESSION_INFO;
    }

    @Override
    public SessionData validate(ClientMessage sessionMessage) {

        String sessionTime = (String)sessionMessage.getPayload().get("sessionTime");
        String sessionLaps = (String)sessionMessage.getPayload().get("sessionLaps");

        if( sessionTime == null | sessionLaps == null ) {
            throw new InvalidClientMessageException("sessionLaps or sessionTime has to be present");
        }

        try {
            return ModelFactory.getFromSessionMessage(sessionMessage.getPayload());
        } catch (Exception e) {
            throw new InvalidClientMessageException(e);
        }
    }
}
