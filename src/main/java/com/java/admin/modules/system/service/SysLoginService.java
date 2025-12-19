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

@Service
@RequiredArgsConstructor
@Slf4j
public class SysLoginService {

    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, Object> redisTemplate;

    public String login(String username, String password) {
        log.info("User login attempt - Username: {}", username);

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

            log.info("Operation [LOGIN] - User: {}, Success: true, Details: {}", userDetails.getUserid(), username);
            return token;
        }

        return null;
    }

    public void logout(String token) {
        try {
            String userId = JwtUtil.parseClaims(token).getSubject();
            redisTemplate.delete(String.format("user:%s", userId));
            log.info("Operation [LOGOUT] - User: {}, Success: true", userId);
        } catch (Exception e) {
            // 登出是幂等操作，即使 token 无效或 Redis 异常，也应视为成功
            // 因为用户已无法使用当前 token 访问系统
            log.warn("Logout completed with warning - Token may be invalid or already expired: {}", e.getMessage());
        }
    }
}
