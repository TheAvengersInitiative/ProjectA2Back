package com.a2.backend.exception;

public class InvalidPasswordRecoveryException extends RuntimeException {
    public InvalidPasswordRecoveryException(String message) {
        super(message);
    }
}
