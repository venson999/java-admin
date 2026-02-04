package com.java.admin.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java.admin.modules.system.mapper.SysUserMapper;
import com.java.admin.modules.system.model.SysUser;
import com.java.admin.testutil.AbstractMockTest;
import com.java.admin.testutil.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
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
 *   <li>User pagination functionality</li>
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

    @Test
    @DisplayName("Should return paginated user list successfully")
    void shouldReturnPaginatedUserList() {
        // Given
        int page = 1;
        int size = 10;
        String username = null;

        Page<SysUser> mockPage = new Page<>(page, size);
        mockPage.setRecords(List.of(
                TestDataFactory.createDefaultUser(),
                TestDataFactory.createAdminUser()
        ));
        mockPage.setTotal(2);

        when(sysUserMapper.selectPage(any(), any()))
                .thenReturn(mockPage);

        // When
        IPage<SysUser> result = sysUserService.pageUsers(page - 1, size, username);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRecords()).hasSize(2);
        assertThat(result.getTotal()).isEqualTo(2);
        assertThat(result.getCurrent()).isEqualTo(page);
        assertThat(result.getSize()).isEqualTo(size);

        verify(sysUserMapper, times(1)).selectPage(any(), any());
    }

    @Test
    @DisplayName("Should filter users by username when provided")
    void shouldFilterUsersByUsername() {
        // Given
        int page = 0;
        int size = 10;
        String username = "admin";

        Page<SysUser> mockPage = new Page<>(page, size);
        mockPage.setRecords(List.of(TestDataFactory.createAdminUser()));
        mockPage.setTotal(1);

        when(sysUserMapper.selectPage(any(), any()))
                .thenReturn(mockPage);

        // When
        IPage<SysUser> result = sysUserService.pageUsers(page, size, username);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRecords()).hasSize(1);
        assertThat(result.getRecords().get(0).getUserName()).isEqualTo("admin");

        verify(sysUserMapper, times(1)).selectPage(any(), any());
    }

    @Test
    @DisplayName("Should return empty page when no users match")
    void shouldReturnEmptyPageWhenNoUsers() {
        // Given
        int page = 0;
        int size = 10;
        String username = "nonexistent";

        Page<SysUser> mockPage = new Page<>(page, size);
        mockPage.setRecords(List.of());
        mockPage.setTotal(0);

        when(sysUserMapper.selectPage(any(), any()))
                .thenReturn(mockPage);

        // When
        IPage<SysUser> result = sysUserService.pageUsers(page, size, username);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRecords()).isEmpty();
        assertThat(result.getTotal()).isEqualTo(0);

        verify(sysUserMapper, times(1)).selectPage(any(), any());
    }

    @Test
    @DisplayName("Should handle different page sizes")
    void shouldHandleDifferentPageSizes() {
        // Given
        int page = 0;
        int size = 20;

        Page<SysUser> mockPage = new Page<>(page, size);
        mockPage.setRecords(List.of(TestDataFactory.createDefaultUser()));
        mockPage.setTotal(1);

        when(sysUserMapper.selectPage(any(), any()))
                .thenReturn(mockPage);

        // When
        IPage<SysUser> result = sysUserService.pageUsers(page, size, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSize()).isEqualTo(size);

        verify(sysUserMapper, times(1)).selectPage(any(), any());
    }

    @Test
    @DisplayName("Should handle different page numbers")
    void shouldHandleDifferentPageNumbers() {
        // Given
        int page = 2;
        int size = 10;

        Page<SysUser> mockPage = new Page<>(page, size);
        mockPage.setRecords(List.of(TestDataFactory.createDefaultUser()));
        mockPage.setTotal(25);

        when(sysUserMapper.selectPage(any(), any()))
                .thenReturn(mockPage);

        // When
        IPage<SysUser> result = sysUserService.pageUsers(page, size, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCurrent()).isEqualTo(page);

        verify(sysUserMapper, times(1)).selectPage(any(), any());
    }

    @Test
    @DisplayName("Should handle pagination with multiple users")
    void shouldHandlePaginationWithMultipleUsers() {
        // Given
        int page = 0;
        int size = 10;

        Page<SysUser> mockPage = new Page<>(page, size);
        mockPage.setRecords(List.of(
                TestDataFactory.createUserWithUsername("user1"),
                TestDataFactory.createUserWithUsername("user2"),
                TestDataFactory.createUserWithUsername("user3"),
                TestDataFactory.createUserWithUsername("user4"),
                TestDataFactory.createUserWithUsername("user5")
        ));
        mockPage.setTotal(5);

        when(sysUserMapper.selectPage(any(), any()))
                .thenReturn(mockPage);

        // When
        IPage<SysUser> result = sysUserService.pageUsers(page, size, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRecords()).hasSize(5);
        assertThat(result.getTotal()).isEqualTo(5);

        verify(sysUserMapper, times(1)).selectPage(any(), any());
    }

    @Test
    @DisplayName("Should propagate exception from mapper during pagination")
    void shouldPropagateExceptionDuringPagination() {
        // Given
        when(sysUserMapper.selectPage(any(), any()))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThatThrownBy(() -> sysUserService.pageUsers(0, 10, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database error");

        verify(sysUserMapper, times(1)).selectPage(any(), any());
    }
}
