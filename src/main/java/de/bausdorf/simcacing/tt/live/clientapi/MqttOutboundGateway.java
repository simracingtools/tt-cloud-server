package de.bausdorf.simcacing.tt.live.clientapi;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttOutboundGateway {
    void sendToMqtt(String message);
    void sendToMqtt(String data, @Header(MqttHeaders.TOPIC) String topic);
}
