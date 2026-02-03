package com.java.admin.infrastructure.filter;

import com.java.admin.config.AuthProperties;
import com.java.admin.infrastructure.model.SecurityUserDetails;
import com.java.admin.infrastructure.util.JwtUtil;
import com.java.admin.modules.system.mapper.SessionMapper;
import com.java.admin.testutil.AbstractMockTest;
import com.java.admin.testutil.TestDataFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * JwtAuthenticationFilter Unit Tests
 *
 * <p>Test Coverage:
 * <ul>
 *   <li>Skip configured paths</li>
 *   <li>Token missing scenarios</li>
 *   <li>Token valid scenarios</li>
 *   <li>Token expired and auto-refresh scenarios</li>
 *   <li>Token invalid scenarios</li>
 *   <li>Session expired scenarios</li>
 *   <li>Token fingerprint mismatch scenarios</li>
 * </ul>
 *
 * <p>Coverage Target: 90%+
 */
@DisplayName("JwtAuthenticationFilter Unit Tests")
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class JwtAuthenticationFilterTest extends AbstractMockTest {

    private static final String TEST_USER_ID = "test-user-id";
    private static final String TEST_URI = "/api/test";
    private static final String SKIP_URI = "/api/login";
    private static final String VALID_TOKEN = "valid-access-token";
    private static final String EXPIRED_TOKEN = "expired-access-token";
    private static final String NEW_TOKEN = "new-access-token";
    private static final String TOKEN_FINGERPRINT = "token-fingerprint-123";
    private static final String NEW_FINGERPRINT = "new-fingerprint-456";
    private static final long ACCESS_EXPIRE_MILLIS = 3600000L; // 1 hour
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private SessionMapper sessionMapper;
    @Mock
    private AuthProperties authProperties;
    @Mock
    private PrintWriter writer;
    private JwtAuthenticationFilter filter;
    private MockedStatic<JwtUtil> mockedJwtUtil;

    @BeforeEach
    void setUp() throws IOException {
        filter = new JwtAuthenticationFilter(sessionMapper, authProperties);

        // Configure skip paths mock
        when(authProperties.getSkipPaths())
                .thenReturn(List.of(SKIP_URI, "/api/public", "/api/register"));

        // Initialize Mock objects
        mockedJwtUtil = mockStatic(JwtUtil.class);

        // Configure default behavior
        when(response.getWriter()).thenReturn(writer);
        when(authProperties.getAccessExpireMillis()).thenReturn(ACCESS_EXPIRE_MILLIS);
    }

    @AfterEach
    void tearDown() {
        if (mockedJwtUtil != null) {
            mockedJwtUtil.close();
        }
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should skip configured paths")
    void shouldSkipConfiguredPaths() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn(SKIP_URI);

        // When - Use doFilter() instead of doFilterInternal() to test the complete filter behavior
        filter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        verify(sessionMapper, never()).find(anyString());
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    @DisplayName("Should skip multiple configured paths")
    void shouldSkipMultipleConfiguredPaths() throws ServletException, IOException {
        // Given - Test all skip paths
        String[] skipUris = {SKIP_URI, "/api/public", "/api/register"};

        for (String uri : skipUris) {
            reset(request, response, filterChain);
            when(response.getWriter()).thenReturn(writer);
            when(request.getRequestURI()).thenReturn(uri);

            // When - Use doFilter() instead of doFilterInternal()
            filter.doFilter(request, response, filterChain);

            // Then
            verify(filterChain, times(1)).doFilter(request, response);
            verify(sessionMapper, never()).find(anyString());
        }
    }

    @Test
    @DisplayName("Should return TOKEN_MISSING error when access_token not in request header")
    void shouldReturnTokenMissingWhenAccessTokenNotPresent() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn(TEST_URI);
        when(request.getHeader("access_token")).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(401);
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Should return TOKEN_MISSING error when access_token is empty string")
    void shouldReturnTokenMissingWhenAccessTokenIsEmpty() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn(TEST_URI);
        when(request.getHeader("access_token")).thenReturn("");

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(401);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Should successfully handle valid token")
    void shouldHandleValidTokenSuccessfully() throws ServletException, IOException {
        // Given
        SecurityUserDetails userDetails = TestDataFactory.createSecurityUserDetails(TEST_USER_ID);
        userDetails.setCurrentTokenFingerprint(TOKEN_FINGERPRINT);

        when(request.getRequestURI()).thenReturn(TEST_URI);
        when(request.getHeader("access_token")).thenReturn(VALID_TOKEN);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(TEST_USER_ID);
        mockedJwtUtil.when(() -> JwtUtil.parseClaims(VALID_TOKEN)).thenReturn(claims);

        when(sessionMapper.find(TEST_USER_ID)).thenReturn(userDetails);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(sessionMapper, times(1)).find(TEST_USER_ID);
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    @DisplayName("Should set SecurityContext when token is valid")
    void shouldSetSecurityContextWhenTokenValid() throws ServletException, IOException {
        // Given
        SecurityUserDetails userDetails = TestDataFactory.createSecurityUserDetails(TEST_USER_ID);
        userDetails.setCurrentTokenFingerprint(TOKEN_FINGERPRINT);

        when(request.getRequestURI()).thenReturn(TEST_URI);
        when(request.getHeader("access_token")).thenReturn(VALID_TOKEN);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(TEST_USER_ID);
        mockedJwtUtil.when(() -> JwtUtil.parseClaims(VALID_TOKEN)).thenReturn(claims);

        when(sessionMapper.find(TEST_USER_ID)).thenReturn(userDetails);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then - Verify SecurityContext is set
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isEqualTo(userDetails);
    }

    @Test
    @DisplayName("Should auto-refresh expired token when session is valid")
    void shouldRefreshExpiredTokenWhenSessionValid() throws ServletException, IOException {
        // Given
        SecurityUserDetails userDetails = TestDataFactory.createSecurityUserDetails(TEST_USER_ID);
        userDetails.setCurrentTokenFingerprint(TOKEN_FINGERPRINT);

        when(request.getRequestURI()).thenReturn(TEST_URI);
        when(request.getHeader("access_token")).thenReturn(EXPIRED_TOKEN);

        Claims expiredClaims = mock(Claims.class);
        when(expiredClaims.getSubject()).thenReturn(TEST_USER_ID);
        when(expiredClaims.getId()).thenReturn(TOKEN_FINGERPRINT);

        Claims newClaims = mock(Claims.class);
        when(newClaims.getId()).thenReturn(NEW_FINGERPRINT);

        ExpiredJwtException expiredException = new ExpiredJwtException(null, expiredClaims, "Token expired");

        mockedJwtUtil.when(() -> JwtUtil.parseClaims(EXPIRED_TOKEN))
                .thenThrow(expiredException);
        mockedJwtUtil.when(() -> JwtUtil.createToken(TEST_USER_ID, ACCESS_EXPIRE_MILLIS))
                .thenReturn(NEW_TOKEN);
        mockedJwtUtil.when(() -> JwtUtil.parseClaims(NEW_TOKEN))
                .thenReturn(newClaims);

        when(sessionMapper.find(TEST_USER_ID)).thenReturn(userDetails);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(sessionMapper, times(1)).find(TEST_USER_ID);
        verify(sessionMapper, times(1)).save(userDetails);
        verify(response).setHeader("new_access_token", NEW_TOKEN);
        verify(filterChain, times(1)).doFilter(request, response);

        // Verify fingerprint is updated
        assertThat(userDetails.getCurrentTokenFingerprint()).isEqualTo(NEW_FINGERPRINT);
    }

    @Test
    @DisplayName("Should return new access token in response header")
    void shouldReturnNewAccessTokenInResponseHeader() throws ServletException, IOException {
        // Given
        SecurityUserDetails userDetails = TestDataFactory.createSecurityUserDetails(TEST_USER_ID);
        userDetails.setCurrentTokenFingerprint(TOKEN_FINGERPRINT);

        when(request.getRequestURI()).thenReturn(TEST_URI);
        when(request.getHeader("access_token")).thenReturn(EXPIRED_TOKEN);

        Claims expiredClaims = mock(Claims.class);
        when(expiredClaims.getSubject()).thenReturn(TEST_USER_ID);
        when(expiredClaims.getId()).thenReturn(TOKEN_FINGERPRINT);

        Claims newClaims = mock(Claims.class);
        when(newClaims.getId()).thenReturn(NEW_FINGERPRINT);

        ExpiredJwtException expiredException = new ExpiredJwtException(null, expiredClaims, "Token expired");

        mockedJwtUtil.when(() -> JwtUtil.parseClaims(EXPIRED_TOKEN))
                .thenThrow(expiredException);
        mockedJwtUtil.when(() -> JwtUtil.createToken(TEST_USER_ID, ACCESS_EXPIRE_MILLIS))
                .thenReturn(NEW_TOKEN);
        mockedJwtUtil.when(() -> JwtUtil.parseClaims(NEW_TOKEN))
                .thenReturn(newClaims);

        when(sessionMapper.find(TEST_USER_ID)).thenReturn(userDetails);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setHeader(eq("new_access_token"), eq(NEW_TOKEN));
    }

    @Test
    @DisplayName("Should return SESSION_EXPIRED error when token is valid but session does not exist")
    void shouldReturnSessionExpiredWhenTokenValidButSessionNotExists() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn(TEST_URI);
        when(request.getHeader("access_token")).thenReturn(VALID_TOKEN);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(TEST_USER_ID);
        mockedJwtUtil.when(() -> JwtUtil.parseClaims(VALID_TOKEN)).thenReturn(claims);

        when(sessionMapper.find(TEST_USER_ID)).thenReturn(null);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(401);
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Should return SESSION_EXPIRED error when token is expired and session does not exist")
    void shouldReturnSessionExpiredWhenTokenExpiredAndSessionNotExists() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn(TEST_URI);
        when(request.getHeader("access_token")).thenReturn(EXPIRED_TOKEN);

        Claims expiredClaims = mock(Claims.class);
        when(expiredClaims.getSubject()).thenReturn(TEST_USER_ID);
        when(expiredClaims.getId()).thenReturn(TOKEN_FINGERPRINT);

        ExpiredJwtException expiredException = new ExpiredJwtException(null, expiredClaims, "Token expired");

        mockedJwtUtil.when(() -> JwtUtil.parseClaims(EXPIRED_TOKEN))
                .thenThrow(expiredException);

        when(sessionMapper.find(TEST_USER_ID)).thenReturn(null);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(401);
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(filterChain, never()).doFilter(request, response);
        verify(sessionMapper, never()).save(any());
    }

    @Test
    @DisplayName("Should return TOKEN_FINGERPRINT_MISMATCH error when fingerprint does not match")
    void shouldReturnTokenFingerprintMismatchWhenFingerprintNotMatch() throws ServletException, IOException {
        // Given
        SecurityUserDetails userDetails = TestDataFactory.createSecurityUserDetails(TEST_USER_ID);
        userDetails.setCurrentTokenFingerprint("different-fingerprint");

        when(request.getRequestURI()).thenReturn(TEST_URI);
        when(request.getHeader("access_token")).thenReturn(EXPIRED_TOKEN);

        Claims expiredClaims = mock(Claims.class);
        when(expiredClaims.getSubject()).thenReturn(TEST_USER_ID);
        when(expiredClaims.getId()).thenReturn(TOKEN_FINGERPRINT);

        ExpiredJwtException expiredException = new ExpiredJwtException(null, expiredClaims, "Token expired");

        mockedJwtUtil.when(() -> JwtUtil.parseClaims(EXPIRED_TOKEN))
                .thenThrow(expiredException);

        when(sessionMapper.find(TEST_USER_ID)).thenReturn(userDetails);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(401);
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(filterChain, never()).doFilter(request, response);
        verify(sessionMapper, never()).save(any());
    }

    @Test
    @DisplayName("Should return TOKEN_INVALID error when token is invalid")
    void shouldReturnTokenInvalidWhenTokenInvalid() throws ServletException, IOException {
        // Given
        String invalidToken = "invalid-token";
        when(request.getRequestURI()).thenReturn(TEST_URI);
        when(request.getHeader("access_token")).thenReturn(invalidToken);

        JwtException jwtException = new JwtException("Invalid token");
        mockedJwtUtil.when(() -> JwtUtil.parseClaims(invalidToken))
                .thenThrow(jwtException);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(401);
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle URI with special characters")
    void shouldHandleUriWithSpecialCharacters() throws ServletException, IOException {
        // Given
        String specialUri = "/api/test?param=value&other=123";
        when(request.getRequestURI()).thenReturn(specialUri);
        when(request.getHeader("access_token")).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(401);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle multiple consecutive requests")
    void shouldHandleMultipleRequests() throws ServletException, IOException {
        // Given
        SecurityUserDetails userDetails = TestDataFactory.createSecurityUserDetails(TEST_USER_ID);
        userDetails.setCurrentTokenFingerprint(TOKEN_FINGERPRINT);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(TEST_USER_ID);
        mockedJwtUtil.when(() -> JwtUtil.parseClaims(VALID_TOKEN)).thenReturn(claims);

        when(sessionMapper.find(TEST_USER_ID)).thenReturn(userDetails);
        when(request.getRequestURI()).thenReturn(TEST_URI);
        when(request.getHeader("access_token")).thenReturn(VALID_TOKEN);

        // When - Execute three consecutive requests
        filter.doFilterInternal(request, response, filterChain);
        filter.doFilterInternal(request, response, filterChain);
        filter.doFilterInternal(request, response, filterChain);

        // Then - Each should succeed
        verify(filterChain, times(3)).doFilter(request, response);
        verify(sessionMapper, times(3)).find(TEST_USER_ID);
    }

    @Test
    @DisplayName("Should handle token with spaces")
    void shouldHandleTokenWithSpaces() throws ServletException, IOException {
        // Given
        String tokenWithSpaces = " token-with-spaces ";
        when(request.getRequestURI()).thenReturn(TEST_URI);
        when(request.getHeader("access_token")).thenReturn(tokenWithSpaces);

        JwtException jwtException = new JwtException("Invalid token");
        mockedJwtUtil.when(() -> JwtUtil.parseClaims(tokenWithSpaces))
                .thenThrow(jwtException);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(401);
    }

    @Test
    @DisplayName("Should verify token contains correct user information")
    void shouldVerifyTokenContainsCorrectUserInfo() throws ServletException, IOException {
        // Given
        String customUserId = "custom-user-123";
        SecurityUserDetails userDetails = TestDataFactory.createSecurityUserDetails(customUserId);
        userDetails.setCurrentTokenFingerprint(TOKEN_FINGERPRINT);

        when(request.getRequestURI()).thenReturn(TEST_URI);
        when(request.getHeader("access_token")).thenReturn(VALID_TOKEN);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(customUserId);
        mockedJwtUtil.when(() -> JwtUtil.parseClaims(VALID_TOKEN)).thenReturn(claims);

        when(sessionMapper.find(customUserId)).thenReturn(userDetails);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(sessionMapper).find(customUserId);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isEqualTo(userDetails);
    }
}
