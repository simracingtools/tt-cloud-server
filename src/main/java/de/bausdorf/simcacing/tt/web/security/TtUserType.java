package de.bausdorf.simcacing.tt.web.security;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 bausdorf engineering
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.springframework.security.core.GrantedAuthority;

public enum TtUserType implements GrantedAuthority {
    TT_SYSADMIN("Sysadmin"),
    TT_TEAMADMIN("Team admin"),
    TT_MEMBER("Member"),
    TT_REGISTERED("Registered user"),
    TT_NEW("New user");

    private String name;

    TtUserType(String name) {
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
            case "Registered user": return TT_REGISTERED;
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
