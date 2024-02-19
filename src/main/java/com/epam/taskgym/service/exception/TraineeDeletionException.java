package com.epam.taskgym.service.exception;

import org.springframework.dao.DataAccessException;

public class TraineeDeletionException extends RuntimeException {
    public TraineeDeletionException(String s, DataAccessException e) {
    }
}
