package com.epam.microservice.Controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final String error;
    private final String message;
    private final LocalDateTime timestamp;
    private final int status;
    private final Throwable cause;
    private final String localizedMessage;
    private final List<StackTraceElement> stackTrace;
}
