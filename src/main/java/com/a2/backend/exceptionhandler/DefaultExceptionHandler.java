package com.a2.backend.exceptionhandler;

import com.a2.backend.exception.ProjectWithThatTitleExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
public class DefaultExceptionHandler {

    Logger logger = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @ExceptionHandler(ProjectWithThatTitleExistsException.class)
    protected ResponseEntity<Object> handleProjectWithThatTitleExists(ProjectWithThatTitleExistsException exception) {
        logger.info(exception.getMessage());
        return ResponseEntity.badRequest().build();
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException exception) {
        logger.info(exception.getMessage());
        return ResponseEntity.badRequest().build();
    }

}