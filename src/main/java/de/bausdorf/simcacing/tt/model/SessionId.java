package de.bausdorf.simcacing.tt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SessionId {
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
}
