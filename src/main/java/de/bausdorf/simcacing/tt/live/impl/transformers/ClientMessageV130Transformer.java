package de.bausdorf.simcacing.tt.live.impl.transformers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;

import de.bausdorf.simcacing.tt.live.clientapi.ClientMessage;
import de.bausdorf.simcacing.tt.live.clientapi.MessageConstants.EventData;
import de.bausdorf.simcacing.tt.live.clientapi.MessageConstants.LapData;
import de.bausdorf.simcacing.tt.live.clientapi.MessageConstants.RunData;
import de.bausdorf.simcacing.tt.live.clientapi.MessageConstants.SessionData;
import de.bausdorf.simcacing.tt.live.clientapi.MessageProcessor;
import de.bausdorf.simcacing.tt.live.clientapi.MessageTransformer;
import de.bausdorf.simcacing.tt.live.clientapi.MessageType;

@Component
public class ClientMessageV130Transformer extends MessageTransformer {

	public ClientMessageV130Transformer(@Autowired MessageProcessor processor) {
		super(processor);
	}

	@Override
	public String supportedMessageVersion() {
		return "1.30";
	}

	@Override
	public ClientMessage transform(ClientMessage message) {
		if (message.getType() == MessageType.EVENT) {
			message.getPayload().put(EventData.SERVICE_FLAGS, 0);
		} else if (message.getType() == MessageType.LAP) {
			message.getPayload().put(LapData.DRIVER_ID, "");
		}
		return message;
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
		registerTransformer();
	}
}
