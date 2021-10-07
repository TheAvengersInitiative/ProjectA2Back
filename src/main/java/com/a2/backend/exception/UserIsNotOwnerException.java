package com.a2.backend.exception;

public class UserIsNotOwnerException extends RuntimeException {
    public UserIsNotOwnerException(String message) {
        super(message);
    }
}
