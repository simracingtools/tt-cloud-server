package de.bausdorf.simcacing.tt.live.impl.validation;

import de.bausdorf.simcacing.tt.live.clientapi.*;
import de.bausdorf.simcacing.tt.live.impl.ModelFactory;
import de.bausdorf.simcacing.tt.live.model.SessionData;
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

        String sessionTime = (String)sessionMessage.getPayload().get("sessionTime");
        String sessionLaps = (String)sessionMessage.getPayload().get("sessionLaps");

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
