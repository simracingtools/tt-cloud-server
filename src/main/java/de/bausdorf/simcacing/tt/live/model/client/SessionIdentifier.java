package de.bausdorf.simcacing.tt.live.model.client;

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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class SessionIdentifier {

    public static final String IS_NOT_A_VALID_SESSION_IDENTIFIER = " is not a valid session identifier";

    private String teamName;
    private String sessionId;
    private String subSessionId;
    private int sessionNum;

    @Override
    public String toString() {
        return new StringBuilder()
                .append(teamName).append('@')
                .append(sessionId).append('#')
                .append(subSessionId).append('#')
                .append(sessionNum)
                .toString();
    }

    public boolean equalsWithoutSessionNum(SessionIdentifier other) {
        if (other == null) {
            return false;
        }
        return teamName.equalsIgnoreCase(other.getTeamName())
                && sessionId.equalsIgnoreCase(other.getSessionId())
                && subSessionId.equalsIgnoreCase(other.getSubSessionId());
    }

    public String getSubscriptionId() {
        return new StringBuilder(teamName.replace(" ", ""))
                .append('-').append(sessionId)
                .append('-').append(subSessionId)
                .append('-').append(sessionNum)
                .toString();
    }

    public static SessionIdentifier parse(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        String[] teamAndSession = s.split("@");
        if (teamAndSession.length != 2) {
            throw new IllegalArgumentException(s + IS_NOT_A_VALID_SESSION_IDENTIFIER);
        }
        SessionIdentifierBuilder sessionIdentifierBuilder = SessionIdentifier.builder();
        sessionIdentifierBuilder = sessionIdentifierBuilder.teamName(teamAndSession[0]);

        String[] sessionIdComponents = teamAndSession[1].split("#");
        if (sessionIdComponents.length != 3) {
            throw new IllegalArgumentException(s + IS_NOT_A_VALID_SESSION_IDENTIFIER);
        }
        try {
            sessionIdentifierBuilder
                    .sessionId(sessionIdComponents[0])
                    .subSessionId(sessionIdComponents[1])
                    .sessionNum(Integer.parseInt(sessionIdComponents[2]));
        } catch (Exception e) {
            throw new IllegalArgumentException(s + IS_NOT_A_VALID_SESSION_IDENTIFIER);
        }

        return sessionIdentifierBuilder.build();
    }
}
