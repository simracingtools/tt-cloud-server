package de.bausdorf.simcacing.tt.web.security;

import com.google.cloud.firestore.DocumentSnapshot;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TtUser {
    private String id;

    private String name;
    private String email;
    private String imageUrl;
    private String iRacingId;
    private String clientMessageAccessToken;
    private TtUserType userType;

    public TtUser(DocumentSnapshot doc) {
        id = doc.getId();
        name = doc.getString("name");
        imageUrl = doc.getString("imageUrl");
        email = doc.getString("email");
        iRacingId = doc.getString("iRacingId");
        userType = TtUserType.valueOf(doc.getString("userType"));
        clientMessageAccessToken = doc.getString("clientMessageAccessToken");
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
        return map;
    }
}
