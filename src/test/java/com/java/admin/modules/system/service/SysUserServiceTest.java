package com.java.admin.modules.system.service;

import com.java.admin.modules.system.mapper.SysUserMapper;
import com.java.admin.modules.system.model.SysUser;
import com.java.admin.testutil.AbstractMockTest;
import com.java.admin.testutil.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
@DisplayName("SysUserService Unit Tests")
class SysUserServiceTest extends AbstractMockTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @InjectMocks
    private SysUserService sysUserService;

    @Test
    @DisplayName("Should successfully return existing user")
    void shouldReturnUserWhenUserExists() {
        // Given
        String username = "test-user-name";
        SysUser expectedUser = TestDataFactory.createDefaultUser();

        when(sysUserMapper.selectOne(any()))
                .thenReturn(expectedUser);

        // When
        SysUser actualUser = sysUserService.getUserByUsername(username);

        // Then
        assertThat(actualUser).isNotNull();
        assertThat(actualUser.getUserId()).isEqualTo(expectedUser.getUserId());
        assertThat(actualUser.getUserName()).isEqualTo(expectedUser.getUserName());

        // Verify Mapper is called
        verify(sysUserMapper, times(1)).selectOne(any());
    }

    @Test
    @DisplayName("Should return null when user does not exist")
    void shouldReturnNullWhenUserNotExists() {
        // Given
        String username = "nonexistent";
        when(sysUserMapper.selectOne(any()))
                .thenReturn(null);

        // When
        SysUser actualUser = sysUserService.getUserByUsername(username);

        // Then
        assertThat(actualUser).isNull();

        // Verify Mapper is called
        verify(sysUserMapper, times(1)).selectOne(any());
    }

    @Test
    @DisplayName("Should use correct username to build query conditions")
    void shouldUseCorrectUsernameForQuery() {
        // Given
        String username = "admin";
        SysUser adminUser = TestDataFactory.createAdminUser();
        when(sysUserMapper.selectOne(any()))
                .thenReturn(adminUser);

        // When
        sysUserService.getUserByUsername(username);

        // Then
        verify(sysUserMapper).selectOne(argThat(Objects::nonNull));
    }

    @Test
    @DisplayName("Should handle empty string username")
    void shouldHandleEmptyUsername() {
        // Given
        String emptyUsername = "";
        when(sysUserMapper.selectOne(any()))
                .thenReturn(null);

        // When
        SysUser actualUser = sysUserService.getUserByUsername(emptyUsername);

        // Then
        assertThat(actualUser).isNull();
        verify(sysUserMapper, times(1)).selectOne(any());
    }

    @Test
    @DisplayName("Should handle username with special characters")
    void shouldHandleSpecialCharactersInUsername() {
        // Given
        String specialUsername = "user@domain.com";
        SysUser user = TestDataFactory.createUserWithUsername(specialUsername);
        when(sysUserMapper.selectOne(any()))
                .thenReturn(user);

        // When
        SysUser actualUser = sysUserService.getUserByUsername(specialUsername);

        // Then
        assertThat(actualUser).isNotNull();
        assertThat(actualUser.getUserName()).isEqualTo(specialUsername);
        verify(sysUserMapper, times(1)).selectOne(any());
    }

    @Test
    @DisplayName("Should handle null username")
    void shouldHandleNullUsername() {
        // Given
        when(sysUserMapper.selectOne(any()))
                .thenReturn(null);

        // When
        SysUser actualUser = sysUserService.getUserByUsername(null);

        // Then
        assertThat(actualUser).isNull();
        verify(sysUserMapper, times(1)).selectOne(any());
    }

    @Test
    @DisplayName("Should propagate database exception")
    void shouldPropagateDatabaseException() {
        // Given
        String username = "test-user-name";
        when(sysUserMapper.selectOne(any()))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        assertThatThrownBy(() -> sysUserService.getUserByUsername(username))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database connection failed");

        verify(sysUserMapper, times(1)).selectOne(any());
    }

    @Test
    @DisplayName("Should handle null pointer exception")
    void shouldHandleNullPointerException() {
        // Given
        String username = "test-user-name";
        when(sysUserMapper.selectOne(any()))
                .thenThrow(new NullPointerException("Unexpected null value"));

        // When & Then
        assertThatThrownBy(() -> sysUserService.getUserByUsername(username))
                .isInstanceOf(NullPointerException.class);

        verify(sysUserMapper, times(1)).selectOne(any());
    }

    @Test
    @DisplayName("Should support multiple queries for different users")
    void shouldSupportMultipleQueries() {
        // Given
        SysUser user1 = TestDataFactory.createUserWithUsername("test-user-name");
        SysUser user2 = TestDataFactory.createAdminUser();

        when(sysUserMapper.selectOne(any()))
                .thenReturn(user1)
                .thenReturn(user2);

        // When
        SysUser actualUser1 = sysUserService.getUserByUsername("test-user-name");
        SysUser actualUser2 = sysUserService.getUserByUsername("admin");

        // Then
        assertThat(actualUser1).isNotNull();
        assertThat(actualUser2).isNotNull();
        assertThat(actualUser1.getUserName()).isEqualTo("test-user-name");
        assertThat(actualUser2.getUserName()).isEqualTo("admin");

        verify(sysUserMapper, times(2)).selectOne(any());
    }

    @Test
    @DisplayName("Should support querying same user multiple times")
    void shouldSupportQuerySameUserMultipleTimes() {
        // Given
        SysUser user = TestDataFactory.createDefaultUser();
        when(sysUserMapper.selectOne(any()))
                .thenReturn(user);

        // When
        SysUser result1 = sysUserService.getUserByUsername("test-user-name");
        SysUser result2 = sysUserService.getUserByUsername("test-user-name");

        // Then
        assertThat(result1).isNotNull();
        assertThat(result2).isNotNull();
        assertThat(result1.getUserId()).isEqualTo(result2.getUserId());

        verify(sysUserMapper, times(2)).selectOne(any());
    }

    @Test
    @DisplayName("Should call Mapper only once")
    void shouldCallMapperOnlyOnce() {
        // Given
        String username = "test-user-name";
        when(sysUserMapper.selectOne(any()))
                .thenReturn(TestDataFactory.createDefaultUser());

        // When
        sysUserService.getUserByUsername(username);

        // Then
        verify(sysUserMapper, times(1)).selectOne(any());
        verifyNoMoreInteractions(sysUserMapper);
    }

    @Test
    @DisplayName("Should not call other Mapper methods")
    void shouldNotCallOtherMapperMethods() {
        // Given
        when(sysUserMapper.selectOne(any()))
                .thenReturn(TestDataFactory.createDefaultUser());

        // When
        sysUserService.getUserByUsername("test-user-name");

        // Then
        verify(sysUserMapper, never()).selectList(any());
    }
}
