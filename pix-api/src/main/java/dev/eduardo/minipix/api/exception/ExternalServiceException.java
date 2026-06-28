package dev.eduardo.minipix.api.exception;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(Throwable cause) {
        super("An external service failed to respond in time", cause);
    }
}
