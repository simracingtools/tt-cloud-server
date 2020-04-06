package de.bausdorf.simcacing.tt.web.security;

import com.google.cloud.firestore.DocumentSnapshot;
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

    public TtUser(DocumentSnapshot doc) {
        id = doc.getId();
        name = doc.getString("name");
        imageUrl = doc.getString("imageUrl");
        email = doc.getString("email");
        iRacingId = doc.getString("iRacingId");
        userType = TtUserType.valueOf(doc.getString("userType"));
        clientMessageAccessToken = doc.getString("clientMessageAccessToken");
        enabled = doc.getBoolean("enabled");
        locked = doc.getBoolean("locked");
        expired = doc.getBoolean("expired");
    }

    public Map<String, Object> toObjectMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("imageUrl", imageUrl);
        map.put("email", email);
        map.put("iRacingId", iRacingId);
        map.put("clientMessageAccessToken", clientMessageAccessToken);
        map.put("userType", userType.name());
        map.put("enabled", enabled);
        map.put("locked", locked);
        map.put("expired", expired);
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
