package com.java.admin.infrastructure.handler;

import com.java.admin.infrastructure.constants.ErrorCode;
import com.java.admin.infrastructure.exception.AppException;
import com.java.admin.infrastructure.model.Result;
import com.java.admin.testutil.AbstractMockTest;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * GlobalExceptionHandler Unit Tests
 *
 * <p>Test Coverage:
 * <ul>
 *   <li>Authentication exception handling (AuthenticationException)</li>
 *   <li>Authorization exception handling (AccessDeniedException)</li>
 *   <li>Business exception handling (AppException)</li>
 *   <li>System exception handling (Exception)</li>
 * </ul>
 *
 * <p>Coverage Target: 90%+
 */
@DisplayName("GlobalExceptionHandler Unit Tests")
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class GlobalExceptionHandlerTest extends AbstractMockTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
    }

    @Test
    @DisplayName("Should handle authentication exception")
    void shouldHandleAuthenticationException() {
        // Given
        AuthenticationException exception = new BadCredentialsException("Invalid credentials");

        // When
        ResponseEntity<Result<Void>> response = globalExceptionHandler
                .handleAuthenticationException(exception, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(response.getBody().getCode()).isEqualTo("30000");
        assertThat(response.getBody().getMsg()).isEqualTo("Authentication failed");
    }

    @Test
    @DisplayName("Should handle different authentication exceptions")
    void shouldHandleDifferentAuthenticationExceptions() {
        // Given
        AuthenticationException exception1 = new BadCredentialsException("Bad credentials");
        AuthenticationException exception2 = new BadCredentialsException("User not found");

        // When
        ResponseEntity<Result<Void>> response1 = globalExceptionHandler
                .handleAuthenticationException(exception1, request);
        ResponseEntity<Result<Void>> response2 = globalExceptionHandler
                .handleAuthenticationException(exception2, request);

        // Then
        assertThat(response1.getStatusCode().value()).isEqualTo(401);
        assertThat(response1.getBody().getCode()).isEqualTo("30000");
        assertThat(response2.getStatusCode().value()).isEqualTo(401);
        assertThat(response2.getBody().getCode()).isEqualTo("30000");
    }

    @Test
    @DisplayName("Should handle authorization exception")
    void shouldHandleAccessDeniedException() {
        // Given
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        // When
        ResponseEntity<Result<Void>> response = globalExceptionHandler
                .handleAccessDeniedException(exception, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(403);
        assertThat(response.getBody().getCode()).isEqualTo("30001");
        assertThat(response.getBody().getMsg()).isEqualTo("Insufficient permissions");
    }

    @Test
    @DisplayName("Authorization exception response should return 403 status code")
    void shouldReturn403ForAccessDeniedException() {
        // Given
        AccessDeniedException exception = new AccessDeniedException("Forbidden");

        // When
        ResponseEntity<Result<Void>> response = globalExceptionHandler
                .handleAccessDeniedException(exception, request);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(403);
        assertThat(response.getBody().getCode()).isEqualTo("30001");
    }

    @Test
    @DisplayName("Should handle business exception")
    void shouldHandleAppException() {
        // Given
        AppException exception = new AppException(ErrorCode.BUSINESS_ERROR);

        // When
        ResponseEntity<Result<Void>> response = globalExceptionHandler
                .handleAppException(exception, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getCode()).isEqualTo("20000");
        assertThat(response.getBody().getMsg()).isEqualTo("Business logic error");
    }

    @Test
    @DisplayName("Should handle business exception with custom message")
    void shouldHandleAppExceptionWithCustomMessage() {
        // Given
        String customMessage = "Username already exists";
        AppException exception = new AppException(ErrorCode.BUSINESS_ERROR, customMessage);

        // When
        ResponseEntity<Result<Void>> response = globalExceptionHandler
                .handleAppException(exception, request);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getCode()).isEqualTo("20000");
        assertThat(response.getBody().getMsg()).isEqualTo(customMessage);
    }

    @Test
    @DisplayName("Should handle parameter validation exception")
    void shouldHandleParameterValidationException() {
        // Given
        AppException exception = new AppException(ErrorCode.PARAM_VALIDATION_ERROR);

        // When
        ResponseEntity<Result<Void>> response = globalExceptionHandler
                .handleAppException(exception, request);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getCode()).isEqualTo("20001");
        assertThat(response.getBody().getMsg()).isEqualTo("Parameter validation failed");
    }

    @Test
    @DisplayName("Should handle data not found exception")
    void shouldHandleDataNotFoundException() {
        // Given
        AppException exception = new AppException(ErrorCode.DATA_NOT_FOUND);

        // When
        ResponseEntity<Result<Void>> response = globalExceptionHandler
                .handleAppException(exception, request);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody().getCode()).isEqualTo("20002");
        assertThat(response.getBody().getMsg()).isEqualTo("Data not found");
    }

    @Test
    @DisplayName("Should handle system exception")
    void shouldHandleSystemException() {
        // Given
        Exception exception = new RuntimeException("System error");

        // When
        ResponseEntity<Result<Void>> response = globalExceptionHandler
                .handleException(exception, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().getCode()).isEqualTo("10000");
        assertThat(response.getBody().getMsg()).isEqualTo("Internal system error");
    }

    @Test
    @DisplayName("System exception response should return 500 status code")
    void shouldReturn500ForSystemException() {
        // Given
        Exception exception = new NullPointerException("Null pointer");

        // When
        ResponseEntity<Result<Void>> response = globalExceptionHandler
                .handleException(exception, request);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().getCode()).isEqualTo("10000");
        assertThat(response.getBody().getMsg()).isEqualTo("Internal system error");
    }

    @Test
    @DisplayName("Should handle different system exceptions")
    void shouldHandleDifferentSystemExceptions() {
        // Given
        Exception exception1 = new IllegalStateException("Illegal state");
        Exception exception2 = new IllegalArgumentException("Illegal argument");
        Exception exception3 = new RuntimeException("Runtime error");

        // When
        ResponseEntity<Result<Void>> response1 = globalExceptionHandler.handleException(exception1, request);
        ResponseEntity<Result<Void>> response2 = globalExceptionHandler.handleException(exception2, request);
        ResponseEntity<Result<Void>> response3 = globalExceptionHandler.handleException(exception3, request);

        // Then
        assertThat(response1.getStatusCode().value()).isEqualTo(500);
        assertThat(response2.getStatusCode().value()).isEqualTo(500);
        assertThat(response3.getStatusCode().value()).isEqualTo(500);

        assertThat(response1.getBody().getCode()).isEqualTo("10000");
        assertThat(response2.getBody().getCode()).isEqualTo("10000");
        assertThat(response3.getBody().getCode()).isEqualTo("10000");
    }

    @Test
    @DisplayName("All exception responses should contain code field")
    void shouldAllContainCodeField() {
        // Given
        AuthenticationException authException = new BadCredentialsException("Auth error");
        AccessDeniedException accessException = new AccessDeniedException("Access error");
        AppException appException = new AppException(ErrorCode.BUSINESS_ERROR);
        Exception systemException = new RuntimeException("System error");

        // When
        ResponseEntity<Result<Void>> response1 = globalExceptionHandler
                .handleAuthenticationException(authException, request);
        ResponseEntity<Result<Void>> response2 = globalExceptionHandler
                .handleAccessDeniedException(accessException, request);
        ResponseEntity<Result<Void>> response3 = globalExceptionHandler
                .handleAppException(appException, request);
        ResponseEntity<Result<Void>> response4 = globalExceptionHandler
                .handleException(systemException, request);

        // Then
        assertThat(response1.getBody().getCode()).isNotBlank();
        assertThat(response2.getBody().getCode()).isNotBlank();
        assertThat(response3.getBody().getCode()).isNotBlank();
        assertThat(response4.getBody().getCode()).isNotBlank();
    }

    @Test
    @DisplayName("All exception responses should contain message field")
    void shouldAllContainMessageField() {
        // Given
        Exception exception = new RuntimeException("Test error");

        // When
        ResponseEntity<Result<Void>> response = globalExceptionHandler
                .handleException(exception, request);

        // Then
        assertThat(response.getBody().getMsg()).isNotBlank();
    }

    @Test
    @DisplayName("All exception responses should have null data field")
    void shouldAllHaveNullDataField() {
        // Given
        AuthenticationException exception = new BadCredentialsException("Auth error");

        // When
        ResponseEntity<Result<Void>> response = globalExceptionHandler
                .handleAuthenticationException(exception, request);

        // Then
        assertThat(response.getBody().getData()).isNull();
    }

    @Test
    @DisplayName("Should handle empty exception message")
    void shouldHandleEmptyExceptionMessage() {
        // Given
        AuthenticationException exception = new BadCredentialsException("");

        // When
        ResponseEntity<Result<Void>> response = globalExceptionHandler
                .handleAuthenticationException(exception, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }

    @Test
    @DisplayName("Should handle null exception message")
    void shouldHandleNullExceptionMessage() {
        // Given
        AuthenticationException exception = new BadCredentialsException(null);

        // When
        ResponseEntity<Result<Void>> response = globalExceptionHandler
                .handleAuthenticationException(exception, request);

        // Then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("Authentication exception should return 401 not 500")
    void shouldReturn401Not500ForAuthenticationException() {
        // Given
        AuthenticationException exception = new BadCredentialsException("Auth failed");

        // When
        ResponseEntity<Result<Void>> response = globalExceptionHandler
                .handleAuthenticationException(exception, request);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(response.getStatusCode().value()).isNotEqualTo(500);
    }

    @Test
    @DisplayName("Authorization exception should return 403 not 500")
    void shouldReturn403Not500ForAccessDeniedException() {
        // Given
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        // When
        ResponseEntity<Result<Void>> response = globalExceptionHandler
                .handleAccessDeniedException(exception, request);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(403);
        assertThat(response.getStatusCode().value()).isNotEqualTo(500);
    }

    @Test
    @DisplayName("Business exception should return 400")
    void shouldReturn400ForBusinessException() {
        // Given
        AppException exception = new AppException(ErrorCode.BUSINESS_ERROR);

        // When
        ResponseEntity<Result<Void>> response = globalExceptionHandler
                .handleAppException(exception, request);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    @DisplayName("Data not found exception should return 404")
    void shouldReturn404ForDataNotFoundException() {
        // Given
        AppException exception = new AppException(ErrorCode.DATA_NOT_FOUND);

        // When
        ResponseEntity<Result<Void>> response = globalExceptionHandler
                .handleAppException(exception, request);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    @DisplayName("Should support handling multiple different exceptions")
    void shouldHandleMultipleDifferentExceptions() {
        // Given
        AuthenticationException authException = new BadCredentialsException("Auth error");
        AccessDeniedException accessException = new AccessDeniedException("Access error");
        AppException appException = new AppException(ErrorCode.BUSINESS_ERROR);

        // When
        ResponseEntity<Result<Void>> response1 = globalExceptionHandler
                .handleAuthenticationException(authException, request);
        ResponseEntity<Result<Void>> response2 = globalExceptionHandler
                .handleAccessDeniedException(accessException, request);
        ResponseEntity<Result<Void>> response3 = globalExceptionHandler
                .handleAppException(appException, request);

        // Then
        assertThat(response1.getStatusCode().value()).isEqualTo(401);
        assertThat(response2.getStatusCode().value()).isEqualTo(403);
        assertThat(response3.getStatusCode().value()).isEqualTo(400);
    }
}
