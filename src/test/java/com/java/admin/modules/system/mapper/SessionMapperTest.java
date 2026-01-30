package com.java.admin.modules.system.mapper;

import com.java.admin.config.AuthProperties;
import com.java.admin.infrastructure.model.SecurityUserDetails;
import com.java.admin.testutil.AbstractMockTest;
import com.java.admin.testutil.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * SessionMapper Unit Tests
 *
 * <p>Test Coverage:
 * <ul>
 *   <li>Session save (save)</li>
 *   <li>Session find (find)</li>
 *   <li>Session delete (delete)</li>
 *   <li>Redis key building logic</li>
 * </ul>
 *
 * <p>Coverage Target: 90%+
 */
@DisplayName("SessionMapper Unit Tests")
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class SessionMapperTest extends AbstractMockTest {

    private static final long REFRESH_EXPIRE_MILLIS = 2592000000L; // 30 days
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private AuthProperties authProperties;
    @Mock
    private ValueOperations<String, Object> valueOperations;
    @InjectMocks
    private SessionMapper sessionMapper;
    private SecurityUserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        testUserDetails = TestDataFactory.createDefaultSecurityUserDetails();
        when(authProperties.getRefreshExpireMillis()).thenReturn(REFRESH_EXPIRE_MILLIS);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("Should successfully save user session")
    void shouldSaveUserSessionSuccessfully() {
        // When
        sessionMapper.save(testUserDetails);

        // Then
        verify(valueOperations, times(1)).set(
                eq("user:test-user-id"),
                eq(testUserDetails),
                eq(REFRESH_EXPIRE_MILLIS),
                eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    @DisplayName("Should use correct Redis key format")
    void shouldUseCorrectRedisKeyFormat() {
        // Given
        String userId = "custom-user-id";
        SecurityUserDetails userDetails = TestDataFactory.createSecurityUserDetails(userId);

        // When
        sessionMapper.save(userDetails);

        // Then
        verify(valueOperations, times(1)).set(
                eq("user:" + userId),
                eq(userDetails),
                anyLong(),
                any(TimeUnit.class)
        );
    }

    @Test
    @DisplayName("Should use configured expiration time")
    void shouldUseConfiguredExpireTime() {
        // Given
        long customExpireTime = 86400000L; // 1 day
        when(authProperties.getRefreshExpireMillis()).thenReturn(customExpireTime);

        // When
        sessionMapper.save(testUserDetails);

        // Then
        verify(valueOperations, times(1)).set(
                anyString(),
                any(),
                eq(customExpireTime),
                eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    @DisplayName("Should successfully find existing user session")
    void shouldFindExistingUserSession() {
        // Given
        when(valueOperations.get("user:test-user-id")).thenReturn(testUserDetails);

        // When
        SecurityUserDetails result = sessionMapper.find("test-user-id");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserid()).isEqualTo("test-user-id");
        verify(valueOperations, times(1)).get("user:test-user-id");
    }

    @Test
    @DisplayName("Should return null when session does not exist")
    void shouldReturnNullWhenSessionNotExists() {
        // Given
        when(valueOperations.get("user:non-existent-id")).thenReturn(null);

        // When
        SecurityUserDetails result = sessionMapper.find("non-existent-id");

        // Then
        assertThat(result).isNull();
        verify(valueOperations, times(1)).get("user:non-existent-id");
    }

    @Test
    @DisplayName("Should use correct key to find user session")
    void shouldUseCorrectKeyToFindSession() {
        // Given
        String userId = "user-123";
        when(valueOperations.get("user:" + userId)).thenReturn(testUserDetails);

        // When
        sessionMapper.find(userId);

        // Then
        verify(valueOperations, times(1)).get("user:" + userId);
    }

    @Test
    @DisplayName("Should successfully delete user session")
    void shouldDeleteUserSessionSuccessfully() {
        // When
        sessionMapper.delete("test-user-id");

        // Then
        verify(redisTemplate, times(1)).delete("user:test-user-id");
    }

    @Test
    @DisplayName("Should use correct key to delete user session")
    void shouldUseCorrectKeyToDeleteSession() {
        // Given
        String userId = "user-to-delete";

        // When
        sessionMapper.delete(userId);

        // Then
        verify(redisTemplate, times(1)).delete("user:" + userId);
    }

    @Test
    @DisplayName("Delete operation should be called once")
    void shouldCallDeleteOperationOnce() {
        // When
        sessionMapper.delete("test-user-id");

        // Then
        verify(redisTemplate, times(1)).delete(anyString());
        verify(redisTemplate, never()).delete((String) isNull());
    }

    @Test
    @DisplayName("Should handle user ID with special characters")
    void shouldHandleUserIdWithSpecialCharacters() {
        // Given
        String userId = "user@domain#123";
        SecurityUserDetails userDetails = TestDataFactory.createSecurityUserDetails(userId);

        // When
        sessionMapper.save(userDetails);
        sessionMapper.find(userId);
        sessionMapper.delete(userId);

        // Then
        verify(valueOperations, times(1)).set(
                eq("user:" + userId),
                any(),
                anyLong(),
                any(TimeUnit.class)
        );
        verify(valueOperations, times(1)).get("user:" + userId);
        verify(redisTemplate, times(1)).delete("user:" + userId);
    }

    @Test
    @DisplayName("Should handle empty user ID")
    void shouldHandleEmptyUserId() {
        // Given
        String emptyUserId = "";

        // When
        sessionMapper.save(testUserDetails);
        sessionMapper.find(emptyUserId);
        sessionMapper.delete(emptyUserId);

        // Then - Verify operations are called, even with empty string
        verify(valueOperations, times(1)).set(
                startsWith("user:"),
                any(),
                anyLong(),
                any(TimeUnit.class)
        );
    }

    @Test
    @DisplayName("Should handle null return value")
    void shouldHandleNullReturnValue() {
        // Given
        when(valueOperations.get(anyString())).thenReturn(null);

        // When
        SecurityUserDetails result = sessionMapper.find("any-user-id");

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should support save then find session")
    void shouldSupportSaveAndThenFind() {
        // Given
        when(valueOperations.get("user:test-user-id")).thenReturn(testUserDetails);

        // When
        sessionMapper.save(testUserDetails);
        SecurityUserDetails found = sessionMapper.find("test-user-id");

        // Then
        assertThat(found).isNotNull();
        verify(valueOperations, times(1)).set(anyString(), any(), anyLong(), any(TimeUnit.class));
        verify(valueOperations, times(1)).get(anyString());
    }

    @Test
    @DisplayName("Should support save then delete session")
    void shouldSupportSaveAndThenDelete() {
        // When
        sessionMapper.save(testUserDetails);
        sessionMapper.delete("test-user-id");

        // Then
        verify(valueOperations, times(1)).set(anyString(), any(), anyLong(), any(TimeUnit.class));
        verify(redisTemplate, times(1)).delete(anyString());
    }

    @Test
    @DisplayName("Should support complete session lifecycle")
    void shouldSupportCompleteSessionLifecycle() {
        // Given
        when(valueOperations.get("user:test-user-id"))
                .thenReturn(testUserDetails)
                .thenReturn(null); // Second find returns null (deleted)

        // When
        sessionMapper.save(testUserDetails);
        SecurityUserDetails found1 = sessionMapper.find("test-user-id");
        sessionMapper.delete("test-user-id");
        SecurityUserDetails found2 = sessionMapper.find("test-user-id");

        // Then
        assertThat(found1).isNotNull();
        assertThat(found2).isNull();
        verify(valueOperations, times(1)).set(anyString(), any(), anyLong(), any(TimeUnit.class));
        verify(valueOperations, times(2)).get(anyString());
        verify(redisTemplate, times(1)).delete(anyString());
    }

    @Test
    @DisplayName("Should support managing multiple user sessions simultaneously")
    void shouldHandleMultipleUserSessions() {
        // Given
        SecurityUserDetails user1 = TestDataFactory.createSecurityUserDetails("user-1");
        SecurityUserDetails user2 = TestDataFactory.createSecurityUserDetails("user-2");
        SecurityUserDetails user3 = TestDataFactory.createSecurityUserDetails("user-3");

        when(valueOperations.get("user:user-1")).thenReturn(user1);
        when(valueOperations.get("user:user-2")).thenReturn(user2);
        when(valueOperations.get("user:user-3")).thenReturn(user3);

        // When
        sessionMapper.save(user1);
        sessionMapper.save(user2);
        sessionMapper.save(user3);

        SecurityUserDetails found1 = sessionMapper.find("user-1");
        SecurityUserDetails found2 = sessionMapper.find("user-2");
        SecurityUserDetails found3 = sessionMapper.find("user-3");

        // Then
        assertThat(found1).isNotNull();
        assertThat(found2).isNotNull();
        assertThat(found3).isNotNull();
        assertThat(found1.getUserid()).isEqualTo("user-1");
        assertThat(found2.getUserid()).isEqualTo("user-2");
        assertThat(found3.getUserid()).isEqualTo("user-3");

        verify(valueOperations, times(3)).set(anyString(), any(), anyLong(), any(TimeUnit.class));
        verify(valueOperations, times(3)).get(anyString());
    }
}
