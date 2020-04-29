package de.bausdorf.simcacing.tt.live.clientapi;


import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

public abstract class MessageValidator<T extends ClientData> implements ApplicationListener<ApplicationReadyEvent> {

    private final MessageProcessor processor;

    public MessageValidator(MessageProcessor processor) {
        this.processor = processor;
    }

    public void registerValidator() {
        processor.registerMessageValidator(this);
    }

    public abstract MessageType supportedMessageType();
    public abstract T validate(ClientMessage message);
}
