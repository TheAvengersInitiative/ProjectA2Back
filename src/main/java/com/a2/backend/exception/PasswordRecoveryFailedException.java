package com.a2.backend.exception;

public class PasswordRecoveryFailedException extends RuntimeException {
    public PasswordRecoveryFailedException(String message) {
        super(message);
    }
}
