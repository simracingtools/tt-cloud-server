package de.bausdorf.simcacing.tt.live.clientapi;

public class InvalidClientMessageException extends RuntimeException {
    public InvalidClientMessageException(String message) {
        super(message);
    }
    public InvalidClientMessageException(String message, Throwable e) {
        super(message, e);
    }
    public InvalidClientMessageException(Throwable e) {
        super(e.getMessage(), e);
    }
}
