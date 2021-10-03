package com.a2.backend.exception;

public class UserIsNotCollaboratorNorOwnerException extends RuntimeException {
    public UserIsNotCollaboratorNorOwnerException(String message) {
        super(message);
    }
}
