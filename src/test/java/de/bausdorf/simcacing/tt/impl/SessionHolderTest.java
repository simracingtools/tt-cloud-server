package de.bausdorf.simcacing.tt.impl;

import de.bausdorf.simcacing.tt.model.TimedMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Slf4j
public class SessionHolderTest {
    @Autowired
    private SessionHolder holder;

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
