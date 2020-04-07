package de.bausdorf.simcacing.tt.live.impl;

import de.bausdorf.simcacing.tt.live.model.TimedMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalTime;

@Slf4j
public class LogReplay {

    SessionHolder holder = null;

    public LogReplay(SessionHolder holder) {
        this.holder = holder;
    }

    public void replayLogfile(InputStream logStream, boolean timed) {
        try (BufferedLogReader logReader = new BufferedLogReader(new InputStreamReader(logStream))) {
            LocalTime replayStart = null;
            while( logReader.ready() ) {
                TimedMessage timedMessage = logReader.readMessage();
                if(timed) {
                    if( replayStart == null ) {
                        replayStart = timedMessage.getTimestamp();
                    } else {
                        Duration waitTime = Duration.between(replayStart, timedMessage.getTimestamp());
                        log.info("Sleeping {} sec to replay next message", ((double)waitTime.toMillis()) / 1000);
                        Thread.sleep(waitTime.toMillis());
                    }
                }
                holder.processMessage(timedMessage.getMessage());
            }
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
        }

    }
}
