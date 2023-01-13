package de.bausdorf.simcacing.tt.web.security;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 bausdorf engineering
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GoogleUserService extends OidcUserService implements UserDetailsService {

    private final TtClientRegistrationRepository userRepository;
    private final TtIdentityRepository identityRepository;

    public GoogleUserService(@Autowired TtClientRegistrationRepository userRepository,
                             @Autowired TtIdentityRepository identityRepository) {
        this.userRepository = userRepository;
        this.identityRepository = identityRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);
        Map<String, Object> attributes = oidcUser.getAttributes();
        String userId = (String) attributes.get("sub");
        updateJpaUserRepository(userId, attributes);
        return oidcUser;
    }

    @Override
    public UserDetails loadUserByUsername(String s) {
        List<TtIdentity> users = identityRepository.findAllByEmail(s);
        return users.isEmpty() ? null : users.get(0);
    }

    public SimpleGrantedAuthority determineUserRole(String userId) {
        TtIdentity ttUser = identityRepository.findById(userId).orElse(null);
        if (ttUser != null) {
            ttUser.setLastAccess(OffsetDateTime.now());
            String roleName = WebSecurityConfig.ROLE_PREFIX + ttUser.getUserType().name();
            if (!ttUser.isEnabled()) {
                roleName = WebSecurityConfig.ROLE_PREFIX + TtUserType.TT_NEW.name();
                ttUser.setUserType(TtUserType.TT_NEW);
            }
            identityRepository.save(ttUser);
            return new SimpleGrantedAuthority(roleName);
        }
        return null;
    }


    private void updateCloudUserRepository(String userId, Map<String, Object> attributes) {
        Optional<TtUser> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            userRepository.save(TtUser.builder()
                    .email((String) attributes.get("email"))
                    .id(userId)
                    .imageUrl((String) attributes.get("picture"))
                    .name((String) attributes.get("name"))
                    .userType(TtUserType.TT_NEW)
                    .locked(false)
                    .expired(false)
                    .enabled(false)
                    .build()
            );
        } else {
            Optional<TtIdentity> identity = identityRepository.findById(user.get().getId());
            if (identity.isEmpty()) {
                migrateUsers();
            }
        }
    }

    private void updateJpaUserRepository(String userId, Map<String, Object> attributes) {
        Optional<TtIdentity> user = identityRepository.findById(userId);
        if (user.isEmpty()) {
            identityRepository.save(TtIdentity.builder()
                    .email((String) attributes.get("email"))
                    .id(userId)
                    .imageUrl((String) attributes.get("picture"))
                    .name((String) attributes.get("name"))
                    .created(OffsetDateTime.now())
                    .userType(TtUserType.TT_NEW)
                    .locked(false)
                    .expired(false)
                    .enabled(false)
                    .build()
            );
        }
    }

    private void migrateUsers() {
        List<TtUser> users = userRepository.loadAll(userRepository.getUserCollectionName());
        users.forEach(user -> {
            TtIdentity identity = TtIdentity.builder()
                    .id(user.getId())
                    .iracingId(user.getIRacingId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .imageUrl(user.getImageUrl())
                    .userType(user.getUserType())
                    .subscriptionType(user.getSubscriptionType())
                    .enabled(user.isEnabled())
                    .expired(user.isExpired())
                    .locked(user.isLocked())
                    .clientMessageAccessToken(user.getClientMessageAccessToken())
                    .created(OffsetDateTime.of(user.getCreated().toLocalDate(), user.getCreated().toLocalTime(), user.getTimezone().getRules().getOffset(user.getCreated().toLocalDateTime())))
                    .lastAccess(OffsetDateTime.of(user.getLastAccess().toLocalDate(), user.getLastAccess().toLocalTime(), user.getTimezone().getRules().getOffset(user.getLastAccess().toLocalDateTime())))
                    .lastSubscription(OffsetDateTime.now())
                    .zoneIdName(user.getTimezone().toString())
                    .build();
            identityRepository.save(identity);
        });
    }
}
