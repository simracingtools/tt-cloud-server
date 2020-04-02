package de.bausdorf.simcacing.tt.impl;

import de.bausdorf.simcacing.tt.BufferedLogReader;
import de.bausdorf.simcacing.tt.clientapi.ClientMessage;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Slf4j
public class SessionHolderTest {
    @Autowired
    private SessionHolder holder;

    @Test
    public void replayLog() {
        ClassPathResource resource = new ClassPathResource("/logs/irtactics.log");
        try (BufferedLogReader logReader = new BufferedLogReader(new InputStreamReader(resource.getInputStream()))) {
            while( logReader.ready() ) {
                ClientMessage message = logReader.readMessage();
                holder.processMessage(message);
            }
        } catch (IOException e) {
            fail(e.getMessage(), e);
        }
    }
}
