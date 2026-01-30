package com.java.admin.modules.system.controller;

import com.java.admin.infrastructure.model.Result;
import com.java.admin.testutil.AbstractMockTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SysUserController Unit Tests
 *
 * <p>Test Coverage:
 * <ul>
 *   <li>User info query endpoint</li>
 *   <li>Response format verification</li>
 * </ul>
 *
 * <p>Coverage Target: 80%+
 */
@DisplayName("SysUserController Unit Tests")
class SysUserControllerTest extends AbstractMockTest {

    private final SysUserController controller = new SysUserController();

    @Test
    @DisplayName("Should return user info")
    void shouldReturnUserInfo() {
        // When
        Result<Object> result = controller.getUserInfo();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getData()).isEqualTo("user info");
    }

    @Test
    @DisplayName("Should return correct response format")
    void shouldReturnCorrectResponseFormat() {
        // When
        Result<Object> result = controller.getUserInfo();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isNotNull();
        assertThat(result.getData()).isNotNull();
    }

    @Test
    @DisplayName("Should return string type user info")
    void shouldReturnStringUserInfo() {
        // When
        Result<Object> result = controller.getUserInfo();

        // Then
        assertThat(result.getData()).isInstanceOf(String.class);
    }

    @Test
    @DisplayName("Should return correct user info content")
    void shouldReturnCorrectUserInfoContent() {
        // When
        Result<Object> result = controller.getUserInfo();

        // Then
        assertThat(result.getData()).isEqualTo("user info");
    }

    @Test
    @DisplayName("Should return 200 status code")
    void shouldReturn200StatusCode() {
        // When
        Result<Object> result = controller.getUserInfo();

        // Then
        assertThat(result.getCode()).isEqualTo("200");
    }

    @Test
    @DisplayName("Should return consistent results on multiple calls")
    void shouldReturnConsistentResultsOnMultipleCalls() {
        // When
        Result<Object> result1 = controller.getUserInfo();
        Result<Object> result2 = controller.getUserInfo();

        // Then
        assertThat(result1).isEqualTo(result2);
    }

    @Test
    @DisplayName("Should return non-null response")
    void shouldReturnNonNullResponse() {
        // When
        Result<Object> result = controller.getUserInfo();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isNotNull();
        assertThat(result.getData()).isNotNull();
    }

    @Test
    @DisplayName("Response should contain success status code")
    void shouldContainSuccessCode() {
        // When
        Result<Object> result = controller.getUserInfo();

        // Then
        assertThat(result.getCode()).isNotBlank();
        assertThat(result.getCode()).isEqualTo("200");
    }
}
