package de.bausdorf.simcacing.tt.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class GoogleUserService extends OidcUserService {

    private final TtClientRegistrationRepository userRepository;

    public GoogleUserService(@Autowired TtClientRegistrationRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
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
                    .userType(TtUserType.NEW)
                    .build()
            );
        }
        return oidcUser;
    }
}
