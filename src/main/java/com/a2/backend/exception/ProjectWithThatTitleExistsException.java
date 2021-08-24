package com.a2.backend.exception;

public class ProjectWithThatTitleExistsException extends RuntimeException {
    public ProjectWithThatTitleExistsException(String message) {
        super(message);
    }
}