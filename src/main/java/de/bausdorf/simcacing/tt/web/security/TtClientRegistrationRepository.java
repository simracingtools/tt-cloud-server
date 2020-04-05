package de.bausdorf.simcacing.tt.web.security;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TtClientRegistrationRepository {
    private final Map<String, TtUser> registrations;

    public TtClientRegistrationRepository() {
        registrations = new HashMap<>();
    }

}
