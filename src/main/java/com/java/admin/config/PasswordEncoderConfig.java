package com.java.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password Encoder Configuration
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * BCrypt password encoder bean
     *
     * <p>Uses BCrypt algorithm with default strength (10 rounds).
     * BCrypt is a one-way hashing algorithm suitable for password storage.
     *
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
