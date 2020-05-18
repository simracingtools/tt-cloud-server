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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GoogleUserService extends OidcUserService implements UserDetailsService {

    private final TtClientRegistrationRepository userRepository;

    public GoogleUserService(@Autowired TtClientRegistrationRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);
        Map attributes = oidcUser.getAttributes();
        String userId = (String) attributes.get("sub");
        Optional<TtUser> user = userRepository.findById(userId);
        if(!user.isPresent()) {
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
        }
        return oidcUser;
    }

    @Override
    public UserDetails loadUserByUsername(String s) {
        List<TtUser> users = userRepository.findByUserEmail(s);
        return users.isEmpty() ? null : users.get(0);
    }
}
