package de.bausdorf.simcacing.tt.live.clientapi;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 - 2023 bausdorf engineering
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
