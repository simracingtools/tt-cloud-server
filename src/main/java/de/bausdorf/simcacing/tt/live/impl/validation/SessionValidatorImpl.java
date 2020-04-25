package de.bausdorf.simcacing.tt.live.impl.validation;

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
