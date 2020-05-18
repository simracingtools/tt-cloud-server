package de.bausdorf.simcacing.tt.live.impl;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 bausdorf engineering
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.bausdorf.simcacing.tt.live.impl.assumptions.Assumptions;
import de.bausdorf.simcacing.tt.live.impl.assumptions.DriverAssumption;
import de.bausdorf.simcacing.tt.live.model.client.Assumption;
import de.bausdorf.simcacing.tt.util.TeamtacticsServerProperties;
import de.bausdorf.simcacing.tt.util.TimeTools;
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
        final Optional<DriverAssumption> driverAssumption = assumptions.findByName(track, car, driver);
        if (!driverAssumption.isPresent()) {
            return null;
        }
        double avgFuelPerLap = Double.parseDouble(driverAssumption.get().getAvgFuelPerLap().orElse("0.000"));
        Duration avgLapTime = TimeTools.durationFromPattern(driverAssumption.get().getAvgLapTime().orElse("00:00.000"), DURATION_PATTERN);
        Duration avgPitstopTime = TimeTools.durationFromPattern(driverAssumption.get().getAvgPitstopTime().orElse("00:00.000"), DURATION_PATTERN);
        double carFuel = driverAssumption.get().getCarFuel().isPresent() ? Double.parseDouble(driverAssumption.get().getCarFuel().orElse("0.000")) : carMaxFuel;
        return Assumption.builder()
                .avgFuelPerLap(Optional.of(avgFuelPerLap))
                .avgLapTime(Optional.of(avgLapTime))
                .avgPitStopTime(Optional.of(avgPitstopTime))
                .carFuel(carFuel)
                .build();
    }
}
