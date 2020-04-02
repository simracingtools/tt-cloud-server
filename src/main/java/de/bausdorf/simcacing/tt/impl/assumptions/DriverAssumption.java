package de.bausdorf.simcacing.tt.impl.assumptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DriverAssumption {
    private String driver;
    private Optional<String> avgFuelPerLap;
    private Optional<String> avgLapTime;
    private Optional<String> avgPitstopTime;
    private Optional<String> carFuel;
}