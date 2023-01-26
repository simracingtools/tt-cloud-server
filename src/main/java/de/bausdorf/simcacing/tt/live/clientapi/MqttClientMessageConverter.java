package de.bausdorf.simcacing.tt.live.clientapi;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

public class MqttClientMessageConverter extends DefaultPahoMessageConverter {

    @Override
    public Message<?> toMessage(Object mqttMessage, MessageHeaders headers) {
        return super.toMessage(mqttMessage, headers);
    }

    @Override
    public MqttMessage fromMessage(Message<?> message, Class<?> targetClass) {
        return super.fromMessage(message, targetClass);
    }
}
