package com.java.admin.infrastructure.handler;

import com.java.admin.infrastructure.constants.ErrorCode;
import com.java.admin.infrastructure.exception.AppException;
import com.java.admin.infrastructure.model.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle authentication exception (spring security)
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Result<Void>> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        Result<Void> result = Result.error(ErrorCode.AUTHENTICATION_ERROR.getCode(), ErrorCode.AUTHENTICATION_ERROR.getMessage());
        return ResponseEntity.status(ErrorCode.AUTHENTICATION_ERROR.getHttpStatus()).body(result);
    }

    /**
     * Handle authorization exception (spring security)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<Void>> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        Result<Void> result = Result.error(ErrorCode.AUTHORIZATION_ERROR.getCode(), ErrorCode.AUTHORIZATION_ERROR.getMessage());
        return ResponseEntity.status(ErrorCode.AUTHORIZATION_ERROR.getHttpStatus()).body(result);
    }

    /**
     * Handle application exceptions (business exceptions)
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<Result<Void>> handleAppException(AppException e, HttpServletRequest request) {
        Result<Void> result = Result.error(e.getErrorCode().getCode(), e.getErrorMessage());
        return ResponseEntity.status(e.getHttpStatus()).body(result);
    }

    /**
     * Handle system exceptions (fallback)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e, HttpServletRequest request) {
        Result<Void> result = Result.error(ErrorCode.SYSTEM_ERROR.getCode(), ErrorCode.SYSTEM_ERROR.getMessage());
        return ResponseEntity.status(ErrorCode.SYSTEM_ERROR.getHttpStatus()).body(result);
    }
}