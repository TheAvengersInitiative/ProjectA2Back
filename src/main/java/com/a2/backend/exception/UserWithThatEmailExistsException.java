package com.a2.backend.exception;

public class UserWithThatEmailExistsException extends RuntimeException {
    public UserWithThatEmailExistsException(String message) {
        super(message);
    }
}
