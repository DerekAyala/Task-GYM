package com.epam.taskgym.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TraineeDeletionException extends RuntimeException {
    public TraineeDeletionException(String s, DataAccessException e) {
    }
}
