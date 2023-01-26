package de.bausdorf.simcacing.tt.live.impl.transformers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.bausdorf.simcacing.tt.live.clientapi.*;
import de.bausdorf.simcacing.tt.live.impl.validation.ClientTokenValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ClientMessageReader {
    public static final String KEY_TYPE = "type";

    private static final List<String> acceptedVersions = new ArrayList<>();

    static {
        acceptedVersions.add("1.31");
        acceptedVersions.add("1.32");
        acceptedVersions.add("1.33");
        acceptedVersions.add("1.34");
        acceptedVersions.add("2.00");
    }

    private final ObjectMapper objectMapper;
    private final ClientTokenValidator tokenValidator;

    public ClientMessageReader(@Autowired ClientTokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
        this.objectMapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)
                .build();
    }

    @Deprecated(forRemoval = true)
    public Map<String, Object> readClientMessage(String clientString) {
        try {
            // Maybe json string is quoted - so remove quotes if present
            if (clientString.startsWith("\"") && clientString.endsWith("\"")) {
                clientString = clientString.substring(1, clientString.length() - 1);
            }
            TypeReference<HashMap<String, Object>> typeRef
                    = new TypeReference<HashMap<String, Object>>() {};
            return objectMapper.readValue(clientString, typeRef);
        } catch (JsonProcessingException e) {
            throw new InvalidClientMessageException(e.getMessage());
        }
    }

    public ClientMessage convertClientMessage(String clientString){
        try {
            // Maybe json string is quoted - so remove quotes if present
            if (clientString.startsWith("\"") && clientString.endsWith("\"")) {
                clientString = clientString.substring(1, clientString.length() - 1);
            }
            return objectMapper.readValue(clientString, ClientMessage.class);
        } catch (JsonProcessingException e) {
            throw new InvalidClientMessageException(e.getMessage());
        }
    }

    public ClientMessage convertClientMessage(Reader reader) {
        try {
            return objectMapper.readValue(reader, ClientMessage.class);
        } catch (IOException e) {
            throw new InvalidClientMessageException(e.getMessage());
        }
    }

    @Deprecated(forRemoval = true)
    public ClientMessage validateClientMessage(Map<String, Object> clientMessage, String accessToken) {
        String clientId = (String)clientMessage.get(MessageConstants.Message.CLIENT_ID);
        tokenValidator.validateAccessToken(accessToken, clientId);

        String messageType = (String)clientMessage.get(KEY_TYPE);
        String clientVersion = (String)clientMessage.get(MessageConstants.Message.VERSION);
        String sessionId = (String)clientMessage.get(MessageConstants.Message.SESSION_ID);
        String teamId = (String)clientMessage.get(MessageConstants.Message.TEAM_ID);

        if( messageType == null ) {
            throw new InvalidClientMessageException("No message type in message");
        }
        if( clientVersion == null ) {
            throw new InvalidClientMessageException("No message version");
        } else {
            if( !acceptedVersions.contains(clientVersion)) {
                throw new UnsupportedClientException("Client version " + clientVersion + " not accepted");
            }
        }
        if( clientId == null ) {
            throw new InvalidClientMessageException("Message without client id");
        }
        if( sessionId == null ) {
            throw new InvalidClientMessageException("Message without session id");
        }

        try {
            return ClientMessage.builder()
                    .type(MessageType.fromJsonKey(messageType))
                    .version(clientVersion)
                    .sessionId(sessionId)
                    .teamId(teamId)
                    .clientId(clientId)
                    .payload((Map<String, Object>) clientMessage.get(MessageConstants.Message.PAYLOAD))
                    .build();
        } catch( Exception e ) {
            throw new InvalidClientMessageException(e.getMessage());
        }
    }

    public void validateClientMessage(ClientMessage message) {
        tokenValidator.validateAccessToken(message);
        if( message.getType() == null ) {
            throw new InvalidClientMessageException("No message type in message");
        }
        if( message.getVersion() == null ) {
            throw new InvalidClientMessageException("No message version");
        } else {
            if( !acceptedVersions.contains(message.getVersion())) {
                throw new UnsupportedClientException("Client version " + message.getVersion() + " not accepted");
            }
        }
        if( message.getClientId() == null ) {
            throw new InvalidClientMessageException("Message without client id");
        }
        if( message.getSessionId() == null ) {
            throw new InvalidClientMessageException("Message without session id");
        }
    }

    public <K> K convertPayload(ClientMessage message, String payloadProperty, Class<K> targetType) {
        if (payloadProperty == null)
            return objectMapper.convertValue(message.getPayload(), targetType);
        else
            return objectMapper.convertValue(message.getPayload().get(payloadProperty), targetType);
    }
}
