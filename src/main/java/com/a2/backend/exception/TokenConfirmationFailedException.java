package com.a2.backend.exception;

public class TokenConfirmationFailedException extends RuntimeException {
    public TokenConfirmationFailedException(String message) {
        super(message);
    }
}
