package com.a2.backend.exception;

public class DiscussionWithThatTitleExistsInProjectException extends RuntimeException {
    public DiscussionWithThatTitleExistsInProjectException(String message) {
        super(message);
    }
}
