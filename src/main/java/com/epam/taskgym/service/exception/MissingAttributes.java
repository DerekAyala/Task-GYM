package com.epam.taskgym.service.exception;

public class MissingAttributes extends RuntimeException {
    public MissingAttributes(String message) {
        super(message);
    }

    public MissingAttributes(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingAttributes(Throwable cause) {
        super(cause);
    }

    public MissingAttributes() {
    }
}
