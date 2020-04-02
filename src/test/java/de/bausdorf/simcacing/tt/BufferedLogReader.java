package de.bausdorf.simcacing.tt;

import ch.qos.logback.core.net.server.Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bausdorf.simcacing.tt.clientapi.ClientMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.StringTokenizer;

public class BufferedLogReader extends BufferedReader {

    ObjectMapper mapper;

    public BufferedLogReader(Reader in) {
        super(in);
        mapper = new ObjectMapper();
    }

    @Override
    public String readLine() throws IOException {
        String line = super.readLine();
        line = line.replace("INFO:root:", "");
        line = line.replaceAll("\\\\", "");
        return line.substring(1, line.length() -1);
    }

    public ClientMessage readMessage() throws IOException {
        String jsonString = this.readLine();

        return mapper.readValue(jsonString, ClientMessage.class);
    }
}
