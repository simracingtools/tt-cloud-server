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

import de.bausdorf.simcacing.tt.live.model.client.TimedMessage;
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
            Thread.currentThread().interrupt();
        }

    }
}
