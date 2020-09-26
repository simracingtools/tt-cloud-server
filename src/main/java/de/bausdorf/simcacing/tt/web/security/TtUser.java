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

import de.bausdorf.simcacing.tt.util.MapTools;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
    public static final String TIMEZONE = "timezone";
    public static final String CREATED = "created";
    public static final String LAST_ACCESS = "lastAccess";
    public static final String LAST_SUBSCRIPTION = "lastSubscription";
    public static final String SUBSCRIPTION = "subscriptionType";

    private String id;
    private String name;
    private String email;
    private String imageUrl;
    private String iRacingId;
    private String clientMessageAccessToken;
    private TtUserType userType;
    private ZoneId timezone;
    private ZonedDateTime created;
    private ZonedDateTime lastAccess;
    private ZonedDateTime lastSubscription;
    private SubscriptionType subscriptionType;
    private boolean enabled;
    private boolean locked;
    private boolean expired;

    public TtUser(Map<String, Object> data) {
        id = MapTools.stringFromMap(ID, data);
        name = MapTools.stringFromMap(NAME, data);
        imageUrl = MapTools.stringFromMap(IMAGE_URL, data);
        email = MapTools.stringFromMap(EMAIL, data);
        iRacingId = MapTools.stringFromMap(I_RACING_ID, data);
        userType = TtUserType.valueOf(MapTools.stringFromMap(USER_TYPE, data));
        clientMessageAccessToken = MapTools.stringFromMap(CLIENT_MESSAGE_ACCESS_TOKEN, data);
        enabled = MapTools.booleanFromMap(ENABLED, data, false);
        locked = MapTools.booleanFromMap(LOCKED, data, false);
        expired = MapTools.booleanFromMap(EXPIRED, data, false);
        timezone = data.get(TIMEZONE) != null ? ZoneId.of((String)data.get(TIMEZONE)) : ZoneId.systemDefault();
        created = MapTools.zonedDateTimeFromMap(CREATED, data);
        lastAccess = MapTools.zonedDateTimeFromMap(LAST_ACCESS, data);
        lastSubscription = MapTools.zonedDateTimeFromMap(LAST_SUBSCRIPTION, data);
        subscriptionType = SubscriptionType.valueOf(MapTools.stringFromMapWithDefault(SUBSCRIPTION, data, "NONE"));
        if (LocalDateTime.MIN.equals(created.toLocalDateTime())) {
            created = ZonedDateTime.now();
        }
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
        map.put(TIMEZONE, timezone != null ? timezone.toString() : null);
        map.put(CREATED, created != null ? created.toString() : ZonedDateTime.now());
        map.put(LAST_ACCESS, lastAccess.toString());
        map.put(LAST_SUBSCRIPTION, lastSubscription.toString());
        map.put(SUBSCRIPTION, subscriptionType.name());
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
