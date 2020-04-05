package de.bausdorf.simcacing.tt.impl;

import com.google.api.client.util.Value;
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
}
