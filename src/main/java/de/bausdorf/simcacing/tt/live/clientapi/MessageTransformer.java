package de.bausdorf.simcacing.tt.live.clientapi;


import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

public abstract class MessageTransformer implements ApplicationListener<ApplicationReadyEvent> {

    private final MessageProcessor processor;

    public MessageTransformer(MessageProcessor processor) {
        this.processor = processor;
    }

    public void registerTransformer() {
        processor.registerMessageTransformer(this);
    }

    public abstract String supportedMessageVersion();
    public abstract ClientMessage transform(ClientMessage message);
}
