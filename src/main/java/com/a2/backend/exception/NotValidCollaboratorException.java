package com.a2.backend.exception;

public class NotValidCollaboratorException extends RuntimeException {
    public NotValidCollaboratorException(String message) {
        super(message);
    }
}
