package de.bausdorf.simcacing.tt.live.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class SessionIdentifier {
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
