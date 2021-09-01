package com.a2.backend.exception;

public class UserWithThatNicknameExistsException extends RuntimeException {
    public UserWithThatNicknameExistsException(String message) {
        super(message);
    }
}
