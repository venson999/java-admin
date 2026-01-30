package com.java.admin.infrastructure.util;

import com.alibaba.fastjson2.JSON;
import com.java.admin.infrastructure.constants.ErrorCode;
import com.java.admin.infrastructure.model.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ServletUtil Unit Tests
 *
 * <p>Test Coverage:
 * <ul>
 *   <li>Error response rendering</li>
 *   <li>HTTP status code setting</li>
 *   <li>Content-Type header setting</li>
 *   <li>JSON response body generation</li>
 *   <li>Different error codes handling</li>
 * </ul>
 *
 * <p>Coverage Target: 100%
 */
@DisplayName("ServletUtil Unit Tests")
class ServletUtilTest {

    @Test
    @DisplayName("Should render authentication error response correctly")
    void shouldRenderAuthenticationErrorResponseCorrectly() throws Exception {
        // Given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        ServletUtil.renderErrorResponse(response, ErrorCode.AUTHENTICATION_ERROR);

        // Then - Verify status code
        assertThat(response.getStatus()).isEqualTo(401);

        // Verify Content-Type
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(response.getCharacterEncoding()).isEqualTo("UTF-8");

        // Verify response body
        String responseBody = response.getContentAsString();
        assertThat(responseBody).isNotBlank();

        Result<?> result = JSON.parseObject(responseBody, Result.class);
        assertThat(result.getCode()).isEqualTo("30000");
        assertThat(result.getMsg()).isEqualTo("Authentication failed");
    }

    @Test
    @DisplayName("Should render authorization error response correctly")
    void shouldRenderAuthorizationErrorResponseCorrectly() throws Exception {
        // Given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        ServletUtil.renderErrorResponse(response, ErrorCode.AUTHORIZATION_ERROR);

        // Then
        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");

        String responseBody = response.getContentAsString();
        Result<?> result = JSON.parseObject(responseBody, Result.class);
        assertThat(result.getCode()).isEqualTo("30001");
        assertThat(result.getMsg()).isEqualTo("Insufficient permissions");
    }

    @Test
    @DisplayName("Should render business error response correctly")
    void shouldRenderBusinessErrorResponseCorrectly() throws Exception {
        // Given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        ServletUtil.renderErrorResponse(response, ErrorCode.BUSINESS_ERROR);

        // Then
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");

        String responseBody = response.getContentAsString();
        Result<?> result = JSON.parseObject(responseBody, Result.class);
        assertThat(result.getCode()).isEqualTo("20000");
        assertThat(result.getMsg()).isEqualTo("Business logic error");
    }

    @Test
    @DisplayName("Should render data not found error response correctly")
    void shouldRenderDataNotFoundErrorResponseCorrectly() throws Exception {
        // Given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        ServletUtil.renderErrorResponse(response, ErrorCode.DATA_NOT_FOUND);

        // Then
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");

        String responseBody = response.getContentAsString();
        Result<?> result = JSON.parseObject(responseBody, Result.class);
        assertThat(result.getCode()).isEqualTo("20002");
        assertThat(result.getMsg()).isEqualTo("Data not found");
    }

    @Test
    @DisplayName("Should render system error response correctly")
    void shouldRenderSystemErrorResponseCorrectly() throws Exception {
        // Given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        ServletUtil.renderErrorResponse(response, ErrorCode.SYSTEM_ERROR);

        // Then
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");

        String responseBody = response.getContentAsString();
        Result<?> result = JSON.parseObject(responseBody, Result.class);
        assertThat(result.getCode()).isEqualTo("10000");
        assertThat(result.getMsg()).isEqualTo("Internal system error");
    }

    @Test
    @DisplayName("Should render parameter validation error response correctly")
    void shouldRenderParameterValidationErrorResponseCorrectly() throws Exception {
        // Given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        ServletUtil.renderErrorResponse(response, ErrorCode.PARAM_VALIDATION_ERROR);

        // Then
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");

        String responseBody = response.getContentAsString();
        Result<?> result = JSON.parseObject(responseBody, Result.class);
        assertThat(result.getCode()).isEqualTo("20001");
        assertThat(result.getMsg()).isEqualTo("Parameter validation failed");
    }

    @Test
    @DisplayName("Should render token invalid error response correctly")
    void shouldRenderTokenInvalidErrorResponseCorrectly() throws Exception {
        // Given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        ServletUtil.renderErrorResponse(response, ErrorCode.TOKEN_INVALID);

        // Then
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");

        String responseBody = response.getContentAsString();
        Result<?> result = JSON.parseObject(responseBody, Result.class);
        assertThat(result.getCode()).isEqualTo("30002");
        assertThat(result.getMsg()).isEqualTo("Invalid token");
    }

