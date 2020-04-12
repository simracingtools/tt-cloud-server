package de.bausdorf.simcacing.tt.web.security;

import org.springframework.security.core.GrantedAuthority;

public enum TtUserType implements GrantedAuthority {
    TT_SYSADMIN("Sysadmin"),
    TT_TEAMADMIN("Team admin"),
    TT_MEMBER("Member"),
    TT_REGISTERED("Registered"),
    TT_NEW("New user");

    private String name;

    private TtUserType(String name) {
        this.name = name;
    }

    public String toText() {
        return this.name;
    }

    public static TtUserType ofText(String text) {
        switch(text) {
            case "Sysadmin": return TT_SYSADMIN;
            case "Team admin": return TT_TEAMADMIN;
            case "Member": return TT_MEMBER;
            case "Registered": return TT_REGISTERED;
            case "New user": return TT_NEW;
            default:
                throw new IllegalArgumentException("Unknown TtUserType: " + text);
        }
    }
    @Override
    public String getAuthority() {
        return name();
    }
}
