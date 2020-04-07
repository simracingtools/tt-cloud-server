package de.bausdorf.simcacing.tt.live.impl.assumptions;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@Data
public class CarAssumption {
    String car;
    private String avgFuelPerLap;
    private String avgLapTime;
    private String avgPitstopTime;
    private Optional<String> carFuel;

    private List<DriverAssumption> drivers;

    public Optional<DriverAssumption> findByName(String name) {
        DriverAssumption driverOrCommon = new DriverAssumption(
                name,
                Optional.of(avgFuelPerLap),
                Optional.of(avgLapTime),
                Optional.of(avgPitstopTime),
                Optional.empty());

        for (DriverAssumption a : drivers) {
            if (a.getDriver().equalsIgnoreCase(name)) {
                if (a.getAvgFuelPerLap().isPresent()) {
                    driverOrCommon.setAvgFuelPerLap(a.getAvgFuelPerLap());
                }
                if (a.getAvgLapTime().isPresent()) {
                    driverOrCommon.setAvgLapTime(a.getAvgLapTime());
                }
                if (a.getAvgPitstopTime().isPresent()) {
                    driverOrCommon.setAvgPitstopTime(a.getAvgPitstopTime());
                }
                if (a.getCarFuel().isPresent()) {
                    driverOrCommon.setCarFuel(a.getCarFuel());
                }
            }
        }
        return Optional.ofNullable(driverOrCommon);
    }
}