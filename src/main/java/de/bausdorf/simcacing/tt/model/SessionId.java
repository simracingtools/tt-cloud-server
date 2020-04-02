package de.bausdorf.simcacing.tt.model;

import de.bausdorf.simcacing.tt.impl.SessionHolder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
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
