package com.epam.taskgym.controller;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Aspect
@Component
public class GymControllerLoggingAspect {

    private static final Logger transactionLogger = LoggerFactory.getLogger("TransactionLogger");
    private static final Logger restCallLogger = LoggerFactory.getLogger("RestCallLogger");

    // Aspect for transaction level logging
    @AfterReturning("execution(* com.epam.taskgym.controller.GymController.*(..))")
    public void logTransaction(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String transactionId = generateTransactionId(); // You need to implement this method
        transactionLogger.info("Transaction ID: {}, Method: {}", transactionId, methodName);
    }

    // Aspect for specific REST call details logging
    @AfterReturning(pointcut = "execution(* com.epam.taskgym.controller.GymController.*(..))", returning = "result")
    public void logRestCallDetails(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String requestDetails = Arrays.toString(joinPoint.getArgs());
        String responseDetails = result != null ? result.toString() : "No response";

        restCallLogger.info("Method: {}, Request: {}, Response: {}", methodName, requestDetails, responseDetails);
    }

    // Method to generate transaction ID (you can use any logic you prefer)
    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }
}
