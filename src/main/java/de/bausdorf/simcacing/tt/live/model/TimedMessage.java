package de.bausdorf.simcacing.tt.live.model;

import de.bausdorf.simcacing.tt.live.clientapi.ClientMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@AllArgsConstructor
@Getter
@Builder
public class TimedMessage {
    private final LocalTime timestamp;
    private final ClientMessage message;
}
