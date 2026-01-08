package com.java.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    /**
     * Access token 过期时间(毫秒)
     */
    private long accessExpireMillis;

    /**
     * 会话过期时间(毫秒)
     */
    private long refreshExpireMillis;
}
