package com.java.admin.modules.system.controller;

import com.java.admin.infrastructure.constants.ErrorCode;
import com.java.admin.infrastructure.model.Result;
import com.java.admin.infrastructure.model.SecurityUserDetails;
import com.java.admin.modules.system.model.LoginUser;
import com.java.admin.modules.system.service.SysLoginService;
import com.java.admin.testutil.AbstractMockTest;
import com.java.admin.testutil.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * SysLoginController Unit Tests
 *
 * <p>Test Coverage:
 * <ul>
 *   <li>Login endpoint (success/failure)</li>
 *   <li>Logout endpoint</li>
 *   <li>Revoke endpoint (admin only)</li>
 *   <li>Parameter validation</li>
 *   <li>Response format verification</li>
 * </ul>
 *
 * <p>Coverage Target: 90%+
 */
@DisplayName("SysLoginController Unit Tests")
class SysLoginControllerTest extends AbstractMockTest {

    @Mock
    private SysLoginService sysLoginService;

    @InjectMocks
    private SysLoginController controller;

    @Test
    @DisplayName("Should successfully login and return token")
    void shouldReturnTokenWhenLoginSuccessful() {
        // Given
        LoginUser loginUser = new LoginUser();
        loginUser.setUsername("test-user-name");
        loginUser.setPassword("password");

        when(sysLoginService.login("test-user-name", "password"))
                .thenReturn("valid-jwt-token");

        // When
        Result<String> result = controller.login(loginUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getData()).isEqualTo("valid-jwt-token");

        verify(sysLoginService, times(1)).login("test-user-name", "password");
    }

