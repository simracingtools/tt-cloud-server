package de.bausdorf.simcacing.tt.web.model;

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

import de.bausdorf.simcacing.tt.stock.DriverRepository;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.util.TimeTools;
import de.bausdorf.simcacing.tt.web.security.SubscriptionType;
import de.bausdorf.simcacing.tt.web.security.TtIdentity;
import de.bausdorf.simcacing.tt.web.security.TtUserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileView {

    private String id;
    private String name;
    private String email;
    private String imageUrl;
    private String iRacingId;
    private String driverNick;
    private String clientMessageAccessToken;
    private String userType;
    private SubscriptionType subscriptionType;
    private String subscriptionExpiration;
    private String timezone;
    private Boolean enabled;
    private Boolean locked;
    private Boolean expired;

    // Read-only
    private String username;

    public UserProfileView(TtIdentity user, DriverRepository driverRepository) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.imageUrl = user.getImageUrl();
        this.iRacingId = user.getIracingId();
        this.userType = user.getUserType().toText();
        this.clientMessageAccessToken = user.getClientMessageAccessToken();
        this.enabled = user.isEnabled();
        this.locked = user.isLocked();
        this.expired = user.isExpired();
        this.username = user.getUsername();
        this.timezone = user.getZoneIdName() != null ? TimeTools.toShortZoneId(ZoneId.of(user.getZoneIdName())) : "";
        this.subscriptionType = user.getSubscriptionType();
        this.subscriptionExpiration = subscriptionType != SubscriptionType.NONE
                ? user.getLastSubscription().plus(this.subscriptionType.getDuration()).toLocalDate().toString()
                : "Not relevant";

        Optional<IRacingDriver> driver = driverRepository.findById(iRacingId);
        this.driverNick = driver.isPresent() ? driver.get().getName() : "";
    }

    public TtIdentity getUser(TtIdentity merge) {
        return TtIdentity.builder()
                .name(name != null ? name : merge.getName())
                .id(id != null ? id : merge.getId())
                .email(email != null ? email : merge.getEmail())
                .imageUrl(imageUrl != null ? imageUrl : merge.getImageUrl())
                .clientMessageAccessToken(clientMessageAccessToken != null ? clientMessageAccessToken : merge.getClientMessageAccessToken())
                .enabled(enabled != null ? enabled : merge.isEnabled())
                .locked(locked != null ? locked : merge.isLocked())
                .expired(expired != null ? expired : merge.isExpired())
                .iracingId(iRacingId != null ? iRacingId : merge.getIracingId())
                .userType(userType != null ? TtUserType.ofText(userType) : merge.getUserType())
                .zoneIdName(timezone != null ? timezone : merge.getZoneIdName())
                .created(merge.getCreated())
                .lastAccess(merge.getLastAccess())
                .lastSubscription(merge.getLastSubscription())
                .subscriptionType(merge.getSubscriptionType())
                .build();
    }

}
