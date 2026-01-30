package com.java.admin.modules.system.mapper;

import com.java.admin.config.AuthProperties;
import com.java.admin.infrastructure.model.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Session data access layer
 * Responsible for Redis CRUD operations for user sessions
 */
@Component
@RequiredArgsConstructor
public class SessionMapper {

    private static final String SESSION_KEY_PREFIX = "user:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final AuthProperties authProperties;

    /**
     * Save user session
     *
     * @param details User details
     */
    public void save(SecurityUserDetails details) {
        String key = buildKey(details.getUserid());
        redisTemplate.opsForValue().set(
                key,
                details,
                authProperties.getRefreshExpireMillis(),
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * Delete user session
     *
     * @param userId User ID
     */
    public void delete(String userId) {
        String key = buildKey(userId);
        redisTemplate.delete(key);
    }

    /**
     * Find user session
     *
     * @param userId User ID
     * @return User details, or null if not found
     */
    public SecurityUserDetails find(String userId) {
        String key = buildKey(userId);
        return (SecurityUserDetails) redisTemplate.opsForValue().get(key);
    }

    /**
     * Build Redis key
     *
     * @param userId User ID
     * @return Redis key
     */
    private String buildKey(String userId) {
        return SESSION_KEY_PREFIX + userId;
    }
}
