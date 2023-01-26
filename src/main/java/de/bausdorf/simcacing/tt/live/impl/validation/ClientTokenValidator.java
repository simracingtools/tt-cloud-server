package de.bausdorf.simcacing.tt.live.impl.validation;

import de.bausdorf.simcacing.tt.live.clientapi.ClientMessage;
import de.bausdorf.simcacing.tt.live.clientapi.UnauthorizedAccessException;
import de.bausdorf.simcacing.tt.web.security.TtClientRegistrationRepository;
import de.bausdorf.simcacing.tt.web.security.TtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ClientTokenValidator {
    public static final String TOKEN_COULD_NOT_BE_VALIDATED_ON_CLIENT_ID = "Token could not be validated on client id ";
    private final TtClientRegistrationRepository clientRepository;

    private final Map<String, String> tokenCache;

    public ClientTokenValidator(@Autowired TtClientRegistrationRepository clientRepository) {
        this.clientRepository = clientRepository;
        this.tokenCache = new HashMap<>();
    }

    public void validateAccessToken(String accessToken, String clientId) {
        String authorizedClientId = tokenCache.get(accessToken);
        if (authorizedClientId == null) {
            List<TtUser> users = clientRepository.findByAccessToken(accessToken);
            if (users.isEmpty()) {
                throw new UnauthorizedAccessException("No token for client id " + clientId);
            }
            for (TtUser user : users) {
                if (user.getClientMessageAccessToken() == null) {
                    continue;
                }
                if (user.getClientMessageAccessToken().equalsIgnoreCase(accessToken)
                        && user.getIRacingId().equalsIgnoreCase(clientId)) {
                    authorizedClientId = clientId;
                    tokenCache.put(accessToken, authorizedClientId);
                    return;
                }
            }
            tokenCache.put(accessToken, "NONE");
            throw new UnauthorizedAccessException(TOKEN_COULD_NOT_BE_VALIDATED_ON_CLIENT_ID + clientId);
        } else {
            if (!authorizedClientId.equalsIgnoreCase(clientId)) {
                throw new UnauthorizedAccessException(TOKEN_COULD_NOT_BE_VALIDATED_ON_CLIENT_ID + clientId);
            }
        }
    }

    public void validateAccessToken(ClientMessage message) {
        String authorizedClientId = tokenCache.get(message.getAccessToken());
        if (authorizedClientId == null) {
            List<TtUser> users = clientRepository.findByAccessToken(message.getAccessToken());
            if (users.isEmpty()) {
                throw new UnauthorizedAccessException("No token for client id " + message.getClientId());
            }
            for (TtUser user : users) {
                if (user.getClientMessageAccessToken() == null) {
                    continue;
                }
                if (user.getClientMessageAccessToken().equalsIgnoreCase(message.getAccessToken())
                        && user.getIRacingId().equalsIgnoreCase(message.getClientId())) {
                    authorizedClientId = message.getClientId();
                    tokenCache.put(message.getAccessToken(), authorizedClientId);
                    return;
                }
            }
            tokenCache.put(message.getAccessToken(), "NONE");
            throw new UnauthorizedAccessException(TOKEN_COULD_NOT_BE_VALIDATED_ON_CLIENT_ID + message.getClientId());
        } else {
            if (!authorizedClientId.equalsIgnoreCase(message.getClientId())) {
                throw new UnauthorizedAccessException(TOKEN_COULD_NOT_BE_VALIDATED_ON_CLIENT_ID + message.getClientId());
            }
        }
    }
}
