package com.java.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    /**
     * Access token expiration time (milliseconds)
     */
    private long accessExpireMillis;

    /**
     * Session expiration time (milliseconds)
     */
    private long refreshExpireMillis;
}
