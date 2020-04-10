package de.bausdorf.simcacing.tt.web;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Messages extends ArrayList<Message> {

    public List<Message> errors() {
        return this.stream()
                .filter(s -> s.getType().equalsIgnoreCase(Message.ERROR))
                .collect(Collectors.toList());
    }

    public List<Message> warnings() {
        return this.stream()
                .filter(s -> s.getType().equalsIgnoreCase(Message.WARN))
                .collect(Collectors.toList());
    }

    public List<Message> infos() {
        return this.stream()
                .filter(s -> s.getType().equalsIgnoreCase(Message.INFO))
                .collect(Collectors.toList());
    }
}
