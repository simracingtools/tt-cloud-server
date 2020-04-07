package de.bausdorf.simcacing.tt.live.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.Optional;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Assumption {

    private Optional<Double> avgFuelPerLap;
    private double carFuel;
    private Optional<Duration> avgLapTime;
    private Optional<Duration> avgPitStopTime;
}
