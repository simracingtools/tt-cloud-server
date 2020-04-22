package de.bausdorf.simcacing.tt.web.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class TtUserDetailsManager  implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String s) {
        return null;
    }
}
