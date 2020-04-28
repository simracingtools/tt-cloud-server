package de.bausdorf.simcacing.tt.live.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Slf4j
public class SessionHolderTest {
    @Autowired
    private SessionHolder holder;

    @Configuration
    static class TestConfig {
        @Bean
        @ConditionalOnMissingBean
        SessionHolder getSessionHolder() {
            return new SessionHolder();
        }
    }

    @Test
    @Disabled
    public void fastLogReplay() {
        try {
            LogReplay replay = new LogReplay(holder);
            ClassPathResource resource = new ClassPathResource("/logs/irtactics.log");
            replay.replayLogfile(resource.getInputStream(), false);
        } catch (IOException e) {
            fail(e.getMessage(), e);
        }
    }

    @Test
    @Disabled
    public void timedLogReplay() {
        try {
            LogReplay replay = new LogReplay(holder);
            ClassPathResource resource = new ClassPathResource("/logs/irtactics.log");
            replay.replayLogfile(resource.getInputStream(), false);
        } catch (IOException e) {
            fail(e.getMessage(), e);
        }
    }

}
