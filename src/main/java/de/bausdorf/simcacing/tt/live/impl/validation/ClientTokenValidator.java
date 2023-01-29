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
import de.bausdorf.simcacing.tt.web.security.TtIdentity;
import de.bausdorf.simcacing.tt.web.security.TtIdentityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class ClientTokenValidator {
    public static final String TOKEN_COULD_NOT_BE_VALIDATED_ON_CLIENT_ID = "Token could not be validated on client id ";
    private final TtIdentityRepository clientRepository;

    private final Map<String, String> tokenCache;

    public ClientTokenValidator(@Autowired TtIdentityRepository clientRepository) {
        this.clientRepository = clientRepository;
        this.tokenCache = new HashMap<>();
    }

    public void validateAccessToken(String accessToken, String clientId) {
        String authorizedClientId = tokenCache.get(accessToken);
        if (authorizedClientId == null) {
            Optional<TtIdentity> userOptional = clientRepository.findByClientMessageAccessToken(accessToken);
            userOptional.ifPresentOrElse(
                    user -> {
                        if (user.getClientMessageAccessToken().equalsIgnoreCase(accessToken)
                                && user.getIracingId().equalsIgnoreCase(clientId)) {
                            tokenCache.put(accessToken, clientId);
                        } else {
                            tokenCache.put(accessToken, "NONE");
                            throw new UnauthorizedAccessException(TOKEN_COULD_NOT_BE_VALIDATED_ON_CLIENT_ID + clientId);
                        }
                    },
                    () -> {
                        throw new UnauthorizedAccessException("No token for client id " + clientId);
                    }
            );
        } else {
            if (!authorizedClientId.equalsIgnoreCase(clientId)) {
                throw new UnauthorizedAccessException(TOKEN_COULD_NOT_BE_VALIDATED_ON_CLIENT_ID + clientId);
            }
        }
    }

    public void validateAccessToken(ClientMessage message) {
        validateAccessToken(message.getAccessToken(), message.getClientId());
    }
}
