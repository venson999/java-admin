package com.java.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    /**
     * Paths that skip authentication
     */
    private List<String> skipPaths;

    /**
     * Access token expiration time (milliseconds)
     */
    private long accessExpireMillis;

    /**
     * Session expiration time (milliseconds)
     */
    private long refreshExpireMillis;
}
