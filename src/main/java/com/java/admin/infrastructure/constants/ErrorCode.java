package com.java.admin.infrastructure.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // System Errors (10000-19999)
    SYSTEM_ERROR("10000", "系统内部错误", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR("10001", "数据库操作错误", HttpStatus.INTERNAL_SERVER_ERROR),

    // Business Errors (20000-29999)
    BUSINESS_ERROR("20000", "业务逻辑错误", HttpStatus.BAD_REQUEST),
    PARAM_VALIDATION_ERROR("20001", "参数验证失败", HttpStatus.BAD_REQUEST),
    DATA_NOT_FOUND("20002", "数据不存在", HttpStatus.NOT_FOUND),

    // Authentication Errors (30000-39999)
    AUTHENTICATION_ERROR("30000", "认证失败", HttpStatus.UNAUTHORIZED),
    AUTHORIZATION_ERROR("30001", "权限不足", HttpStatus.FORBIDDEN),
    TOKEN_INVALID("30002", "令牌无效", HttpStatus.UNAUTHORIZED),
    TOKEN_MISSING("30003", "令牌缺失", HttpStatus.UNAUTHORIZED),
    TOKEN_FINGERPRINT_MISMATCH("30004", "令牌指纹不匹配，可能已被使用", HttpStatus.UNAUTHORIZED),
    SESSION_EXPIRED("30005", "会话已过期", HttpStatus.UNAUTHORIZED);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}