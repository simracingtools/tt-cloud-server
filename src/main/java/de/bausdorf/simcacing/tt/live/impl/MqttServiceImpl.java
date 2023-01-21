package de.bausdorf.simcacing.tt.live.impl;

import de.bausdorf.simcacing.tt.live.clientapi.*;
import de.bausdorf.simcacing.tt.live.impl.transformers.ClientMessageReader;
import de.bausdorf.simcacing.tt.util.TeamtacticsServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.event.MqttIntegrationEvent;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class MqttServiceImpl {

    public static final String MQTT_DEFAULT_OUTBOUND_TOPIC = "/racecontrol";

    private final TeamtacticsServerProperties config;

    private final MqttOutboundGateway mqttGateway;

    private final ClientMessageReader messageReader;

    public MqttServiceImpl(@Autowired TeamtacticsServerProperties config,
                           @Autowired MqttOutboundGateway gateway,
                           @Autowired ClientMessageReader messageReader) {
        this.config = config;
        this.mqttGateway = gateway;
        this.messageReader = messageReader;
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] { config.getMqttBrokerHost() });
        options.setUserName(config.getMqttUsername());
        options.setPassword(config.getMqttPassword().toCharArray());
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound(@Autowired MqttPahoClientFactory mqttClientFactory,
                                   @Autowired MessageChannel mqttInputChannel) {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(config.getMqttClientId() + "-in", mqttClientFactory,"/driver-id");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel);
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(@NonNull Message<?> message) throws MessagingException {
                String topic = (String)message.getHeaders().get("mqtt_receivedTopic");
                String oneLineMessage = message.getPayload().toString().replace("\n", "");

                try(FileWriter mqttLogWriter = new FileWriter("mqttlog.txt", true)) {
                    mqttLogWriter.write(topic + '#' + oneLineMessage + '\n');
                } catch (IOException e) {
                    log.warn(e.getMessage());
                }
//                mqttGateway.sendToMqtt("Echo: " + oneLineMessage, "/echoes");

                try {
                    Map<String, Object> messageMap = messageReader.readClientMessage(oneLineMessage);
                    String accessToken = (String)messageMap.get("accessToken");
                    log.debug("{}", messageMap);
                    ClientMessage clientMessage = messageReader.validateClientMessage(messageMap, accessToken);
                    log.debug("{}", clientMessage);
                    if (clientMessage.getType() == MessageType.AUTH) {
                        mqttGateway.sendToMqtt("Authorized", topic + "/" + accessToken);
                    }
                } catch (InvalidClientMessageException e) {
                    log.warn("Invalid client message on {}: {}", topic, oneLineMessage);
                } catch (UnauthorizedAccessException e) {
                    log.warn("Client not authorized");
                }
            }
        };
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound(@Autowired MqttPahoClientFactory mqttClientFactory) {
        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler(config.getMqttClientId() + "-out", mqttClientFactory);
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(MQTT_DEFAULT_OUTBOUND_TOPIC);
        return messageHandler;
    }

    @EventListener
    public void mqttEventListener(MqttIntegrationEvent event) {
        log.info("MQTT event: {}", event);
    }
}
