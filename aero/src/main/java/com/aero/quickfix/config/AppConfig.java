package com.aero.quickfix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Application configuration for REST clients and utilities.
 */
@Configuration
public class AppConfig {

    /**
     * RestTemplate bean for HTTP requests to external APIs (EODHD).
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
