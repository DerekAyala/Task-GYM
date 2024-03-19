package com.epam.taskgym.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class AuthorizationFilter implements Filter {
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String bearerToken = httpRequest.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken)) {
            MDC.put(AUTHORIZATION_HEADER, bearerToken);
        }
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(AUTHORIZATION_HEADER);
        }
    }
}
