package de.bausdorf.simcacing.tt.live.clientapi;

import java.util.Map;
import java.util.Optional;

public interface TeamTacticsClientService {

    String receiveClientData(String clientMessage, Optional<String> clientAccessToken);

}
