package de.bausdorf.simcacing.tt.live.clientapi;

import java.util.Map;

public interface TeamTacticsClientService {

    void receiveClientData(Map<String, Object> clientMessage);

}
