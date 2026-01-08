package com.java.admin.modules.system.mapper;

import com.java.admin.config.AuthProperties;
import com.java.admin.infrastructure.model.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 会话数据访问层
 * 负责用户会话的 Redis CRUD 操作
 */
@Component
@RequiredArgsConstructor
public class SessionMapper {

    private static final String SESSION_KEY_PREFIX = "user:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final AuthProperties authProperties;

    /**
     * 保存用户会话
     *
     * @param details 用户详情
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
     * 删除用户会话
     *
     * @param userId 用户ID
     */
    public void delete(String userId) {
        String key = buildKey(userId);
        redisTemplate.delete(key);
    }

    /**
     * 查找用户会话
     *
     * @param userId 用户ID
     * @return 用户详情，不存在返回 null
     */
    public SecurityUserDetails find(String userId) {
        String key = buildKey(userId);
        return (SecurityUserDetails) redisTemplate.opsForValue().get(key);
    }

    /**
     * 构建 Redis key
     *
     * @param userId 用户ID
     * @return Redis key
     */
    private String buildKey(String userId) {
        return SESSION_KEY_PREFIX + userId;
    }
}
