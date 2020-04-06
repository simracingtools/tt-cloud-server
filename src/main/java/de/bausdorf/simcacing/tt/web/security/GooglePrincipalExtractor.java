package de.bausdorf.simcacing.tt.web.security;

import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.util.Map;

public class GooglePrincipalExtractor implements PrincipalExtractor {

    private GoogleUserService userService;

    public GooglePrincipalExtractor(GoogleUserService service) {
        this.userService = service;
    }

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        String id = (String) map.get("id");
        // Check if we've already registered this uer
        TtUser user = null;//userService.loadUser()
        if (user == null) {
            // If we haven't registered this user yet, create a new one
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            // This Details object exposes a token that allows us to interact with Facebook on this user's behalf
            String token = ((OAuth2AuthenticationDetails) authentication.getDetails()).getTokenValue();
            user = new TtUser();
//            user.setEmail(fbUser.getEmail());
            // Set the default Roles for users registered via Facebook
//            user.setRoles(Sets.newHashSet(Role.ROLE_USER, Role.ROLE_USER_FACEBOOK));
//            user = userService.create(user);
        }
        return user;
    }

}
