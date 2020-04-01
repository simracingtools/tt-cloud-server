package de.bausdorf.simcacing.tt.clientapi;


public interface MessageValidator<T extends ClientData> {

    MessageType supportedMessageType();

    T validate(ClientMessage message);
}
