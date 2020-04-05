package de.bausdorf.simcacing.tt.web.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TtUser {
    private String id;

    private String name;
    private String email;
    private String imageUrl;
    private String iRacingId;
    private String clientMessageAccessToken;
    private TtUserType userType;
}
