package de.bausdorf.simcacing.tt.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
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
                    .userType(TtUserType.TT_NEW)
                    .locked(false)
                    .expired(false)
                    .enabled(true)
                    .build()
            );
        }
        return oidcUser;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        List<TtUser> users = userRepository.findByUserEmail(s);
        return users.isEmpty() ? null : users.get(0);
    }
}
