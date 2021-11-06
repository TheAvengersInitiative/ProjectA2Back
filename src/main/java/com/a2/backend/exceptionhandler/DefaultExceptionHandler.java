package com.a2.backend.exceptionhandler;

import com.a2.backend.exception.*;
import java.util.Arrays;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class DefaultExceptionHandler {

    Logger logger = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @ExceptionHandler(ProjectWithThatTitleExistsException.class)
    protected ResponseEntity<?> handleProjectWithThatTitleExists(
            ProjectWithThatTitleExistsException exception) {
        logger.info(exception.getMessage());
        return new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<?> handleProjectNotFoundException(ProjectNotFoundException exception) {
        logger.info(exception.getMessage());
        return new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserWithThatNicknameExistsException.class)
    protected ResponseEntity<?> handleUserWithThatNicknameExists(
            UserWithThatNicknameExistsException exception) {
        logger.info(exception.getMessage());
        return new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserWithThatEmailExistsException.class)
    protected ResponseEntity<?> handleUserWithThatEmailExists(
            UserWithThatEmailExistsException exception) {
        logger.info(exception.getMessage());
        return new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String validationError(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .map(
                        f -> {
                            String msg =
                                    Arrays.asList(Objects.requireNonNull(f.getCodes()))
                                                    .contains("Pattern")
                                            ? "Invalid pattern for field"
                                            : f.getDefaultMessage();
                            return f.getField() + ": " + msg;
                        })
                .reduce("", (a, s) -> a + s + '\n');
    }

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<?> handleUserNotFoundException(UserNotFoundException exception) {
        logger.info(exception.getMessage());
        return new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenConfirmationFailedException.class)
    public ResponseEntity<?> validateToken(TokenConfirmationFailedException exception) {
        logger.info(exception.getMessage());
        return new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordRecoveryFailedException.class)
    public ResponseEntity<?> PasswordRecoveryFailedException(
            PasswordRecoveryFailedException exception) {
        logger.info(exception.getMessage());
        return new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPasswordRecoveryException.class)
    public ResponseEntity<?> InvalidPasswordRecoveryException(
            InvalidPasswordRecoveryException exception) {
        logger.info(exception.getMessage());
        return new ResponseEntity(exception.getMessage(), HttpStatus.OK);
    }

    @ExceptionHandler(LanguageNotValidException.class)
    protected ResponseEntity<?> handleLanguageNotValid(LanguageNotValidException exception) {
        logger.info(exception.getMessage());
        return new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidProjectCollaborationApplicationException.class)
    protected ResponseEntity<?> handleInvalidProjectCollaborationApplication(
            InvalidProjectCollaborationApplicationException exception) {
        logger.info(exception.getMessage());
        return new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DiscussionWithThatTitleExistsInProjectException.class)
    protected ResponseEntity<?> handleDiscussionWithThatTitleExistsInProject(
            DiscussionWithThatTitleExistsInProjectException exception) {
        logger.info(exception.getMessage());
        return new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserIsNotCollaboratorNorOwnerException.class)
    protected ResponseEntity<?> handleUserIsNotCollaboratorNorOwner(
            UserIsNotCollaboratorNorOwnerException exception) {
        logger.info(exception.getMessage());
        return new ResponseEntity(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidUserException.class)
    protected ResponseEntity<?> handleInvalidUser(InvalidUserException exception) {
        logger.info(exception.getMessage());
        return new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotValidCollaboratorException.class)
    protected ResponseEntity<?> handleNotValidCollaborator(
            NotValidCollaboratorException exception) {
        logger.info(exception.getMessage());
        return new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DiscussionNotFoundException.class)
    protected ResponseEntity<?> handleDiscussionNotFound(DiscussionNotFoundException exception) {
        logger.info(exception.getMessage());
        return new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserIsNotOwnerException.class)
    protected ResponseEntity<?> handleUserIsNotOwner(UserIsNotOwnerException exception) {
        logger.info(exception.getMessage());
        return new ResponseEntity(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    protected ResponseEntity<?> handleCommentNotFound(CommentNotFoundException exception) {
        logger.info(exception.getMessage());
        return new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    protected ResponseEntity<?> handleNotificationNotFound(
            NotificationNotFoundException exception) {
        logger.info(exception.getMessage());
        return new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
