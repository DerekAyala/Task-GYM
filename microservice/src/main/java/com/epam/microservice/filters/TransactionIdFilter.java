package com.epam.microservice.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class TransactionIdFilter implements Filter{

    private static final String TRANSACTION_ID_HEADER = "Transaction-ID";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String transactionId = httpRequest.getHeader(TRANSACTION_ID_HEADER);
        if (StringUtils.hasText(transactionId)) {
            MDC.put("transactionId", transactionId);
        }
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove("transactionId");
        }
    }
}
