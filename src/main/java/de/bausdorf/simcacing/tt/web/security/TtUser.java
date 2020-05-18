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

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TtUser implements UserDetails {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String IMAGE_URL = "imageUrl";
    public static final String EMAIL = "email";
    public static final String I_RACING_ID = "iRacingId";
    public static final String USER_TYPE = "userType";
    public static final String CLIENT_MESSAGE_ACCESS_TOKEN = "clientMessageAccessToken";
    public static final String ENABLED = "enabled";
    public static final String LOCKED = "locked";
    public static final String EXPIRED = "expired";

    private String id;
    private String name;
    private String email;
    private String imageUrl;
    private String iRacingId;
    private String clientMessageAccessToken;
    private TtUserType userType;
    private boolean enabled;
    private boolean locked;
    private boolean expired;

    public TtUser(Map<String, Object> data) {
        id = (String)data.get(ID);
        name = (String)data.get(NAME);
        imageUrl = (String)data.get(IMAGE_URL);
        email = (String)data.get(EMAIL);
        iRacingId = (String)data.get(I_RACING_ID);
        userType = TtUserType.valueOf((String)data.get(USER_TYPE));
        clientMessageAccessToken = (String)data.get(CLIENT_MESSAGE_ACCESS_TOKEN);
        enabled = (Boolean) data.get(ENABLED);
        locked = (Boolean) data.get(LOCKED);
        expired = (Boolean) data.get(EXPIRED);
    }

    public Map<String, Object> toObjectMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(ID, id);
        map.put(NAME, name);
        map.put(IMAGE_URL, imageUrl);
        map.put(EMAIL, email);
        map.put(I_RACING_ID, iRacingId);
        map.put(CLIENT_MESSAGE_ACCESS_TOKEN, clientMessageAccessToken);
        map.put(USER_TYPE, userType.name());
        map.put(ENABLED, enabled);
        map.put(LOCKED, locked);
        map.put(EXPIRED, expired);
        return map;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final Set<SimpleGrantedAuthority> roles = new HashSet<>();
        roles.add(new SimpleGrantedAuthority("USER_ROLE"));
        roles.add(new SimpleGrantedAuthority(userType.name()));
        return roles;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !expired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
