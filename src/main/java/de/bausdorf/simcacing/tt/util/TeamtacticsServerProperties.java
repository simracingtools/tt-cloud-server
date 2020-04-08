package de.bausdorf.simcacing.tt.util;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties(prefix="teamtactics")
@NoArgsConstructor
@Data
public class TeamtacticsServerProperties {
    private String version;
    private String assumptionResource;
    private String userCollectionName;
    private Long driverRepositoryCacheMinutes;
    private Long teamRepositoryCacheMinutes;
    private Long userRepositoryCacheMinutes;
}
