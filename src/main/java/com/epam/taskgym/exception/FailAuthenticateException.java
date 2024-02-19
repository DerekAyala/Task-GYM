package com.epam.taskgym.exception;

public class FailAuthenticateException extends RuntimeException{
    public FailAuthenticateException(String message) {
        super(message);
    }
}
