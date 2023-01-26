package de.bausdorf.simcacing.tt.util;

import de.bausdorf.simcacing.tt.live.model.iracing.SessionData;

import java.util.List;
import java.util.stream.Collectors;

public class DataTools {
    private DataTools() { super(); }

    public static List<SessionData.Session> lastSessionFirst(SessionData.SessionInfo sessionsInfo) {
        return sessionsInfo.getSessions().stream()
                .sorted((s1, s2) -> Integer.compare(s2.getSessionNum(), s1.getSessionNum()))
                .collect(Collectors.toList());
    }
}
