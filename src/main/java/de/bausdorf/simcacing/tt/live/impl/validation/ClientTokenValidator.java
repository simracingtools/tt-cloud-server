package de.bausdorf.simcacing.tt.live.impl.validation;

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
            throw new UnauthorizedAccessException("Token could not be validated on client id " + clientId);
        } else {
            if (!authorizedClientId.equalsIgnoreCase(clientId)) {
                throw new UnauthorizedAccessException("Token could not be validated on client id " + clientId);
            }
        }
    }
}
