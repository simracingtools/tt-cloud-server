package de.bausdorf.simcacing.tt.clientapi;

public interface MessageProcessor {

    void registerMessageValidator(MessageValidator validator);

    public void processMessage(ClientMessage message);
}
