package de.bausdorf.simcacing.tt.live.clientapi;

public interface MessageProcessor {

    void registerMessageValidator(MessageValidator validator);

    public void processMessage(ClientMessage message);
}
