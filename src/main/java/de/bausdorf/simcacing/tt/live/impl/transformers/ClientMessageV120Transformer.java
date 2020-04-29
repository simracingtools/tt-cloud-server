package de.bausdorf.simcacing.tt.live.impl.transformers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;

import de.bausdorf.simcacing.tt.live.clientapi.ClientMessage;
import de.bausdorf.simcacing.tt.live.clientapi.MessageConstants.EventData;
import de.bausdorf.simcacing.tt.live.clientapi.MessageConstants.RunData;
import de.bausdorf.simcacing.tt.live.clientapi.MessageConstants.SessionData;
import de.bausdorf.simcacing.tt.live.clientapi.MessageProcessor;
import de.bausdorf.simcacing.tt.live.clientapi.MessageTransformer;
import de.bausdorf.simcacing.tt.live.clientapi.MessageType;

@Component
public class ClientMessageV120Transformer extends MessageTransformer {

	public ClientMessageV120Transformer(@Autowired MessageProcessor processor) {
		super(processor);
	}

	@Override
	public String supportedMessageVersion() {
		return "1.20";
	}

	@Override
	public ClientMessage transform(ClientMessage message) {
		if (message.getType() == MessageType.SESSION_INFO) {
			message.getPayload().put(SessionData.CAR_ID, "");
			message.getPayload().put(SessionData.TRACK_ID, "");
		} else if (message.getType() == MessageType.RUN_DATA) {
			message.getPayload().put(RunData.SESSION_TOD, 0.0D);
		} else if (message.getType() == MessageType.EVENT) {
			message.getPayload().put(EventData.SESSION_TOD, 0.0D);
		}
		return message;
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
		registerTransformer();
	}
}
