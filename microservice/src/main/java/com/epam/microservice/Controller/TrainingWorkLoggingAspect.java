package com.epam.microservice.Controller;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;

@Aspect
@Component
public class TrainingWorkLoggingAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingWorkLoggingAspect.class);

    @Around("execution(* com.epam.microservice.Controller.TrainingWorkController.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        if (MDC.get("transactionId") == null) {
            MDC.put("transactionId", UUID.randomUUID().toString());
        }
        try {
            LOGGER.info("Starting method: {}", methodName);
            return joinPoint.proceed();
        } finally {
            MDC.remove("transactionId");
        }
    }
}
