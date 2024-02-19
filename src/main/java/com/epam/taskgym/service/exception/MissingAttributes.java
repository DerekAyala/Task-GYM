package com.epam.taskgym.service.exception;

public class MissingAttributes extends RuntimeException {
    public MissingAttributes(String message) {
        super(message);
    }
}
