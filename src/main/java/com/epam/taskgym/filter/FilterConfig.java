package com.epam.taskgym.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<AuthorizationFilter> authorizationFilterRegistration() {
        FilterRegistrationBean<AuthorizationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new AuthorizationFilter());
        registration.addUrlPatterns("/*");
        registration.setName("authorizationFilter");
        registration.setOrder(3);
        return registration;
    }
}
