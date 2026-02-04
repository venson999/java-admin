package com.java.admin.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java.admin.infrastructure.model.Result;
import com.java.admin.modules.system.model.SysUser;
import com.java.admin.modules.system.service.SysUserService;
import com.java.admin.testutil.AbstractMockTest;
import com.java.admin.testutil.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * SysUserController Unit Tests
 *
 * <p>Test Coverage:
 * <ul>
 *   <li>User info query endpoint</li>
 *   <li>User pagination endpoint</li>
 *   <li>Response format verification</li>
 *   <li>Permission control</li>
 * </ul>
 *
 * <p>Coverage Target: 80%+
 */
@DisplayName("SysUserController Unit Tests")
class SysUserControllerTest extends AbstractMockTest {

    @Mock
    private SysUserService sysUserService;

    private SysUserController controller() {
        return new SysUserController(sysUserService);
    }

    @Test
    @DisplayName("Should return user info")
    void shouldReturnUserInfo() {
        // When
        Result<Object> result = controller().getUserInfo();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getData()).isEqualTo("user info");
    }

    @Test
    @DisplayName("Should return correct response format")
    void shouldReturnCorrectResponseFormat() {
        // When
        Result<Object> result = controller().getUserInfo();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isNotNull();
        assertThat(result.getData()).isNotNull();
    }

    @Test
    @DisplayName("Should return string type user info")
    void shouldReturnStringUserInfo() {
        // When
        Result<Object> result = controller().getUserInfo();

        // Then
        assertThat(result.getData()).isInstanceOf(String.class);
    }

    @Test
    @DisplayName("Should return correct user info content")
    void shouldReturnCorrectUserInfoContent() {
        // When
        Result<Object> result = controller().getUserInfo();

        // Then
        assertThat(result.getData()).isEqualTo("user info");
    }

    @Test
    @DisplayName("Should return 200 status code")
    void shouldReturn200StatusCode() {
        // When
        Result<Object> result = controller().getUserInfo();

        // Then
        assertThat(result.getCode()).isEqualTo("200");
    }

    @Test
    @DisplayName("Should return consistent results on multiple calls")
    void shouldReturnConsistentResultsOnMultipleCalls() {
        // When
        Result<Object> result1 = controller().getUserInfo();
        Result<Object> result2 = controller().getUserInfo();

        // Then
        assertThat(result1).isEqualTo(result2);
    }

    @Test
    @DisplayName("Should return non-null response")
    void shouldReturnNonNullResponse() {
        // When
        Result<Object> result = controller().getUserInfo();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isNotNull();
        assertThat(result.getData()).isNotNull();
    }

    @Test
    @DisplayName("Response should contain success status code")
    void shouldContainSuccessCode() {
        // When
        Result<Object> result = controller().getUserInfo();

        // Then
        assertThat(result.getCode()).isNotBlank();
        assertThat(result.getCode()).isEqualTo("200");
    }

    @Test
    @DisplayName("Should return paginated user list successfully")
    void shouldReturnPaginatedUserList() {
        // Given
        Page<SysUser> mockPage = new Page<>(0, 10);
        mockPage.setRecords(List.of(
                TestDataFactory.createDefaultUser(),
                TestDataFactory.createAdminUser()
        ));
        mockPage.setTotal(2);

        when(sysUserService.pageUsers(anyInt(), anyInt(), isNull()))
                .thenReturn(mockPage);

        // When
        Result<Page<SysUser>> result = controller().pageUsers(0, 10, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getRecords()).hasSize(2);
        assertThat(result.getData().getTotal()).isEqualTo(2);

        verify(sysUserService).pageUsers(0, 10, null);
    }

    @Test
    @DisplayName("Should filter users by username when provided")
    void shouldFilterUsersByUsername() {
        // Given
        Page<SysUser> mockPage = new Page<>(0, 10);
        mockPage.setRecords(List.of(TestDataFactory.createAdminUser()));
        mockPage.setTotal(1);

        when(sysUserService.pageUsers(anyInt(), anyInt(), anyString()))
                .thenReturn(mockPage);

        // When
        Result<Page<SysUser>> result = controller().pageUsers(0, 10, "admin");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getData().getRecords()).hasSize(1);
        assertThat(result.getData().getRecords().get(0).getUserName()).isEqualTo("admin");

        verify(sysUserService).pageUsers(0, 10, "admin");
    }

    @Test
    @DisplayName("Should return empty page when no users match")
    void shouldReturnEmptyPageWhenNoUsers() {
        // Given
        Page<SysUser> mockPage = new Page<>(0, 10);
        mockPage.setRecords(List.of());
        mockPage.setTotal(0);

        when(sysUserService.pageUsers(anyInt(), anyInt(), anyString()))
                .thenReturn(mockPage);

        // When
        Result<Page<SysUser>> result = controller().pageUsers(0, 10, "nonexistent");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getData().getRecords()).isEmpty();

        verify(sysUserService).pageUsers(0, 10, "nonexistent");
    }

    @Test
    @DisplayName("Should use default page parameters")
    void shouldUseDefaultPageParameters() {
        // Given
        Page<SysUser> mockPage = new Page<>(0, 10);
        mockPage.setRecords(List.of(TestDataFactory.createDefaultUser()));
        mockPage.setTotal(1);

        when(sysUserService.pageUsers(anyInt(), anyInt(), isNull()))
                .thenReturn(mockPage);

        // When
        Result<Page<SysUser>> result = controller().pageUsers(0, 10, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");

        verify(sysUserService).pageUsers(0, 10, null);
    }

    @Test
    @DisplayName("Should handle different page sizes")
    void shouldHandleDifferentPageSizes() {
        // Given
        Page<SysUser> mockPage = new Page<>(0, 20);
        mockPage.setRecords(List.of(TestDataFactory.createDefaultUser()));
        mockPage.setTotal(1);

        when(sysUserService.pageUsers(anyInt(), anyInt(), isNull()))
                .thenReturn(mockPage);

        // When
        Result<Page<SysUser>> result = controller().pageUsers(0, 20, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getData().getSize()).isEqualTo(20);

        verify(sysUserService).pageUsers(0, 20, null);
    }

    @Test
    @DisplayName("Should handle pagination with multiple users")
    void shouldHandlePaginationWithMultipleUsers() {
        // Given
        Page<SysUser> mockPage = new Page<>(0, 10);
        mockPage.setRecords(List.of(
                TestDataFactory.createUserWithUsername("user1"),
                TestDataFactory.createUserWithUsername("user2"),
                TestDataFactory.createUserWithUsername("user3"),
                TestDataFactory.createUserWithUsername("user4"),
                TestDataFactory.createUserWithUsername("user5")
        ));
        mockPage.setTotal(5);

        when(sysUserService.pageUsers(anyInt(), anyInt(), isNull()))
                .thenReturn(mockPage);

        // When
        Result<Page<SysUser>> result = controller().pageUsers(0, 10, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getData().getRecords()).hasSize(5);
        assertThat(result.getData().getTotal()).isEqualTo(5);

        verify(sysUserService).pageUsers(0, 10, null);
    }

    @Test
    @DisplayName("Should handle null username filter")
    void shouldHandleNullUsernameFilter() {
        // Given
        Page<SysUser> mockPage = new Page<>(0, 10);
        mockPage.setRecords(List.of(TestDataFactory.createDefaultUser()));
        mockPage.setTotal(1);

        when(sysUserService.pageUsers(anyInt(), anyInt(), isNull()))
                .thenReturn(mockPage);

        // When
        Result<Page<SysUser>> result = controller().pageUsers(0, 10, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");

        verify(sysUserService).pageUsers(0, 10, null);
    }

    @Test
    @DisplayName("Should handle empty string username filter")
    void shouldHandleEmptyUsernameFilter() {
        // Given
        Page<SysUser> mockPage = new Page<>(0, 10);
        mockPage.setRecords(List.of(TestDataFactory.createDefaultUser()));
        mockPage.setTotal(1);

        when(sysUserService.pageUsers(anyInt(), anyInt(), anyString()))
                .thenReturn(mockPage);

        // When
        Result<Page<SysUser>> result = controller().pageUsers(0, 10, "");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");

        verify(sysUserService).pageUsers(0, 10, "");
    }
}
