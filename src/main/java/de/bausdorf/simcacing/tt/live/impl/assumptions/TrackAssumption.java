package de.bausdorf.simcacing.tt.live.impl.assumptions;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@Data
public class TrackAssumption {
    String track;
    private List<CarAssumption> cars;

    public Optional<DriverAssumption> findByName(String carName, String driverName) {
        for (CarAssumption a : cars) {
            if (a.getCar().equalsIgnoreCase(carName)) {
                return a.findByName(driverName);
            }
        }
        return Optional.empty();
    }
}