package com.epam.taskgym.controller;

import com.epam.taskgym.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(FailAuthenticateException.class)
    public ResponseEntity<ErrorResponse> handleFailAuthenticateException(FailAuthenticateException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(ex.getClass().getName(), ex.getMessage(), LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(), ex.getCause(), ex.getLocalizedMessage(), List.of(ex.getStackTrace())));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPasswordException(InvalidPasswordException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getClass().getName(), ex.getMessage(), LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(), ex.getCause(), ex.getLocalizedMessage(), List.of(ex.getStackTrace())));
    }

    @ExceptionHandler(MissingAttributes.class)
    public ResponseEntity<ErrorResponse> handleMissingAttributes(MissingAttributes ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getClass().getName(), ex.getMessage(), LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getCause(), ex.getLocalizedMessage(), List.of(ex.getStackTrace())));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getClass().getName(), ex.getMessage(), LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getCause(), ex.getLocalizedMessage(), List.of(ex.getStackTrace())));
    }

    @ExceptionHandler(TraineeDeletionException.class)
    public ResponseEntity<ErrorResponse> handleTraineeDeletionException(TraineeDeletionException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(ex.getClass().getName(), ex.getMessage(), LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getCause(), ex.getLocalizedMessage(), List.of(ex.getStackTrace())));
    }

    // Handle generic exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(ex.getClass().getName(), ex.getMessage(), LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getCause(), ex.getLocalizedMessage(), List.of(ex.getStackTrace())));
    }
}
