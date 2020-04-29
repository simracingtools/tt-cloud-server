package de.bausdorf.simcacing.tt.live.clientapi;

public interface MessageProcessor {

    void registerMessageValidator(MessageValidator validator);
    void registerMessageTransformer(MessageTransformer transformer);

    public void processMessage(ClientMessage message);
}
