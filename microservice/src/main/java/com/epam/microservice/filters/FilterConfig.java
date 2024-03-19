package com.epam.microservice.filters;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<TransactionIdFilter> transactionIdFilterRegistration() {
        FilterRegistrationBean<TransactionIdFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TransactionIdFilter());
        registration.addUrlPatterns("/*");
        registration.setName("transactionIdFilter");
        registration.setOrder(1);
        return registration;
    }
}
