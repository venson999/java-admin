package com.java.admin.modules.system.service;

import com.java.admin.infrastructure.model.SecurityUserDetails;
import com.java.admin.infrastructure.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysLoginService {

    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, Object> redisTemplate;

    public String login(String username, String password) {

        UsernamePasswordAuthenticationToken authRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(username, password);

        // Perform authentication
        Authentication authentication = authenticationManager.authenticate(authRequest);
        if (authentication.isAuthenticated()) {

            // Get user details
            SecurityUserDetails userDetails = (SecurityUserDetails) authentication.getPrincipal();

            // Generate JWT token with 5 minutes validity
            int tokenValidityMs = 1000 * 60 * 5;
            String token = JwtUtil.createToken(userDetails.getUserid(), tokenValidityMs);

            // Cache user details with 5 minutes validity
            redisTemplate.opsForValue().set(
                    "user:" + userDetails.getUserid(),
                    userDetails,
                    tokenValidityMs,
                    TimeUnit.MILLISECONDS);

            return token;
        }
        return null;
    }

    public void logout(String token) {
        try {
            String userId = JwtUtil.parseClaims(token).getSubject();
            redisTemplate.delete(String.format("user:%s", userId));
        } catch (Exception e) {
            log.warn("Failed to process logout token", e);
        }
    }
}
