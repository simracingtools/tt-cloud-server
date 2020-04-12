package de.bausdorf.simcacing.tt.web.model;

import de.bausdorf.simcacing.tt.stock.DriverRepository;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.web.security.TtUser;
import de.bausdorf.simcacing.tt.web.security.TtUserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Boolean enabled;
    private Boolean locked;
    private Boolean expired;

    // Read-only
    private String username;

    public UserProfileView(TtUser user, DriverRepository driverRepository) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.imageUrl = user.getImageUrl();
        this.iRacingId = user.getIRacingId();
        this.userType = user.getUserType().toText();
        this.clientMessageAccessToken = user.getClientMessageAccessToken();
        this.enabled = user.isEnabled();
        this.locked = user.isLocked();
        this.expired = user.isExpired();
        this.username = user.getUsername();

        Optional<IRacingDriver> driver = driverRepository.findById(iRacingId);
        this.driverNick = driver.isPresent() ? driver.get().getName() : "";
    }

    public TtUser getUser(TtUser merge) {
        return TtUser.builder()
                .name(name != null ? name : merge.getName())
                .id(id != null ? id : merge.getId())
                .email(email != null ? email : merge.getEmail())
                .imageUrl(imageUrl != null ? imageUrl : merge.getImageUrl())
                .clientMessageAccessToken(clientMessageAccessToken != null ? clientMessageAccessToken : merge.getClientMessageAccessToken())
                .enabled(enabled != null ? enabled : merge.isEnabled())
                .locked(locked != null ? locked : merge.isLocked())
                .expired(expired != null ? expired : merge.isExpired())
                .iRacingId(iRacingId != null ? iRacingId : merge.getIRacingId())
                .userType(userType != null ? TtUserType.ofText(userType) : merge.getUserType())
                .build();
    }

}
