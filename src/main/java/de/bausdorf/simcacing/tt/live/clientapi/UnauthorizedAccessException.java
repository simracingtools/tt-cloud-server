package de.bausdorf.simcacing.tt.live.clientapi;

import lombok.RequiredArgsConstructor;


public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