    @Test
    @DisplayName("Should return authentication error when login fails")
    void shouldReturnAuthenticationErrorWhenLoginFails() {
        // Given
        LoginUser loginUser = new LoginUser();
        loginUser.setUsername("test-user-name");
        loginUser.setPassword("wrongpassword");

        when(sysLoginService.login("test-user-name", "wrongpassword"))
                .thenReturn(null);

        // When
        Result<String> result = controller.login(loginUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo(ErrorCode.AUTHENTICATION_ERROR.getCode());
        assertThat(result.getMsg()).isEqualTo(ErrorCode.AUTHENTICATION_ERROR.getMessage());

        verify(sysLoginService, times(1)).login("test-user-name", "wrongpassword");
    }

    @Test
    @DisplayName("Should handle login request with missing username")
    void shouldHandleMissingUsername() {
        // Given
        LoginUser loginUser = new LoginUser();
        loginUser.setPassword("password");

        when(sysLoginService.login(null, "password")).thenReturn(null);

        // When
        Result<String> result = controller.login(loginUser);

        // Then
        assertThat(result.getCode()).isEqualTo(ErrorCode.AUTHENTICATION_ERROR.getCode());

        verify(sysLoginService, times(1)).login(null, "password");
    }

    @Test
    @DisplayName("Should handle login request with missing password")
    void shouldHandleMissingPassword() {
        // Given
        LoginUser loginUser = new LoginUser();
        loginUser.setUsername("test-user-name");

        when(sysLoginService.login("test-user-name", null)).thenReturn(null);

        // When
        Result<String> result = controller.login(loginUser);

        // Then
        assertThat(result.getCode()).isEqualTo(ErrorCode.AUTHENTICATION_ERROR.getCode());

        verify(sysLoginService, times(1)).login("test-user-name", null);
    }

    @Test
    @DisplayName("Should handle login request with empty username")
    void shouldHandleEmptyUsername() {
        // Given
        LoginUser loginUser = new LoginUser();
        loginUser.setUsername("");
        loginUser.setPassword("password");

        when(sysLoginService.login("", "password")).thenReturn(null);

        // When
        Result<String> result = controller.login(loginUser);

        // Then
        assertThat(result.getCode()).isEqualTo(ErrorCode.AUTHENTICATION_ERROR.getCode());
    }

    @Test
    @DisplayName("Should handle login request with empty password")
    void shouldHandleEmptyPassword() {
        // Given
        LoginUser loginUser = new LoginUser();
        loginUser.setUsername("test-user-name");
        loginUser.setPassword("");

        when(sysLoginService.login("test-user-name", "")).thenReturn(null);

        // When
        Result<String> result = controller.login(loginUser);

        // Then
        assertThat(result.getCode()).isEqualTo(ErrorCode.AUTHENTICATION_ERROR.getCode());
    }

    @Test
    @DisplayName("Should handle service exception during login")
    void shouldHandleServiceExceptionDuringLogin() {
        // Given
        LoginUser loginUser = new LoginUser();
        loginUser.setUsername("test-user-name");
        loginUser.setPassword("password");

        when(sysLoginService.login("test-user-name", "password"))
                .thenThrow(new RuntimeException("Service unavailable"));

        // When & Then
        assertThatThrownBy(() -> controller.login(loginUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Service unavailable");
    }

    @Test
    @DisplayName("Should handle very long username")
    void shouldHandleVeryLongUsername() {
        // Given
        LoginUser loginUser = new LoginUser();
        String longUsername = "a".repeat(1000);
        loginUser.setUsername(longUsername);
        loginUser.setPassword("password");

        when(sysLoginService.login(longUsername, "password"))
                .thenReturn(null);

        // When
        Result<String> result = controller.login(loginUser);

        // Then
        assertThat(result.getCode()).isEqualTo(ErrorCode.AUTHENTICATION_ERROR.getCode());
    }

    @Test
    @DisplayName("Should handle username with spaces")
    void shouldHandleUsernameWithSpaces() {
        // Given
        LoginUser loginUser = new LoginUser();
        loginUser.setUsername(" test user ");
        loginUser.setPassword("password");

        when(sysLoginService.login(" test user ", "password"))
                .thenReturn(null);

        // When
        Result<String> result = controller.login(loginUser);

        // Then
        assertThat(result.getCode()).isEqualTo(ErrorCode.AUTHENTICATION_ERROR.getCode());
    }

    @Test
    @DisplayName("Should return correct response format")
    void shouldReturnCorrectResponseFormat() {
        // Given
        LoginUser loginUser = new LoginUser();
        loginUser.setUsername("test-user-name");
        loginUser.setPassword("password");

        when(sysLoginService.login("test-user-name", "password"))
                .thenReturn("jwt-token");

        // When
        Result<String> result = controller.login(loginUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isNotNull();
        assertThat(result.getData()).isNotNull();
    }

    @Test
    @DisplayName("Should return token data on successful login")
    void shouldReturnTokenDataOnSuccessfulLogin() {
        // Given
        LoginUser loginUser = new LoginUser();
        loginUser.setUsername("test-user-name");
        loginUser.setPassword("password");

        when(sysLoginService.login("test-user-name", "password"))
                .thenReturn("test-token-12345");

        // When
        Result<String> result = controller.login(loginUser);

        // Then
        assertThat(result.getData()).isEqualTo("test-token-12345");
        assertThat(result.getCode()).isEqualTo("200");
    }

    // ==================== Logout Tests ====================

    @Test
    @DisplayName("Should successfully logout user")
    void shouldLogoutUserSuccessfully() {
        // Given
        String userId = "test-user-id";
        SecurityUserDetails userDetails = TestDataFactory.createSecurityUserDetails(userId);
        Authentication authentication = new TestingAuthenticationToken(userDetails, null);

        // When
        Result<String> result = controller.logout(authentication);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getData()).isNull();

        verify(sysLoginService, times(1)).revoke(userId);
    }

    @Test
    @DisplayName("Should call revoke service when logout")
    void shouldCallRevokeServiceWhenLogout() {
        // Given
        String userId = "test-user-id";
        SecurityUserDetails userDetails = TestDataFactory.createSecurityUserDetails(userId);
        Authentication authentication = new TestingAuthenticationToken(userDetails, null);

        // When
        controller.logout(authentication);

        // Then
        verify(sysLoginService, times(1)).revoke(userId);
    }

    @Test
    @DisplayName("Should logout with correct user ID from authentication")
    void shouldLogoutWithCorrectUserId() {
        // Given
        String userId = "custom-user-123";
        SecurityUserDetails userDetails = TestDataFactory.createSecurityUserDetails(userId);
        Authentication authentication = new TestingAuthenticationToken(userDetails, null);

        // When
        controller.logout(authentication);

        // Then
        verify(sysLoginService).revoke(userId);
    }

    @Test
    @DisplayName("Should return success response on logout")
    void shouldReturnSuccessResponseOnLogout() {
        // Given
        SecurityUserDetails userDetails = TestDataFactory.createDefaultSecurityUserDetails();
        Authentication authentication = new TestingAuthenticationToken(userDetails, null);

        // When
        Result<String> result = controller.logout(authentication);

        // Then
        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getMsg()).isNull();
    }

    @Test
    @DisplayName("Should handle logout for admin user")
    void shouldHandleLogoutForAdminUser() {
        // Given
        String adminId = "admin-user-id";
        SecurityUserDetails adminDetails = TestDataFactory.createSecurityUserDetailsWithAuthorities(
                adminId, List.of("ROLE_ADMIN", "admin"));
        Authentication authentication = new TestingAuthenticationToken(adminDetails, null);

        // When
        Result<String> result = controller.logout(authentication);

        // Then
        assertThat(result.getCode()).isEqualTo("200");
        verify(sysLoginService, times(1)).revoke(adminId);
    }

    @Test
    @DisplayName("Should handle logout for regular user")
    void shouldHandleLogoutForRegularUser() {
        // Given
        SecurityUserDetails userDetails = TestDataFactory.createDefaultSecurityUserDetails();
        Authentication authentication = new TestingAuthenticationToken(userDetails, null);

        // When
        Result<String> result = controller.logout(authentication);

        // Then
        assertThat(result.getCode()).isEqualTo("200");
    }

    // ==================== Revoke Tests ====================

    @Test
    @DisplayName("Should successfully revoke user session")
    void shouldRevokeUserSessionSuccessfully() {
        // Given
        String userId = "test-user-id";

        // When
        Result<String> result = controller.revoke(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");

        verify(sysLoginService, times(1)).revoke(userId);
    }

    @Test
    @DisplayName("Should call revoke service with correct user ID")
    void shouldCallRevokeServiceWithCorrectUserId() {
        // Given
        String userId = "user-to-revoke-123";

        // When
        controller.revoke(userId);

        // Then
        verify(sysLoginService, times(1)).revoke(userId);
    }

    @Test
    @DisplayName("Should return success response when revoking user")
    void shouldReturnSuccessResponseWhenRevokingUser() {
        // Given
        String userId = "test-user-id";

        // When
        Result<String> result = controller.revoke(userId);

        // Then
        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getMsg()).isNull();
    }

    @Test
    @DisplayName("Should handle revoking multiple users")
    void shouldHandleRevokingMultipleUsers() {
        // Given
        String userId1 = "user-1";
        String userId2 = "user-2";

        // When
        controller.revoke(userId1);
        controller.revoke(userId2);

        // Then
        verify(sysLoginService, times(1)).revoke(userId1);
        verify(sysLoginService, times(1)).revoke(userId2);
    }

    @Test
    @DisplayName("Should handle revoking same user multiple times")
    void shouldHandleRevokingSameUserMultipleTimes() {
        // Given
        String userId = "test-user-id";

        // When
        controller.revoke(userId);
        controller.revoke(userId);
        controller.revoke(userId);

        // Then
        verify(sysLoginService, times(3)).revoke(userId);
    }

    @Test
    @DisplayName("Should handle empty user ID for revoke")
    void shouldHandleEmptyUserIdForRevoke() {
        // Given
        String emptyUserId = "";

        // When
        Result<String> result = controller.revoke(emptyUserId);

        // Then
        assertThat(result.getCode()).isEqualTo("200");
        verify(sysLoginService, times(1)).revoke(emptyUserId);
    }

    @Test
    @DisplayName("Should handle service exception during revoke")
    void shouldHandleServiceExceptionDuringRevoke() {
        // Given
        String userId = "test-user-id";
        doThrow(new RuntimeException("Database error")).when(sysLoginService).revoke(userId);

        // When & Then
        assertThatThrownBy(() -> controller.revoke(userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");
    }

    @Test
    @DisplayName("Should propagate exception from service during logout")
    void shouldPropagateExceptionFromServiceDuringLogout() {
        // Given
        String userId = "test-user-id";
        SecurityUserDetails userDetails = TestDataFactory.createSecurityUserDetails(userId);
        Authentication authentication = new TestingAuthenticationToken(userDetails, null);

        doThrow(new RuntimeException("Service unavailable")).when(sysLoginService).revoke(userId);

        // When & Then
        assertThatThrownBy(() -> controller.logout(authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Service unavailable");
    }
}