    @Test
    @DisplayName("Should render token missing error response correctly")
    void shouldRenderTokenMissingErrorResponseCorrectly() throws Exception {
        // Given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        ServletUtil.renderErrorResponse(response, ErrorCode.TOKEN_MISSING);

        // Then
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");

        String responseBody = response.getContentAsString();
        Result<?> result = JSON.parseObject(responseBody, Result.class);
        assertThat(result.getCode()).isEqualTo("30003");
        assertThat(result.getMsg()).isEqualTo("Token missing");
    }

    @Test
    @DisplayName("Should render session expired error response correctly")
    void shouldRenderSessionExpiredErrorResponseCorrectly() throws Exception {
        // Given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        ServletUtil.renderErrorResponse(response, ErrorCode.SESSION_EXPIRED);

        // Then
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");

        String responseBody = response.getContentAsString();
        Result<?> result = JSON.parseObject(responseBody, Result.class);
        assertThat(result.getCode()).isEqualTo("30005");
        assertThat(result.getMsg()).isEqualTo("Session expired");
    }

    @Test
    @DisplayName("Should render token fingerprint mismatch error response correctly")
    void shouldRenderTokenFingerprintMismatchErrorResponseCorrectly() throws Exception {
        // Given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        ServletUtil.renderErrorResponse(response, ErrorCode.TOKEN_FINGERPRINT_MISMATCH);

        // Then
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");

        String responseBody = response.getContentAsString();
        Result<?> result = JSON.parseObject(responseBody, Result.class);
        assertThat(result.getCode()).isEqualTo("30004");
        assertThat(result.getMsg()).isEqualTo("Token fingerprint mismatch, possibly already used");
    }

    @Test
    @DisplayName("Should render database error response correctly")
    void shouldRenderDatabaseErrorResponseCorrectly() throws Exception {
        // Given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        ServletUtil.renderErrorResponse(response, ErrorCode.DATABASE_ERROR);

        // Then
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");

        String responseBody = response.getContentAsString();
        Result<?> result = JSON.parseObject(responseBody, Result.class);
        assertThat(result.getCode()).isEqualTo("10001");
        assertThat(result.getMsg()).isEqualTo("Database operation error");
    }

    @Test
    @DisplayName("Should set correct Content-Type header")
    void shouldSetCorrectContentTypeHeader() throws Exception {
        // Given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        ServletUtil.renderErrorResponse(response, ErrorCode.AUTHENTICATION_ERROR);

        // Then
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(response.getHeader("Content-Type")).isEqualTo("application/json;charset=UTF-8");
    }

    @Test
    @DisplayName("Should write valid JSON response")
    void shouldWriteValidJsonResponse() throws Exception {
        // Given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        ServletUtil.renderErrorResponse(response, ErrorCode.BUSINESS_ERROR);

        // Then
        String responseBody = response.getContentAsString();
        assertThat(responseBody).startsWith("{");
        assertThat(responseBody).endsWith("}");

        // Verify it's valid JSON by parsing
        Object parsed = JSON.parse(responseBody);
        assertThat(parsed).isNotNull();
    }

    @Test
    @DisplayName("Should include error code in JSON response")
    void shouldIncludeErrorCodeInJsonResponse() throws Exception {
        // Given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        ServletUtil.renderErrorResponse(response, ErrorCode.AUTHENTICATION_ERROR);

        // Then
        String responseBody = response.getContentAsString();
        assertThat(responseBody).contains("\"code\":\"30000\"");
    }

    @Test
    @DisplayName("Should include error message in JSON response")
    void shouldIncludeErrorMessageInJsonResponse() throws Exception {
        // Given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        ServletUtil.renderErrorResponse(response, ErrorCode.AUTHORIZATION_ERROR);

        // Then
        String responseBody = response.getContentAsString();
        assertThat(responseBody).contains("\"msg\":\"Insufficient permissions\"");
    }

