package com.java.admin.modules.system.controller;

import com.java.admin.infrastructure.constants.ErrorCode;
import com.java.admin.infrastructure.model.Result;
import com.java.admin.modules.system.model.LoginUser;
import com.java.admin.modules.system.service.SysLoginService;
import com.java.admin.testutil.AbstractUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * SysLoginController Unit Tests
 *
 * <p>Test Coverage:
 * <ul>
 *   <li>Login endpoint (success/failure)</li>
 *   <li>Parameter validation</li>
 *   <li>Response format verification</li>
 * </ul>
 *
 * <p>Coverage Target: 80%+
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysLoginController Unit Tests")
class SysLoginControllerTest extends AbstractUnitTest {

    @Mock
    private SysLoginService sysLoginService;

    @InjectMocks
    private SysLoginController controller;

    @Test
    @DisplayName("Should successfully login and return token")
    void shouldReturnTokenWhenLoginSuccessful() {
        // Given
        LoginUser loginUser = new LoginUser();
        loginUser.setUsername("testuser");
        loginUser.setPassword("password");

        when(sysLoginService.login("testuser", "password"))
                .thenReturn("valid-jwt-token");

        // When
        Result<String> result = controller.login(loginUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getData()).isEqualTo("valid-jwt-token");

        verify(sysLoginService, times(1)).login("testuser", "password");
    }

    @Test
    @DisplayName("Should return authentication error when login fails")
    void shouldReturnAuthenticationErrorWhenLoginFails() {
        // Given
        LoginUser loginUser = new LoginUser();
        loginUser.setUsername("testuser");
        loginUser.setPassword("wrongpassword");

        when(sysLoginService.login("testuser", "wrongpassword"))
                .thenReturn(null);

        // When
        Result<String> result = controller.login(loginUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo(ErrorCode.AUTHENTICATION_ERROR.getCode());
        assertThat(result.getMsg()).isEqualTo(ErrorCode.AUTHENTICATION_ERROR.getMessage());

        verify(sysLoginService, times(1)).login("testuser", "wrongpassword");
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
        loginUser.setUsername("testuser");

        when(sysLoginService.login("testuser", null)).thenReturn(null);

        // When
        Result<String> result = controller.login(loginUser);

        // Then
        assertThat(result.getCode()).isEqualTo(ErrorCode.AUTHENTICATION_ERROR.getCode());

        verify(sysLoginService, times(1)).login("testuser", null);
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
        loginUser.setUsername("testuser");
        loginUser.setPassword("");

        when(sysLoginService.login("testuser", "")).thenReturn(null);

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
        loginUser.setUsername("testuser");
        loginUser.setPassword("password");

        when(sysLoginService.login("testuser", "password"))
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
        loginUser.setUsername("testuser");
        loginUser.setPassword("password");

        when(sysLoginService.login("testuser", "password"))
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
        loginUser.setUsername("testuser");
        loginUser.setPassword("password");

        when(sysLoginService.login("testuser", "password"))
                .thenReturn("test-token-12345");

        // When
        Result<String> result = controller.login(loginUser);

        // Then
        assertThat(result.getData()).isEqualTo("test-token-12345");
        assertThat(result.getCode()).isEqualTo("200");
    }
}
