package com.a2.backend.exception;

public class ProjectWithThatIdDoesntExistException extends RuntimeException {
    public ProjectWithThatIdDoesntExistException(String message) {
        super(message);
    }
}