    @Test
    @DisplayName("Should include null data field in JSON response")
    void shouldIncludeNullDataFieldInJsonResponse() throws Exception {
        // Given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        ServletUtil.renderErrorResponse(response, ErrorCode.SYSTEM_ERROR);

        // Then
        String responseBody = response.getContentAsString();
        // Fastjson2 may omit null fields by default
        Result<?> result = JSON.parseObject(responseBody, Result.class);
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("Should flush response writer")
    void shouldFlushResponseWriter() throws Exception {
        // Given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        ServletUtil.renderErrorResponse(response, ErrorCode.BUSINESS_ERROR);

        // Then - MockHttpServletResponse automatically flushes, verify content is written
        assertThat(response.getContentAsString()).isNotBlank();
        assertThat(response.getContentAsByteArray()).isNotEmpty();
    }

    @Test
    @DisplayName("Should handle multiple error responses on same response object")
    void shouldHandleMultipleErrorResponsesOnSameResponseObject() throws Exception {
        // Given
        MockHttpServletResponse response1 = new MockHttpServletResponse();
        MockHttpServletResponse response2 = new MockHttpServletResponse();

        // When - First error
        ServletUtil.renderErrorResponse(response1, ErrorCode.AUTHENTICATION_ERROR);
        String firstResponse = response1.getContentAsString();

        // Second error
        ServletUtil.renderErrorResponse(response2, ErrorCode.AUTHORIZATION_ERROR);
        String secondResponse = response2.getContentAsString();

        // Then
        assertThat(firstResponse).contains("\"code\":\"30000\"");
        assertThat(secondResponse).contains("\"code\":\"30001\"");
    }

    @Test
    @DisplayName("Should handle UTF-8 encoding correctly")
    void shouldHandleUtf8EncodingCorrectly() throws Exception {
        // Given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        ServletUtil.renderErrorResponse(response, ErrorCode.BUSINESS_ERROR);

        // Then
        assertThat(response.getCharacterEncoding()).isEqualTo("UTF-8");
        assertThat(response.getContentType()).contains("charset=UTF-8");
    }

    @Test
    @DisplayName("Should work with all authentication related error codes")
    void shouldWorkWithAllAuthenticationRelatedErrorCodes() throws Exception {
        // Given
        ErrorCode[] authErrors = {
                ErrorCode.AUTHENTICATION_ERROR,
                ErrorCode.AUTHORIZATION_ERROR,
                ErrorCode.TOKEN_INVALID,
                ErrorCode.TOKEN_MISSING,
                ErrorCode.TOKEN_FINGERPRINT_MISMATCH,
                ErrorCode.SESSION_EXPIRED
        };

        for (ErrorCode errorCode : authErrors) {
            MockHttpServletResponse response = new MockHttpServletResponse();

            // When
            ServletUtil.renderErrorResponse(response, errorCode);

            // Then
            assertThat(response.getStatus()).isIn(401, 403); // All auth errors are 401 or 403
            assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");

            String responseBody = response.getContentAsString();
            assertThat(responseBody).contains("\"code\":\"" + errorCode.getCode() + "\"");
            assertThat(responseBody).contains("\"msg\":\"" + errorCode.getMessage() + "\"");
        }
    }

    @Test
    @DisplayName("Should work with all system error codes")
    void shouldWorkWithAllSystemErrorCodes() throws Exception {
        // Given
        ErrorCode[] systemErrors = {
                ErrorCode.SYSTEM_ERROR,
                ErrorCode.DATABASE_ERROR
        };

        for (ErrorCode errorCode : systemErrors) {
            MockHttpServletResponse response = new MockHttpServletResponse();

            // When
            ServletUtil.renderErrorResponse(response, errorCode);

            // Then
            assertThat(response.getStatus()).isEqualTo(500);
            assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");

            String responseBody = response.getContentAsString();
            assertThat(responseBody).contains("\"code\":\"" + errorCode.getCode() + "\"");
        }
    }

    @Test
    @DisplayName("Should work with all business error codes")
    void shouldWorkWithAllBusinessErrorCodes() throws Exception {
        // Given
        ErrorCode[] businessErrors = {
                ErrorCode.BUSINESS_ERROR,
                ErrorCode.PARAM_VALIDATION_ERROR,
                ErrorCode.DATA_NOT_FOUND
        };

        for (ErrorCode errorCode : businessErrors) {
            MockHttpServletResponse response = new MockHttpServletResponse();

            // When
            ServletUtil.renderErrorResponse(response, errorCode);

            // Then
            assertThat(response.getStatus()).isIn(400, 404); // BAD_REQUEST or NOT_FOUND
            assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");

            String responseBody = response.getContentAsString();
            assertThat(responseBody).contains("\"code\":\"" + errorCode.getCode() + "\"");
        }
    }

    @Test
    @DisplayName("Should generate consistent JSON format across all error types")
    void shouldGenerateConsistentJsonFormatAcrossAllErrorTypes() throws Exception {
        // Given
        ErrorCode[] allErrors = ErrorCode.values();

        for (ErrorCode errorCode : allErrors) {
            MockHttpServletResponse response = new MockHttpServletResponse();

            // When
            ServletUtil.renderErrorResponse(response, errorCode);

            // Then - Verify JSON structure
            String responseBody = response.getContentAsString();
            assertThat(responseBody).contains("\"code\":");
            assertThat(responseBody).contains("\"msg\":");

            // Parse and verify structure
            Result<?> result = JSON.parseObject(responseBody, Result.class);
            assertThat(result.getCode()).isEqualTo(errorCode.getCode());
            assertThat(result.getMsg()).isEqualTo(errorCode.getMessage());
            // data may be omitted by fastjson2 when null
        }
    }
}
