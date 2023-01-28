package de.bausdorf.simcacing.tt.live.impl.validation;

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
