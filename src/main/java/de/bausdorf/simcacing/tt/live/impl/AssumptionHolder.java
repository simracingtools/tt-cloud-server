package de.bausdorf.simcacing.tt.live.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.bausdorf.simcacing.tt.live.impl.assumptions.Assumptions;
import de.bausdorf.simcacing.tt.live.impl.assumptions.DriverAssumption;
import de.bausdorf.simcacing.tt.live.model.Assumption;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.time.Duration;
import java.util.Optional;

@Component
@Slf4j
public class AssumptionHolder {

    public static final String COMMON = "common";
    public static final String DURATION_PATTERN = "HH:mm:ss.S";

    private TeamtacticsServerProperties config;
    private Assumptions assumptions;

    public AssumptionHolder(@Autowired TeamtacticsServerProperties config) {
        this.config = config;
    }

    @PostConstruct
    public void readAssumptions() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();

        try (InputStream yamlInputStream = new ClassPathResource(config.getAssumptionResource()).getInputStream()) {
            assumptions = mapper.readValue(yamlInputStream, Assumptions.class);
            log.info("Read {} track assumptions from {}", assumptions.getTracks().size(), config.getAssumptionResource());
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
