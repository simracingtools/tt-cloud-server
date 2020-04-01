package de.bausdorf.simcacing.tt.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.bausdorf.simcacing.tt.model.Assumption;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Component
@NoArgsConstructor
@Getter
@Slf4j
public class AssumptionHolder {

    public static final String COMMON = "common";
    public static final String DURATION_PATTERN = "HH:mm:ss.S";

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

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public class DriverAssumption {
        private String driver;
        private Optional<String> avgFuelPerLap;
        private Optional<String> avgLapTime;
        private Optional<String> avgPitstopTime;
        private Optional<String> carFuel;
    }

    private Assumptions assumptions;

    @PostConstruct
    public void readAssumptions() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();

        try (InputStream yamlInputStream = new ClassPathResource("trackAssumptions.yml").getInputStream()) {
            assumptions = mapper.readValue(yamlInputStream, Assumptions.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public Assumption getAssumption(@NonNull String track, @NonNull String car, String driver, double carMaxFuel) {
        Optional<DriverAssumption> driverAssumption = assumptions.findByName(track, car, driver);
        if (!driverAssumption.isPresent()) {
            return null;
        }
        double avgFuelPerLap = Double.parseDouble(driverAssumption.get().getAvgFuelPerLap().get());
        Duration avgLapTime = TimeTools.durationFromPattern(driverAssumption.get().getAvgLapTime().get(), DURATION_PATTERN);
        Duration avgPitstopTime = TimeTools.durationFromPattern(driverAssumption.get().getAvgPitstopTime().get(), DURATION_PATTERN);
        double carFuel = driverAssumption.get().getCarFuel().isPresent() ? Double.parseDouble(driverAssumption.get().getCarFuel().get()) : carMaxFuel;
        return Assumption.builder()
                .avgFuelPerLap(Optional.of(avgFuelPerLap))
                .avgLapTime(Optional.of(avgLapTime))
                .avgPitStopTime(Optional.of(avgPitstopTime))
                .carFuel(carFuel)
                .build();
    }
}
