package com.java.admin.infrastructure.exception;

import com.java.admin.infrastructure.constants.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * AppException Unit Tests
 *
 * <p>Test Coverage:
 * <ul>
 *   <li>Constructor with ErrorCode only</li>
 *   <li>Constructor with ErrorCode and custom message</li>
 *   <li>Constructor with ErrorCode and cause</li>
 *   <li>Constructor with ErrorCode, custom message and cause</li>
 *   <li>Getter methods (getErrorCode, getErrorMessage, getHttpStatus)</li>
 *   <li>Inheritance from RuntimeException</li>
 * </ul>
 *
 * <p>Coverage Target: 100%
 */
@DisplayName("AppException Unit Tests")
class AppExceptionTest {

    // ==================== Constructor Tests ====================

    @Test
    @DisplayName("Should create exception with ErrorCode only")
    void shouldCreateExceptionWithErrorCodeOnly() {
        // Given
        ErrorCode errorCode = ErrorCode.BUSINESS_ERROR;

        // When
        AppException exception = new AppException(errorCode);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getErrorMessage()).isEqualTo(errorCode.getMessage());
        assertThat(exception.getHttpStatus()).isEqualTo(errorCode.getHttpStatus());
    }

    @Test
    @DisplayName("Should create exception with custom message")
    void shouldCreateExceptionWithCustomMessage() {
        // Given
        ErrorCode errorCode = ErrorCode.BUSINESS_ERROR;
        String customMessage = "Custom error message";

        // When
        AppException exception = new AppException(errorCode, customMessage);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getErrorMessage()).isEqualTo(customMessage);
        assertThat(exception.getHttpStatus()).isEqualTo(errorCode.getHttpStatus());
    }

    @Test
    @DisplayName("Should create exception with cause")
    void shouldCreateExceptionWithCause() {
        // Given
        ErrorCode errorCode = ErrorCode.SYSTEM_ERROR;
        Throwable cause = new RuntimeException("Root cause");

        // When
        AppException exception = new AppException(errorCode, cause);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorMessage()).isEqualTo(errorCode.getMessage());
        assertThat(exception.getHttpStatus()).isEqualTo(errorCode.getHttpStatus());
    }

    @Test
    @DisplayName("Should create exception with custom message and cause")
    void shouldCreateExceptionWithCustomMessageAndCause() {
        // Given
        ErrorCode errorCode = ErrorCode.SYSTEM_ERROR;
        String customMessage = "Custom system error";
        Throwable cause = new NullPointerException("Null value");

        // When
        AppException exception = new AppException(errorCode, customMessage, cause);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getErrorMessage()).isEqualTo(customMessage);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getHttpStatus()).isEqualTo(errorCode.getHttpStatus());
    }

    // ==================== getErrorCode Tests ====================

    @Test
    @DisplayName("Should return correct ErrorCode")
    void shouldReturnCorrectErrorCode() {
        // Given
        ErrorCode errorCode = ErrorCode.AUTHENTICATION_ERROR;

        // When
        AppException exception = new AppException(errorCode);

        // Then
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getErrorCode().getCode()).isEqualTo("30000");
    }

    @Test
    @DisplayName("Should return ErrorCode for different error types")
    void shouldReturnErrorCodeForDifferentErrorTypes() {
        // Given & When
        AppException businessException = new AppException(ErrorCode.BUSINESS_ERROR);
        AppException authException = new AppException(ErrorCode.AUTHENTICATION_ERROR);
        AppException systemException = new AppException(ErrorCode.SYSTEM_ERROR);

        // Then
        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.BUSINESS_ERROR);
        assertThat(authException.getErrorCode()).isEqualTo(ErrorCode.AUTHENTICATION_ERROR);
        assertThat(systemException.getErrorCode()).isEqualTo(ErrorCode.SYSTEM_ERROR);
    }

    // ==================== getErrorMessage Tests ====================

    @Test
    @DisplayName("Should return default error message from ErrorCode")
    void shouldReturnDefaultErrorMessage() {
        // Given
        ErrorCode errorCode = ErrorCode.BUSINESS_ERROR;

        // When
        AppException exception = new AppException(errorCode);

        // Then
        assertThat(exception.getErrorMessage()).isEqualTo(errorCode.getMessage());
        assertThat(exception.getErrorMessage()).isEqualTo("Business logic error");
    }

    @Test
    @DisplayName("Should return custom error message")
    void shouldReturnCustomErrorMessage() {
        // Given
        String customMessage = "Username already exists";

        // When
        AppException exception = new AppException(ErrorCode.BUSINESS_ERROR, customMessage);

        // Then
        assertThat(exception.getErrorMessage()).isEqualTo(customMessage);
    }

    @Test
    @DisplayName("Should return error message from getMessage")
    void shouldReturnErrorMessageFromGetMessage() {
        // Given
        ErrorCode errorCode = ErrorCode.AUTHENTICATION_ERROR;
        String customMessage = "Invalid credentials";

        // When
        AppException exception = new AppException(errorCode, customMessage);

        // Then
        assertThat(exception.getMessage()).isEqualTo(customMessage);
        assertThat(exception.getErrorMessage()).isEqualTo(exception.getMessage());
    }

    @Test
    @DisplayName("Should return empty message when message is null")
    void shouldHandleNullMessage() {
        // Given
        AppException exception = new AppException(ErrorCode.BUSINESS_ERROR, (String) null);

        // When
        String message = exception.getErrorMessage();

        // Then
        assertThat(message).isNull();
    }

    // ==================== getHttpStatus Tests ====================

    @Test
    @DisplayName("Should return correct HTTP status for system error")
    void shouldReturnInternalServerErrorForSystemError() {
        // Given
        AppException exception = new AppException(ErrorCode.SYSTEM_ERROR);

        // When
        var httpStatus = exception.getHttpStatus();

        // Then
        assertThat(httpStatus.value()).isEqualTo(500);
    }

    @Test
    @DisplayName("Should return correct HTTP status for business error")
    void shouldReturnBadRequestForBusinessError() {
        // Given
        AppException exception = new AppException(ErrorCode.BUSINESS_ERROR);

        // When
        var httpStatus = exception.getHttpStatus();

        // Then
        assertThat(httpStatus.value()).isEqualTo(400);
    }

    @Test
    @DisplayName("Should return correct HTTP status for authentication error")
    void shouldReturnUnauthorizedForAuthError() {
        // Given
        AppException exception = new AppException(ErrorCode.AUTHENTICATION_ERROR);

        // When
        var httpStatus = exception.getHttpStatus();

        // Then
        assertThat(httpStatus.value()).isEqualTo(401);
    }

    @Test
    @DisplayName("Should return correct HTTP status for data not found")
    void shouldReturnNotFoundForDataNotFound() {
        // Given
        AppException exception = new AppException(ErrorCode.DATA_NOT_FOUND);

        // When
        var httpStatus = exception.getHttpStatus();

        // Then
        assertThat(httpStatus.value()).isEqualTo(404);
    }

    @Test
    @DisplayName("Should return correct HTTP status for authorization error")
    void shouldReturnForbiddenForAuthzError() {
        // Given
        AppException exception = new AppException(ErrorCode.AUTHORIZATION_ERROR);

        // When
        var httpStatus = exception.getHttpStatus();

        // Then
        assertThat(httpStatus.value()).isEqualTo(403);
    }

    @Test
    @DisplayName("Should maintain HTTP status across constructors")
    void shouldMaintainHttpStatusAcrossConstructors() {
        // Given
        ErrorCode errorCode = ErrorCode.TOKEN_INVALID;

        // When
        AppException ex1 = new AppException(errorCode);
        AppException ex2 = new AppException(errorCode, "Custom message");
        AppException ex3 = new AppException(errorCode, new RuntimeException());
        AppException ex4 = new AppException(errorCode, "Message", new RuntimeException());

        // Then
        assertThat(ex1.getHttpStatus()).isEqualTo(errorCode.getHttpStatus());
        assertThat(ex2.getHttpStatus()).isEqualTo(errorCode.getHttpStatus());
        assertThat(ex3.getHttpStatus()).isEqualTo(errorCode.getHttpStatus());
        assertThat(ex4.getHttpStatus()).isEqualTo(errorCode.getHttpStatus());
    }

    // ==================== Inheritance Tests ====================

    @Test
    @DisplayName("Should extend RuntimeException")
    void shouldExtendRuntimeException() {
        // Given
        AppException exception = new AppException(ErrorCode.SYSTEM_ERROR);

        // Then
        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception).isInstanceOf(Exception.class);
        assertThat(exception).isInstanceOf(Throwable.class);
    }

    @Test
    @DisplayName("Should be throwable and catchable")
    void shouldBeThrowableAndCatchable() {
        // Given
        AppException exception = new AppException(ErrorCode.BUSINESS_ERROR);

        // When & Then - Verify exception can be thrown and caught as RuntimeException
        assertThatThrownBy(() -> {
            throw exception;
        })
                .isInstanceOf(RuntimeException.class)
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode()).isEqualTo(ErrorCode.BUSINESS_ERROR));
    }

    @Test
    @DisplayName("Should be catchable as Exception")
    void shouldBeCatchableAsException() {
        // Given
        AppException exception = new AppException(ErrorCode.SYSTEM_ERROR);

        // When & Then - Verify exception can be caught as Exception
        assertThatThrownBy(() -> {
            throw exception;
        })
                .isInstanceOf(Exception.class)
                .isInstanceOf(AppException.class);
    }

    // ==================== Cause Chain Tests ====================

    @Test
    @DisplayName("Should preserve cause chain")
    void shouldPreserveCauseChain() {
        // Given
        Throwable rootCause = new IllegalStateException("Root");
        Throwable middleCause = new RuntimeException("Middle", rootCause);

        // When
        AppException exception = new AppException(ErrorCode.SYSTEM_ERROR, "Top", middleCause);

        // Then
        assertThat(exception.getCause()).isEqualTo(middleCause);
        assertThat(exception.getCause().getCause()).isEqualTo(rootCause);
    }

    @Test
    @DisplayName("Should handle null cause gracefully")
    void shouldHandleNullCauseGracefully() {
        // Given
        AppException exception = new AppException(ErrorCode.SYSTEM_ERROR, (Throwable) null);

        // When
        Throwable cause = exception.getCause();

        // Then
        assertThat(cause).isNull();
    }

    @Test
    @DisplayName("Should handle null cause with custom message")
    void shouldHandleNullCauseWithCustomMessage() {
        // Given
        AppException exception = new AppException(ErrorCode.SYSTEM_ERROR, "Message", null);

        // When
        Throwable cause = exception.getCause();

        // Then
        assertThat(cause).isNull();
        assertThat(exception.getErrorMessage()).isEqualTo("Message");
    }

    // ==================== Different ErrorCode Types ====================

    @Test
    @DisplayName("Should work with all system error codes")
    void shouldWorkWithAllSystemErrorCodes() {
        // When
        AppException ex1 = new AppException(ErrorCode.SYSTEM_ERROR);
        AppException ex2 = new AppException(ErrorCode.DATABASE_ERROR);

        // Then
        assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.SYSTEM_ERROR);
        assertThat(ex2.getErrorCode()).isEqualTo(ErrorCode.DATABASE_ERROR);
        assertThat(ex1.getHttpStatus().value()).isEqualTo(500);
        assertThat(ex2.getHttpStatus().value()).isEqualTo(500);
    }

    @Test
    @DisplayName("Should work with all business error codes")
    void shouldWorkWithAllBusinessErrorCodes() {
        // When
        AppException ex1 = new AppException(ErrorCode.BUSINESS_ERROR);
        AppException ex2 = new AppException(ErrorCode.PARAM_VALIDATION_ERROR);
        AppException ex3 = new AppException(ErrorCode.DATA_NOT_FOUND);

        // Then
        assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.BUSINESS_ERROR);
        assertThat(ex2.getErrorCode()).isEqualTo(ErrorCode.PARAM_VALIDATION_ERROR);
        assertThat(ex3.getErrorCode()).isEqualTo(ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    @DisplayName("Should work with all authentication error codes")
    void shouldWorkWithAllAuthenticationErrorCodes() {
        // When
        AppException ex1 = new AppException(ErrorCode.AUTHENTICATION_ERROR);
        AppException ex2 = new AppException(ErrorCode.AUTHORIZATION_ERROR);
        AppException ex3 = new AppException(ErrorCode.TOKEN_INVALID);
        AppException ex4 = new AppException(ErrorCode.TOKEN_MISSING);
        AppException ex5 = new AppException(ErrorCode.TOKEN_FINGERPRINT_MISMATCH);
        AppException ex6 = new AppException(ErrorCode.SESSION_EXPIRED);

        // Then
        assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.AUTHENTICATION_ERROR);
        assertThat(ex2.getErrorCode()).isEqualTo(ErrorCode.AUTHORIZATION_ERROR);
        assertThat(ex3.getErrorCode()).isEqualTo(ErrorCode.TOKEN_INVALID);
        assertThat(ex4.getErrorCode()).isEqualTo(ErrorCode.TOKEN_MISSING);
        assertThat(ex5.getErrorCode()).isEqualTo(ErrorCode.TOKEN_FINGERPRINT_MISMATCH);
        assertThat(ex6.getErrorCode()).isEqualTo(ErrorCode.SESSION_EXPIRED);

        // Verify all return 401 except AUTHORIZATION_ERROR (403)
        assertThat(ex1.getHttpStatus().value()).isEqualTo(401);
        assertThat(ex2.getHttpStatus().value()).isEqualTo(403);
        assertThat(ex3.getHttpStatus().value()).isEqualTo(401);
        assertThat(ex4.getHttpStatus().value()).isEqualTo(401);
        assertThat(ex5.getHttpStatus().value()).isEqualTo(401);
        assertThat(ex6.getHttpStatus().value()).isEqualTo(401);
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("Should handle empty custom message")
    void shouldHandleEmptyCustomMessage() {
        // Given
        String emptyMessage = "";

        // When
        AppException exception = new AppException(ErrorCode.BUSINESS_ERROR, emptyMessage);

        // Then
        assertThat(exception.getErrorMessage()).isEmpty();
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BUSINESS_ERROR);
    }

    @Test
    @DisplayName("Should handle very long custom message")
    void shouldHandleVeryLongCustomMessage() {
        // Given
        String longMessage = "a".repeat(10000);

        // When
        AppException exception = new AppException(ErrorCode.BUSINESS_ERROR, longMessage);

        // Then
        assertThat(exception.getErrorMessage()).hasSize(10000);
    }

    @Test
    @DisplayName("Should handle special characters in custom message")
    void shouldHandleSpecialCharactersInCustomMessage() {
        // Given
        String specialMessage = "Error: 中文测试 \n\t \"quoted\" 'single'";

        // When
        AppException exception = new AppException(ErrorCode.BUSINESS_ERROR, specialMessage);

        // Then
        assertThat(exception.getErrorMessage()).isEqualTo(specialMessage);
    }
}
