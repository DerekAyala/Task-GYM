package com.epam.taskgym.service.exception;

public class FailAuthenticateException extends RuntimeException{
    public FailAuthenticateException(String message) {
        super(message);
    }
}
