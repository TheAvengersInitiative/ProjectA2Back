package com.a2.backend.exception;

public class UserNotLoggedIn extends RuntimeException {
    public UserNotLoggedIn(String message) {
        super(message);
    }
}
