package de.bausdorf.simcacing.tt.util;

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

import de.bausdorf.simracing.irdataapi.config.ConfigProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties(prefix="teamtactics")
@NoArgsConstructor
@Data
public class TeamtacticsServerProperties {
    private String version;
    private String userCollectionName;
    private String serverBaseUrl;
    private Long driverRepositoryCacheMinutes;
    private Long teamRepositoryCacheMinutes;
    private Long userRepositoryCacheMinutes;
    private Long inactiveSessionTimeoutMinutes;
    private boolean shiftSessionStartTimeToNow;
    private int serviceDurationSecondsWs;
    private int serviceDurationSecondsTyres;
    private int serviceDurationSecondsFuel10l;
    private String mqttBrokerHost;
    private String mqttUsername;
    private String mqttPassword;
    private String mqttClientId;

    @Bean
    ConfigProperties configProperties() {
        return new ConfigProperties();
    }
}
