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

import de.bausdorf.simcacing.tt.util.OffsetDateTimeConverter;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
public class TtIdentity implements UserDetails {

    @Id
    private String id;
    private String name;
    private String email;
    private String imageUrl;
    @Column(unique = true, nullable = false)
    private String iracingId;
    @Column(unique = true, nullable = false)
    private String clientMessageAccessToken;
    private TtUserType userType;
    private String zoneIdName;
    @Convert(converter = OffsetDateTimeConverter.class)
    private OffsetDateTime created;
    @Convert(converter = OffsetDateTimeConverter.class)
    private OffsetDateTime lastAccess;
    @Convert(converter = OffsetDateTimeConverter.class)
    private OffsetDateTime lastSubscription;
    private SubscriptionType subscriptionType;
    private boolean enabled;
    private boolean locked;
    private boolean expired;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final Set<SimpleGrantedAuthority> roles = new HashSet<>();
        roles.add(new SimpleGrantedAuthority("USER_ROLE"));
        roles.add(new SimpleGrantedAuthority(userType.name()));
        return roles;
    }

    public ZoneId getTimezone() {
        return zoneIdName != null ? ZoneId.of(zoneIdName) : null;
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
