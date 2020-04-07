package de.bausdorf.simcacing.tt.live.impl.assumptions;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@Data
public class Assumptions {
    private List<TrackAssumption> tracks;

    public Optional<DriverAssumption> findByName(String trackName, String carName, String driverName) {
        for (TrackAssumption a : tracks) {
            if (a.getTrack().equalsIgnoreCase(trackName)) {
                return a.findByName(carName, driverName);
            }
        }
        return Optional.empty();
    }
}
