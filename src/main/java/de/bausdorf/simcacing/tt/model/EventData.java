package de.bausdorf.simcacing.tt.model;

import de.bausdorf.simcacing.tt.clientapi.ClientData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventData implements ClientData {
    private LocalTime sessionTime;
    private TrackLocationType trackLocationType;
    private List<FlagType> flags;
    private Duration towingTime;
    private Duration repairTime;
    private Duration optRepairTime;
}
