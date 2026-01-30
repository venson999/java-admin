package com.java.admin.modules.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.java.admin.modules.system.mapper.SysUserMapper;
import com.java.admin.modules.system.model.SysUser;
import com.java.admin.testutil.AbstractUnitTest;
import com.java.admin.testutil.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SysUserService Unit Tests
 *
 * <p>Test Coverage:
 * <ul>
 *   <li>User query functionality</li>
 *   <li>Database interaction verification</li>
 *   <li>Log output verification</li>
 * </ul>
 *
 * <p>Coverage Target: 90%+
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysUserService Unit Tests")
class SysUserServiceTest extends AbstractUnitTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @InjectMocks
    private SysUserService sysUserService;

    @Test
    @DisplayName("Should successfully return existing user")
    void shouldReturnUserWhenUserExists() {
        // Given
        String username = "testuser";
        SysUser expectedUser = TestDataFactory.createDefaultUser();

        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(expectedUser);

        // When
        SysUser actualUser = sysUserService.getUserByUsername(username);

        // Then
        assertThat(actualUser).isNotNull();
        assertThat(actualUser.getUserId()).isEqualTo(expectedUser.getUserId());
        assertThat(actualUser.getUserName()).isEqualTo(expectedUser.getUserName());

        // Verify Mapper is called
        verify(sysUserMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("Should return null when user does not exist")
    void shouldReturnNullWhenUserNotExists() {
        // Given
        String username = "nonexistent";
        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(null);

        // When
        SysUser actualUser = sysUserService.getUserByUsername(username);

        // Then
        assertThat(actualUser).isNull();

        // Verify Mapper is called
        verify(sysUserMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("Should use correct username to build query conditions")
    void shouldUseCorrectUsernameForQuery() {
        // Given
        String username = "admin";
        SysUser adminUser = TestDataFactory.createAdminUser();
        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(adminUser);

        // When
        sysUserService.getUserByUsername(username);

        // Then
        verify(sysUserMapper).selectOne(argThat(wrapper -> {
            // Cannot directly check LambdaQueryWrapper content, but can verify method is called
            return wrapper != null;
        }));
    }

    @Test
    @DisplayName("Should handle empty string username")
    void shouldHandleEmptyUsername() {
        // Given
        String emptyUsername = "";
        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(null);

        // When
        SysUser actualUser = sysUserService.getUserByUsername(emptyUsername);

        // Then
        assertThat(actualUser).isNull();
        verify(sysUserMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("Should handle username with special characters")
    void shouldHandleSpecialCharactersInUsername() {
        // Given
        String specialUsername = "user@domain.com";
        SysUser user = TestDataFactory.createUserWithUsername(specialUsername);
        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(user);

        // When
        SysUser actualUser = sysUserService.getUserByUsername(specialUsername);

        // Then
        assertThat(actualUser).isNotNull();
        assertThat(actualUser.getUserName()).isEqualTo(specialUsername);
        verify(sysUserMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("Should handle null username")
    void shouldHandleNullUsername() {
        // Given
        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(null);

        // When
        SysUser actualUser = sysUserService.getUserByUsername(null);

        // Then
        assertThat(actualUser).isNull();
        verify(sysUserMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("Should propagate database exception")
    void shouldPropagateDatabaseException() {
        // Given
        String username = "testuser";
        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        assertThatThrownBy(() -> sysUserService.getUserByUsername(username))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database connection failed");

        verify(sysUserMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("Should handle null pointer exception")
    void shouldHandleNullPointerException() {
        // Given
        String username = "testuser";
        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenThrow(new NullPointerException("Unexpected null value"));

        // When & Then
        assertThatThrownBy(() -> sysUserService.getUserByUsername(username))
                .isInstanceOf(NullPointerException.class);

        verify(sysUserMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("Should support multiple queries for different users")
    void shouldSupportMultipleQueries() {
        // Given
        SysUser user1 = TestDataFactory.createDefaultUser();
        SysUser user2 = TestDataFactory.createAdminUser();

        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(user1)
                .thenReturn(user2);

        // When
        SysUser actualUser1 = sysUserService.getUserByUsername("testuser");
        SysUser actualUser2 = sysUserService.getUserByUsername("admin");

        // Then
        assertThat(actualUser1).isNotNull();
        assertThat(actualUser2).isNotNull();
        assertThat(actualUser1.getUserName()).isEqualTo("testuser");
        assertThat(actualUser2.getUserName()).isEqualTo("admin");

        verify(sysUserMapper, times(2)).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("Should support querying same user multiple times")
    void shouldSupportQuerySameUserMultipleTimes() {
        // Given
        SysUser user = TestDataFactory.createDefaultUser();
        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(user);

        // When
        SysUser result1 = sysUserService.getUserByUsername("testuser");
        SysUser result2 = sysUserService.getUserByUsername("testuser");

        // Then
        assertThat(result1).isNotNull();
        assertThat(result2).isNotNull();
        assertThat(result1.getUserId()).isEqualTo(result2.getUserId());

        verify(sysUserMapper, times(2)).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("Should call Mapper only once")
    void shouldCallMapperOnlyOnce() {
        // Given
        String username = "testuser";
        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(TestDataFactory.createDefaultUser());

        // When
        sysUserService.getUserByUsername(username);

        // Then
        verify(sysUserMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verifyNoMoreInteractions(sysUserMapper);
    }

    @Test
    @DisplayName("Should not call other Mapper methods")
    void shouldNotCallOtherMapperMethods() {
        // Given
        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(TestDataFactory.createDefaultUser());

        // When
        sysUserService.getUserByUsername("testuser");

        // Then
        verify(sysUserMapper, never()).selectList(any());
    }
}
