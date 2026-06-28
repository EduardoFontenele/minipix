package dev.eduardo.minipix.api.exception;

public class TransactionNotAllowedException extends RuntimeException {
    public TransactionNotAllowedException(String reason) {
        super(reason);
    }
}
