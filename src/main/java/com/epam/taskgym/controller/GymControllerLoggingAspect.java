package com.epam.taskgym.controller;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Aspect
@Component
public class GymControllerLoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(GymControllerLoggingAspect.class);

    //Aspect for transaction level logging
    @Around("execution(* com.epam.taskgym.controller.GymController.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // Start the transaction
        String transactionId = UUID.randomUUID().toString();
        // Put transactionId into MDC
        MDC.put("transactionId", transactionId);
        MDC.put("MethodName", joinPoint.getSignature().getName());
        try {
            String methodName = joinPoint.getSignature().getName();
            LOGGER.info("Starting method: {}, Transaction Id: {}", methodName, transactionId);
            return joinPoint.proceed();
        } finally {
            MDC.remove("transactionId");
            MDC.remove("MethodName");
        }
    }

    // Aspect for specific REST call details logging
    @AfterReturning(pointcut = "execution(* com.epam.taskgym.controller.GymController.*(..))", returning = "result")
    public void logRestCallDetails(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String requestDetails = Arrays.toString(joinPoint.getArgs());
        String responseDetails = result != null ? result.toString() : "No response";

        LOGGER.info("Method: {}, Request: {}, Response: {}", methodName, requestDetails, responseDetails);
    }
}
