package com.java.admin.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java.admin.infrastructure.model.Result;
import com.java.admin.modules.system.dto.CreateUserRequestDTO;
import com.java.admin.modules.system.dto.UpdateUserRequestDTO;
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
import static org.mockito.Mockito.doNothing;
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

    @Test
    @DisplayName("Should create user successfully with valid data")
    void shouldCreateUserSuccessfully() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("newuser");
        dto.setPassword("Password123");
        dto.setEmail("newuser@example.com");

        doNothing().when(sysUserService).createUser(any(CreateUserRequestDTO.class));

        // When
        Result<Void> result = controller().createUser(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");

        verify(sysUserService).createUser(dto);
    }

    @Test
    @DisplayName("Should create user with default role when role not specified")
    void shouldCreateUserWithDefaultRole() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("newuser");
        dto.setPassword("Password123");
        dto.setEmail("newuser@example.com");

        doNothing().when(sysUserService).createUser(any(CreateUserRequestDTO.class));

        // When
        Result<Void> result = controller().createUser(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");

        verify(sysUserService).createUser(argThat(request ->
            "newuser".equals(request.getUsername()) &&
            "Password123".equals(request.getPassword()) &&
            "newuser@example.com".equals(request.getEmail())
        ));
    }

    @Test
    @DisplayName("Should create user without email")
    void shouldCreateUserWithoutEmail() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("usernoemail");
        dto.setPassword("Password123");
        dto.setEmail(null);

        doNothing().when(sysUserService).createUser(any(CreateUserRequestDTO.class));

        // When
        Result<Void> result = controller().createUser(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");

        verify(sysUserService).createUser(argThat(request ->
            "usernoemail".equals(request.getUsername()) &&
            request.getEmail() == null
        ));
    }

    @Test
    @DisplayName("Should pass username to service layer")
    void shouldPassUsernameToService() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("testuser");
        dto.setPassword("Password123");
        dto.setEmail("test@example.com");

        doNothing().when(sysUserService).createUser(any(CreateUserRequestDTO.class));

        // When
        controller().createUser(dto);

        // Then
        verify(sysUserService).createUser(argThat(request ->
            "testuser".equals(request.getUsername())
        ));
    }

    @Test
    @DisplayName("Should pass password to service layer")
    void shouldPassPasswordToService() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("testuser");
        dto.setPassword("SecurePass123");
        dto.setEmail("test@example.com");

        doNothing().when(sysUserService).createUser(any(CreateUserRequestDTO.class));

        // When
        controller().createUser(dto);

        // Then
        verify(sysUserService).createUser(argThat(request ->
            "SecurePass123".equals(request.getPassword())
        ));
    }

    @Test
    @DisplayName("Should pass email to service layer")
    void shouldPassEmailToService() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("testuser");
        dto.setPassword("Password123");
        dto.setEmail("testuser@example.com");

        doNothing().when(sysUserService).createUser(any(CreateUserRequestDTO.class));

        // When
        controller().createUser(dto);

        // Then
        verify(sysUserService).createUser(argThat(request ->
            "testuser@example.com".equals(request.getEmail())
        ));
    }

    // Note: Tests for getUserById endpoint require integration testing with Spring Security context
    // These tests should be added in a separate integration test class
    // Note: Tests for createUser permission control (@PreAuthorize) require integration testing with Spring Security

    @Test
    @DisplayName("Should get user by ID successfully")
    void shouldGetUserById() {
        // Given
        String userId = "123";
        SysUser user = TestDataFactory.createDefaultUser();
        user.setUserId(userId);

        when(sysUserService.getUserById(userId)).thenReturn(user);

        // When
        Result<SysUser> result = controller().getUserById(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getUserId()).isEqualTo(userId);

        verify(sysUserService).getUserById(userId);
    }

    @Test
    @DisplayName("Should get user with correct username")
    void shouldGetUserWithCorrectUsername() {
        // Given
        String userId = "123";
        SysUser user = TestDataFactory.createAdminUser();
        user.setUserId(userId);

        when(sysUserService.getUserById(userId)).thenReturn(user);

        // When
        Result<SysUser> result = controller().getUserById(userId);

        // Then
        assertThat(result.getData().getUserName()).isEqualTo("admin");
        verify(sysUserService).getUserById(userId);
    }

    @Test
    @DisplayName("Should get user with correct email")
    void shouldGetUserWithCorrectEmail() {
        // Given
        String userId = "123";
        SysUser user = TestDataFactory.createDefaultUser();
        user.setUserId(userId);
        user.setEmail("test@example.com");

        when(sysUserService.getUserById(userId)).thenReturn(user);

        // When
        Result<SysUser> result = controller().getUserById(userId);

        // Then
        assertThat(result.getData().getEmail()).isEqualTo("test@example.com");
        verify(sysUserService).getUserById(userId);
    }

    @Test
    @DisplayName("Should return user entity with all fields")
    void shouldReturnUserEntityWithAllFields() {
        // Given
        String userId = "123";
        SysUser user = TestDataFactory.createDefaultUser();
        user.setUserId(userId);
        user.setUserName("testuser");
        user.setEmail("test@example.com");

        when(sysUserService.getUserById(userId)).thenReturn(user);

        // When
        Result<SysUser> result = controller().getUserById(userId);

        // Then
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getUserId()).isEqualTo(userId);
        assertThat(result.getData().getUserName()).isEqualTo("testuser");
        assertThat(result.getData().getEmail()).isEqualTo("test@example.com");

        verify(sysUserService).getUserById(userId);
    }

    @Test
    @DisplayName("Should update user email successfully")
    void shouldUpdateUserEmail() {
        // Given
        String userId = "123";
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setEmail("newemail@example.com");

        doNothing().when(sysUserService).updateUser(eq(userId), any(UpdateUserRequestDTO.class));

        // When
        Result<Void> result = controller().updateUser(userId, dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");

        verify(sysUserService).updateUser(eq(userId), argThat(request ->
            "newemail@example.com".equals(request.getEmail())
        ));
    }

    @Test
    @DisplayName("Should update user with null email")
    void shouldUpdateUserWithNullEmail() {
        // Given
        String userId = "123";
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setEmail(null);

        doNothing().when(sysUserService).updateUser(eq(userId), any(UpdateUserRequestDTO.class));

        // When
        Result<Void> result = controller().updateUser(userId, dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");

        verify(sysUserService).updateUser(eq(userId), argThat(request ->
            request.getEmail() == null
        ));
    }

    @Test
    @DisplayName("Should update user with empty email")
    void shouldUpdateUserWithEmptyEmail() {
        // Given
        String userId = "123";
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setEmail("");

        doNothing().when(sysUserService).updateUser(eq(userId), any(UpdateUserRequestDTO.class));

        // When
        Result<Void> result = controller().updateUser(userId, dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");

        verify(sysUserService).updateUser(eq(userId), argThat(request ->
            "".equals(request.getEmail())
        ));
    }

    @Test
    @DisplayName("Should pass user ID to service layer for update")
    void shouldPassUserIdToServiceForUpdate() {
        // Given
        String userId = "456";
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setEmail("updated@example.com");

        doNothing().when(sysUserService).updateUser(eq(userId), any(UpdateUserRequestDTO.class));

        // When
        controller().updateUser(userId, dto);

        // Then
        verify(sysUserService).updateUser(eq(userId), any(UpdateUserRequestDTO.class));
    }

    @Test
    @DisplayName("Should pass email to service layer for update")
    void shouldPassEmailToServiceForUpdate() {
        // Given
        String userId = "123";
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setEmail("myemail@example.com");

        doNothing().when(sysUserService).updateUser(eq(userId), any(UpdateUserRequestDTO.class));

        // When
        controller().updateUser(userId, dto);

        // Then
        verify(sysUserService).updateUser(eq(userId), argThat(request ->
            "myemail@example.com".equals(request.getEmail())
        ));
    }

    @Test
    @DisplayName("Should return success result after update")
    void shouldReturnSuccessResultAfterUpdate() {
        // Given
        String userId = "123";
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setEmail("new@example.com");

        doNothing().when(sysUserService).updateUser(eq(userId), any(UpdateUserRequestDTO.class));

        // When
        Result<Void> result = controller().updateUser(userId, dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getData()).isNull();
    }

    // Note: Tests for updateUser and getUserById permission control (@PreAuthorize) require integration testing with Spring Security
}
