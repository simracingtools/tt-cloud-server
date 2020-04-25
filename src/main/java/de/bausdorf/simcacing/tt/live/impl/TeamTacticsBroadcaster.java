package de.bausdorf.simcacing.tt.live.impl;

import de.bausdorf.simcacing.tt.live.model.live.ClientAck;
import de.bausdorf.simcacing.tt.live.model.live.LiveClientMessage;
import de.bausdorf.simcacing.tt.live.model.live.SessionDataView;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class TeamTacticsBroadcaster {

    public static final String LIVE_PREFIX = "/live/";

    @Setter
    private MessageBrokerRegistry brokerRegistry;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private Map<String, String> liveTopics;

    public TeamTacticsBroadcaster() {
        this.liveTopics = new HashMap<>();
    }

    @SendTo("/live/{teamId}/sessionData")
    public SessionDataView sendSessionData(SessionDataView message, @DestinationVariable String teamId) {
        return message;
    }

    @MessageMapping("/liveclient")
    @SendTo("/live/client-ack")
    public ClientAck respondAck(LiveClientMessage message) {
        log.info("Connect message from {}: {}", message.getTeamId(), message.getText());
        String teamId = message.getText();
        if (!liveTopics.containsKey(teamId)) {
            liveTopics.put(teamId, LIVE_PREFIX + teamId);
        }
        return new ClientAck(liveTopics.get(teamId));
    }
}
