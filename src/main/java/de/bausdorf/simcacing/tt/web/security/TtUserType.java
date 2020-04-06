package de.bausdorf.simcacing.tt.web.security;

import org.springframework.security.core.GrantedAuthority;

public enum TtUserType implements GrantedAuthority {
    TT_SYSADMIN,
    TT_TEAMADMIN,
    TT_MEMBER,
    TT_REGISTERED,
    TT_NEW;

    @Override
    public String getAuthority() {
        return name();
    }
}
