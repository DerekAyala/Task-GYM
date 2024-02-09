package com.epam.taskgym.service.exception;

public class FailAuthenticateException extends RuntimeException{
    public FailAuthenticateException(String message) {
        super(message);
    }

    public FailAuthenticateException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailAuthenticateException(Throwable cause) {
        super(cause);
    }

    public FailAuthenticateException() {
    }
}
