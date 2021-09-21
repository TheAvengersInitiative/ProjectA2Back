package com.a2.backend.exception;

public class LanguageNotValidException extends RuntimeException {
    public LanguageNotValidException(String message) {
        super(message);
    }
}
