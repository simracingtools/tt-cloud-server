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
import de.bausdorf.simcacing.tt.live.clientapi.ClientMessage;
import de.bausdorf.simcacing.tt.live.model.client.TimedMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BufferedLogReader extends BufferedReader {

    ObjectMapper mapper;

    public BufferedLogReader(Reader in) {
        super(in);
        mapper = new ObjectMapper();
    }

    @Override
    public String readLine() throws IOException {
        String line = super.readLine();
        line = line.replaceAll("\\\\", "");
        return line.substring(0, line.length() -1);
    }

    public TimedMessage readMessage() throws IOException {
        String line = this.readLine();
        String[] parts = line.split("\\$");

        return TimedMessage.builder()
                //time format: 2020-04-04 15:34:09,530
                .timestamp(LocalTime.parse(parts[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS")))
                .message(mapper.readValue(parts[1].substring(1), ClientMessage.class))
                .build();
    }
}
