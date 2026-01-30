package com.java.admin.infrastructure.exception;

import com.java.admin.infrastructure.constants.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Application unified exception class
 * Simplified exception handling following KISS principle
 * Serves as the application's unified exception entry point
 *
 * <h3>Usage examples:</h3>
 * <pre>
 * // Simple business exception
 * throw new AppException(ErrorCode.BUSINESS_ERROR);
 *
 * // Exception with custom message
 * throw new AppException(ErrorCode.AUTHENTICATION_ERROR, "Invalid username or password");
 *
 * // Exception with cause
 * throw new AppException(ErrorCode.SYSTEM_ERROR, throwable);
 *
 * // Exception with custom message and cause
 * throw new AppException(ErrorCode.SYSTEM_ERROR, "System error", throwable);
 * </pre>
 */
@Getter
public class AppException extends RuntimeException {

    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return getMessage();
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}