package de.bausdorf.simcacing.tt.live.clientapi;

import java.util.Map;

public interface TeamTacticsClientService {

    String receiveClientData(Map<String, Object> clientMessage);

}
