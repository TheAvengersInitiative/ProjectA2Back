package com.a2.backend.exception;

public class DiscussionNotFoundException extends RuntimeException {
    public DiscussionNotFoundException(String message) {
        super(message);
    }
}
