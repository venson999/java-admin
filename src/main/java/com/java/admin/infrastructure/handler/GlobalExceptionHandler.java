package com.java.admin.infrastructure.handler;

import com.java.admin.infrastructure.constants.ErrorCode;
import com.java.admin.infrastructure.exception.AppException;
import com.java.admin.infrastructure.model.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle authentication exception (spring security)
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Result<Void>> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        log.warn("Authentication failed - URI: {}, Method: {}, Error: {}",
            request.getRequestURI(), request.getMethod(), e.getMessage());

        Result<Void> result = Result.error(ErrorCode.AUTHENTICATION_ERROR.getCode(), ErrorCode.AUTHENTICATION_ERROR.getMessage());
        return ResponseEntity.status(ErrorCode.AUTHENTICATION_ERROR.getHttpStatus()).body(result);
    }

    /**
     * Handle authorization exception (spring security)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<Void>> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.warn("Access denied - URI: {}, Method: {}, Error: {}",
            request.getRequestURI(), request.getMethod(), e.getMessage());

        Result<Void> result = Result.error(ErrorCode.AUTHORIZATION_ERROR.getCode(), ErrorCode.AUTHORIZATION_ERROR.getMessage());
        return ResponseEntity.status(ErrorCode.AUTHORIZATION_ERROR.getHttpStatus()).body(result);
    }

    /**
     * Handle application exceptions (business exceptions)
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<Result<Void>> handleAppException(AppException e, HttpServletRequest request) {
        log.warn("Business exception - Type: {}, Code: {}, Message: {}, URI: {}",
            e.getClass().getSimpleName(), e.getErrorCode().getCode(), e.getErrorMessage(), request.getRequestURI());

        Result<Void> result = Result.error(e.getErrorCode().getCode(), e.getErrorMessage());
        return ResponseEntity.status(e.getHttpStatus()).body(result);
    }

    /**
     * Handle Bean Validation exceptions (@Valid)
     * <p>
     * This handles validation failures for request body parameters annotated with @Valid.
     * Extracts error messages from field errors and returns them in a user-friendly format.
     *
     * @param e      the validation exception
     * @param request the HTTP request
     * @return 400 Bad Request with error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidationException(
            MethodArgumentNotValidException e,
            HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("Validation failed - URI: {}, Method: {}, Errors: {}",
                request.getRequestURI(), request.getMethod(), errorMessage);

        Result<Void> result = Result.error(ErrorCode.PARAM_VALIDATION_ERROR.getCode(), errorMessage);
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * Handle constraint violation exceptions (@Validated)
     * <p>
     * This handles validation failures for request parameters annotated with @Validated.
     *
     * @param e      the validation exception
     * @param request the HTTP request
     * @return 400 Bad Request with error details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolationException(
            ConstraintViolationException e,
            HttpServletRequest request) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        log.warn("Constraint violation - URI: {}, Method: {}, Errors: {}",
                request.getRequestURI(), request.getMethod(), errorMessage);

        Result<Void> result = Result.error(ErrorCode.PARAM_VALIDATION_ERROR.getCode(), errorMessage);
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * Handle system exceptions (fallback)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e, HttpServletRequest request) {
        log.error("System exception - Type: {}, Message: {}, URI: {}",
            e.getClass().getSimpleName(), e.getMessage(), request.getRequestURI(), e);

        Result<Void> result = Result.error(ErrorCode.SYSTEM_ERROR.getCode(), ErrorCode.SYSTEM_ERROR.getMessage());
        return ResponseEntity.status(ErrorCode.SYSTEM_ERROR.getHttpStatus()).body(result);
    }
}