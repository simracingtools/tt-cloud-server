package de.bausdorf.simcacing.tt.live.model.client;

import de.bausdorf.simcacing.tt.live.clientapi.ClientData;
import lombok.*;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class EventData implements ClientData {
    private LocalTime sessionTime;
    private LocalTime sessionToD;
    private TrackLocationType trackLocationType;
    private List<FlagType> flags;
    private Duration towingTime;
    private Duration repairTime;
    private Duration optRepairTime;
    private List<ServiceFlagType> serviceFlags;
}
