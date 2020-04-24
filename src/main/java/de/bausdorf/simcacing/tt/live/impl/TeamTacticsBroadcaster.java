package de.bausdorf.simcacing.tt.live.impl;

import de.bausdorf.simcacing.tt.live.model.ClientAck;
import de.bausdorf.simcacing.tt.live.model.LiveClientMessage;
import de.bausdorf.simcacing.tt.live.model.SessionDataView;
import lombok.Setter;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TeamTacticsBroadcaster {

    public static final String LIVE_PREFIX = "/live/";

    @Setter
    private MessageBrokerRegistry brokerRegistry;

    private Map<String, String> liveTopics;

    public TeamTacticsBroadcaster() {
        this.liveTopics = new HashMap<>();
    }

    @SendTo("/live/{teamId}/sessionData")
    public SessionDataView sendSessionData(SessionDataView message, @DestinationVariable String teamId) throws Exception {
        return message;
    }

    @MessageMapping("/liveclient")
    @SendTo("/live/client-ack")
    public ClientAck respondAck(LiveClientMessage message) {
        String teamId = message.getText();
        if (!liveTopics.containsKey(teamId)) {
            liveTopics.put(teamId, LIVE_PREFIX + teamId);
        }
        return new ClientAck(liveTopics.get(teamId));
    }
}
