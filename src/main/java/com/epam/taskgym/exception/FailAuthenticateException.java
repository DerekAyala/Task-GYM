package com.epam.taskgym.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class FailAuthenticateException extends RuntimeException{
    public FailAuthenticateException(String message) {
        super(message);
    }
}
