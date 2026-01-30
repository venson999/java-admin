package com.java.admin.modules.system.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.java.admin.config.AuthProperties;
import com.java.admin.infrastructure.model.SecurityUserDetails;
import com.java.admin.infrastructure.util.JwtUtil;
import com.java.admin.modules.system.mapper.SessionMapper;
import com.java.admin.testutil.AbstractMockTest;
import com.java.admin.testutil.InMemoryAppender;
import com.java.admin.testutil.TestDataFactory;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * SysLoginService Unit Tests
 *
 * <p>Test Coverage:
 * <ul>
 *   <li>Login functionality (login)</li>
 *   <li>Session revocation (revoke)</li>
 *   <li>Authentication success and failure scenarios</li>
 * </ul>
 *
 * <p>Coverage Target: 90%+
 */
@DisplayName("SysLoginService Unit Tests")
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class SysLoginServiceTest extends AbstractMockTest {

    private static final String TEST_TOKEN = "test-jwt-token";
    private static final String TEST_TOKEN_FINGERPRINT = "test-fingerprint-123";
    private static final long ACCESS_EXPIRE_MILLIS = 3600000L; // 1 hour
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private SessionMapper sessionMapper;
    @Mock
    private AuthProperties authProperties;
    @Mock
    private Authentication authentication;
    @Mock
    private Claims claims;
    @InjectMocks
    private SysLoginService sysLoginService;
    private MockedStatic<JwtUtil> mockedJwtUtil;
    private SecurityUserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        // Mock JwtUtil static methods
        mockedJwtUtil = mockStatic(JwtUtil.class);

        // Setup test data
        testUserDetails = TestDataFactory.createDefaultSecurityUserDetails();

        // Configure default mock behaviors
        when(authProperties.getAccessExpireMillis()).thenReturn(ACCESS_EXPIRE_MILLIS);
        when(claims.getId()).thenReturn(TEST_TOKEN_FINGERPRINT);
    }

    @AfterEach
    void tearDown() {
        // Close static Mock
        if (mockedJwtUtil != null) {
            mockedJwtUtil.close();
        }
    }

    @Test
    @DisplayName("Should successfully login and return token")
    void shouldReturnTokenWhenLoginSuccessful() {
        // Given
        String username = "test-user-name";
        String password = "password";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(testUserDetails);

        // Mock JwtUtil static methods
        mockedJwtUtil.when(() -> JwtUtil.createToken("test-user-id", ACCESS_EXPIRE_MILLIS))
                .thenReturn(TEST_TOKEN);
        mockedJwtUtil.when(() -> JwtUtil.parseClaims(TEST_TOKEN))
                .thenReturn(claims);

        // When
        String token = sysLoginService.login(username, password);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isEqualTo(TEST_TOKEN);

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(sessionMapper, times(1)).save(any(SecurityUserDetails.class));
    }

    @Test
    @DisplayName("Should return null when authentication fails")
    void shouldReturnNullWhenAuthenticationFails() {
        // Given
        String username = "wronguser";
        String password = "wrongpass";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // When
        String token = sysLoginService.login(username, password);

        // Then
        assertThat(token).isNull();

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(sessionMapper, never()).save(any());
    }

    @Test
    @DisplayName("Should return null when authentication exception is thrown")
    void shouldReturnNullWhenAuthenticationExceptionThrown() {
        // Given
        String username = "test-user-name";
        String password = "wrongpass";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Invalid credentials"));

        // When & Then
        assertThatThrownBy(() -> sysLoginService.login(username, password))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class)
                .hasMessage("Invalid credentials");

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(sessionMapper, never()).save(any());
    }

    @Test
    @DisplayName("Should correctly set token fingerprint")
    void shouldSetTokenFingerprintCorrectly() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(testUserDetails);

        mockedJwtUtil.when(() -> JwtUtil.createToken("test-user-id", ACCESS_EXPIRE_MILLIS))
                .thenReturn(TEST_TOKEN);
        mockedJwtUtil.when(() -> JwtUtil.parseClaims(TEST_TOKEN))
                .thenReturn(claims);

        // When
        sysLoginService.login("test-user-name", "password");

        // Then
        assertThat(testUserDetails.getCurrentTokenFingerprint()).isEqualTo(TEST_TOKEN_FINGERPRINT);
    }

    @Test
    @DisplayName("Should save user session")
    void shouldSaveUserSession() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(testUserDetails);

        mockedJwtUtil.when(() -> JwtUtil.createToken("test-user-id", ACCESS_EXPIRE_MILLIS))
                .thenReturn(TEST_TOKEN);
        mockedJwtUtil.when(() -> JwtUtil.parseClaims(TEST_TOKEN))
                .thenReturn(claims);

        // When
        sysLoginService.login("test-user-name", "password");

        // Then
        verify(sessionMapper, times(1)).save(testUserDetails);
    }

    @Test
    @DisplayName("Should successfully revoke user session")
    void shouldRevokeUserSessionSuccessfully() {
        // Given
        String userId = "test-user-id";

        // When
        sysLoginService.revoke(userId);

        // Then
        verify(sessionMapper, times(1)).delete(userId);
    }

    @Test
    @DisplayName("Should be able to revoke any user session")
    void shouldRevokeAnyUserSession() {
        // Given
        String userId = "another-user-id";

        // When
        sysLoginService.revoke(userId);

        // Then
        verify(sessionMapper, times(1)).delete(userId);
    }

    @Test
    @DisplayName("Should handle empty user ID")
    void shouldHandleEmptyUserId() {
        // Given
        String emptyUserId = "";

        // When
        sysLoginService.revoke(emptyUserId);

        // Then
        verify(sessionMapper, times(1)).delete(emptyUserId);
    }

    @Test
    @DisplayName("Should handle null username")
    void shouldHandleNullUsername() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Username cannot be null"));

        // When & Then
        assertThatThrownBy(() -> sysLoginService.login(null, "password"))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class);
    }

    @Test
    @DisplayName("Should handle null password")
    void shouldHandleNullPassword() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Password cannot be null"));

        // When & Then
        assertThatThrownBy(() -> sysLoginService.login("test-user-name", null))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class);
    }

    @Test
    @DisplayName("Should handle empty username")
    void shouldHandleEmptyUsername() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Empty username"));

        // When & Then
        assertThatThrownBy(() -> sysLoginService.login("", "password"))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class);
    }

    @Test
    @DisplayName("Should handle empty password")
    void shouldHandleEmptyPassword() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Empty password"));

        // When & Then
        assertThatThrownBy(() -> sysLoginService.login("test-user-name", ""))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class);
    }

    @Test
    @DisplayName("Should use configured token expiration time")
    void shouldUseConfiguredTokenExpireTime() {
        // Given
        long customExpireTime = 7200000L; // 2 hours
        when(authProperties.getAccessExpireMillis()).thenReturn(customExpireTime);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(testUserDetails);

        mockedJwtUtil.when(() -> JwtUtil.createToken("test-user-id", customExpireTime))
                .thenReturn(TEST_TOKEN);
        mockedJwtUtil.when(() -> JwtUtil.parseClaims(TEST_TOKEN))
                .thenReturn(claims);

        // When
        sysLoginService.login("test-user-name", "password");

        // Then
        mockedJwtUtil.verify(() -> JwtUtil.createToken("test-user-id", customExpireTime));
    }

    @Test
    @DisplayName("Should support multiple users logging in simultaneously")
    void shouldSupportMultipleUsersLogin() {
        // Given
        SecurityUserDetails user1 = TestDataFactory.createSecurityUserDetails("user-1");
        SecurityUserDetails user2 = TestDataFactory.createSecurityUserDetails("user-2");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal())
                .thenReturn(user1)
                .thenReturn(user2);

        mockedJwtUtil.when(() -> JwtUtil.createToken(anyString(), anyLong()))
                .thenReturn(TEST_TOKEN);
        mockedJwtUtil.when(() -> JwtUtil.parseClaims(TEST_TOKEN))
                .thenReturn(claims);

        // When
        String token1 = sysLoginService.login("user1", "password");
        String token2 = sysLoginService.login("user2", "password");

        // Then
        assertThat(token1).isNotNull();
        assertThat(token2).isNotNull();
        verify(sessionMapper, times(2)).save(any(SecurityUserDetails.class));
    }

    @Test
    @DisplayName("Should log login attempt when user tries to login")
    void shouldLogLoginAttemptWhenUserTriesToLogin() {
        // Given - Setup logger capture
        InMemoryAppender appender = new InMemoryAppender();
        appender.setContext((ch.qos.logback.classic.LoggerContext) LoggerFactory.getILoggerFactory());
        appender.start();

        Logger logger = (Logger) LoggerFactory.getLogger(SysLoginService.class);
        logger.addAppender(appender);
        logger.setLevel(Level.INFO);

        String username = "test-user-name";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(testUserDetails);
        mockedJwtUtil.when(() -> JwtUtil.createToken("test-user-id", ACCESS_EXPIRE_MILLIS))
                .thenReturn(TEST_TOKEN);
        mockedJwtUtil.when(() -> JwtUtil.parseClaims(TEST_TOKEN))
                .thenReturn(claims);

        // When
        sysLoginService.login(username, "password");

        // Then - Verify login attempt log
        assertThat(appender.getOutput()).contains("User login attempt - Username:");
        assertThat(appender.getOutput()).contains(username);

        // Cleanup
        logger.detachAppender(appender);
        appender.stop();
    }

    @Test
    @DisplayName("Should log successful login with user details and token fingerprint")
    void shouldLogSuccessfulLoginWithUserDetailsAndTokenFingerprint() {
        // Given - Setup logger capture
        InMemoryAppender appender = new InMemoryAppender();
        appender.setContext((ch.qos.logback.classic.LoggerContext) LoggerFactory.getILoggerFactory());
        appender.start();

        Logger logger = (Logger) LoggerFactory.getLogger(SysLoginService.class);
        logger.addAppender(appender);
        logger.setLevel(Level.INFO);

        String username = "test-user-name";
        String userId = "test-user-id";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(testUserDetails);
        mockedJwtUtil.when(() -> JwtUtil.createToken(userId, ACCESS_EXPIRE_MILLIS))
                .thenReturn(TEST_TOKEN);
        mockedJwtUtil.when(() -> JwtUtil.parseClaims(TEST_TOKEN))
                .thenReturn(claims);

        // When
        sysLoginService.login(username, "password");

        // Then - Verify login operation log
        assertThat(appender.getOutput()).contains("Operation [LOGIN]");
        assertThat(appender.getOutput()).contains("UserId: " + userId);
        assertThat(appender.getOutput()).contains("Username: " + username);
        assertThat(appender.getOutput()).contains("Success: true");
        assertThat(appender.getOutput()).contains("TokenFingerprint: " + TEST_TOKEN_FINGERPRINT);

        // Cleanup
        logger.detachAppender(appender);
        appender.stop();
    }

    @Test
    @DisplayName("Should log session revocation operation")
    void shouldLogSessionRevocationWhenUserIsRevoked() {
        // Given - Setup logger capture
        InMemoryAppender appender = new InMemoryAppender();
        appender.setContext((ch.qos.logback.classic.LoggerContext) LoggerFactory.getILoggerFactory());
        appender.start();

        Logger logger = (Logger) LoggerFactory.getLogger(SysLoginService.class);
        logger.addAppender(appender);
        logger.setLevel(Level.INFO);

        String userId = "test-user-id";

        // When
        sysLoginService.revoke(userId);

        // Then - Verify revocation log
        assertThat(appender.getOutput()).contains("Operation [REVOKE_SESSION]");
        assertThat(appender.getOutput()).contains("UserId: " + userId);
        assertThat(appender.getOutput()).contains("Success: true");

        // Cleanup
        logger.detachAppender(appender);
        appender.stop();
    }
}
