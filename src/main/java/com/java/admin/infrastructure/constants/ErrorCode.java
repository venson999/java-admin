package com.java.admin.infrastructure.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // System Errors (10000-19999)
    SYSTEM_ERROR("10000", "Internal system error", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR("10001", "Database operation error", HttpStatus.INTERNAL_SERVER_ERROR),

    // Business Errors (20000-29999)
    BUSINESS_ERROR("20000", "Business logic error", HttpStatus.BAD_REQUEST),
    PARAM_VALIDATION_ERROR("20001", "Parameter validation failed", HttpStatus.BAD_REQUEST),
    DATA_NOT_FOUND("20002", "Data not found", HttpStatus.NOT_FOUND),
    USERNAME_ALREADY_EXISTS("20003", "Username already exists", HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_YOURSELF("20004", "Cannot delete yourself", HttpStatus.BAD_REQUEST),

    // Authentication Errors (30000-39999)
    AUTHENTICATION_ERROR("30000", "Authentication failed", HttpStatus.UNAUTHORIZED),
    AUTHORIZATION_ERROR("30001", "Insufficient permissions", HttpStatus.FORBIDDEN),
    TOKEN_INVALID("30002", "Invalid token", HttpStatus.UNAUTHORIZED),
    TOKEN_MISSING("30003", "Token missing", HttpStatus.UNAUTHORIZED),
    TOKEN_FINGERPRINT_MISMATCH("30004", "Token fingerprint mismatch, possibly already used", HttpStatus.UNAUTHORIZED),
    SESSION_EXPIRED("30005", "Session expired", HttpStatus.UNAUTHORIZED);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}