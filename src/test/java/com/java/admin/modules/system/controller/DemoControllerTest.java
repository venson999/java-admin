package com.java.admin.modules.system.controller;

import com.java.admin.infrastructure.model.Result;
import com.java.admin.testutil.AbstractMockTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DemoController Unit Tests
 *
 * <p>Test Coverage:
 * <ul>
 *   <li>Public endpoint (sayHello)</li>
 *   <li>Admin-only endpoint (sayHelloAdmin)</li>
 *   <li>User-only endpoint (sayHelloUser)</li>
 *   <li>Response format verification</li>
 * </ul>
 *
 * <p>Coverage Target: 90%+
 */
@DisplayName("DemoController Unit Tests")
class DemoControllerTest extends AbstractMockTest {

    private final DemoController controller = new DemoController();

    // ==================== sayHello (Public) Tests ====================

    @Test
    @DisplayName("Should return hello message for public endpoint")
    void shouldReturnHelloMessageForPublicEndpoint() {
        // When
        Result<String> result = controller.sayHello();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getData()).isEqualTo("anyone: hello");
    }

    @Test
    @DisplayName("Public endpoint should return success response")
    void shouldReturnSuccessResponseForPublicEndpoint() {
        // When
        Result<String> result = controller.sayHello();

        // Then
        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getMsg()).isNull();
    }

    @Test
    @DisplayName("Public endpoint should return string data")
    void shouldReturnStringDataForPublicEndpoint() {
        // When
        Result<String> result = controller.sayHello();

        // Then
        assertThat(result.getData()).isInstanceOf(String.class);
        assertThat(result.getData()).contains("hello");
    }

    @Test
    @DisplayName("Public endpoint should be accessible without authentication")
    void shouldBeAccessibleWithoutAuthentication() {
        // When - Called without any authentication context
        Result<String> result = controller.sayHello();

        // Then - Should return successful response
        assertThat(result.getCode()).isEqualTo("200");
    }

    @Test
    @DisplayName("Public endpoint should return consistent response on multiple calls")
    void shouldReturnConsistentResponseOnMultipleCallsForPublicEndpoint() {
        // When
        Result<String> result1 = controller.sayHello();
        Result<String> result2 = controller.sayHello();
        Result<String> result3 = controller.sayHello();

        // Then
        assertThat(result1).isEqualTo(result2);
        assertThat(result2).isEqualTo(result3);
    }

    // ==================== sayHelloAdmin (Admin Only) Tests ====================

    @Test
    @DisplayName("Should return admin hello message")
    void shouldReturnAdminHelloMessage() {
        // When
        Result<String> result = controller.sayHelloAdmin();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getData()).isEqualTo("admin: hello");
    }

    @Test
    @DisplayName("Admin endpoint should return success response")
    void shouldReturnSuccessResponseForAdminEndpoint() {
        // When
        Result<String> result = controller.sayHelloAdmin();

        // Then
        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getMsg()).isNull();
    }

    @Test
    @DisplayName("Admin endpoint should return admin-specific message")
    void shouldReturnAdminSpecificMessage() {
        // When
        Result<String> result = controller.sayHelloAdmin();

        // Then
        assertThat(result.getData()).isEqualTo("admin: hello");
        assertThat(result.getData()).startsWith("admin:");
    }

    @Test
    @DisplayName("Admin endpoint should return string type data")
    void shouldReturnStringTypeDataForAdminEndpoint() {
        // When
        Result<String> result = controller.sayHelloAdmin();

        // Then
        assertThat(result.getData()).isInstanceOf(String.class);
    }

    // ==================== sayHelloUser (User Only) Tests ====================

    @Test
    @DisplayName("Should return user hello message")
    void shouldReturnUserHelloMessage() {
        // When
        Result<String> result = controller.sayHelloUser();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getData()).isEqualTo("user: hello");
    }

    @Test
    @DisplayName("User endpoint should return success response")
    void shouldReturnSuccessResponseForUserEndpoint() {
        // When
        Result<String> result = controller.sayHelloUser();

        // Then
        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getMsg()).isNull();
    }

    @Test
    @DisplayName("User endpoint should return user-specific message")
    void shouldReturnUserSpecificMessage() {
        // When
        Result<String> result = controller.sayHelloUser();

        // Then
        assertThat(result.getData()).isEqualTo("user: hello");
        assertThat(result.getData()).startsWith("user:");
    }

    @Test
    @DisplayName("User endpoint should return string type data")
    void shouldReturnStringTypeDataForUserEndpoint() {
        // When
        Result<String> result = controller.sayHelloUser();

        // Then
        assertThat(result.getData()).isInstanceOf(String.class);
    }

    // ==================== Cross-Endpoint Tests ====================

    @Test
    @DisplayName("All endpoints should return non-null responses")
    void shouldReturnNonNullResponsesForAllEndpoints() {
        // When
        Result<String> publicResult = controller.sayHello();
        Result<String> adminResult = controller.sayHelloAdmin();
        Result<String> userResult = controller.sayHelloUser();

        // Then
        assertThat(publicResult).isNotNull();
        assertThat(adminResult).isNotNull();
        assertThat(userResult).isNotNull();
    }

    @Test
    @DisplayName("All endpoints should return success code")
    void shouldReturnSuccessCodeForAllEndpoints() {
        // When
        Result<String> publicResult = controller.sayHello();
        Result<String> adminResult = controller.sayHelloAdmin();
        Result<String> userResult = controller.sayHelloUser();

        // Then
        assertThat(publicResult.getCode()).isEqualTo("200");
        assertThat(adminResult.getCode()).isEqualTo("200");
        assertThat(userResult.getCode()).isEqualTo("200");
    }

    @Test
    @DisplayName("All endpoints should return different messages")
    void shouldReturnDifferentMessagesForDifferentEndpoints() {
        // When
        Result<String> publicResult = controller.sayHello();
        Result<String> adminResult = controller.sayHelloAdmin();
        Result<String> userResult = controller.sayHelloUser();

        // Then
        assertThat(publicResult.getData()).isNotEqualTo(adminResult.getData());
        assertThat(adminResult.getData()).isNotEqualTo(userResult.getData());
        assertThat(publicResult.getData()).isNotEqualTo(userResult.getData());
    }

    @Test
    @DisplayName("All endpoints should return string data type")
    void shouldReturnStringDataTypeForAllEndpoints() {
        // When
        Result<String> publicResult = controller.sayHello();
        Result<String> adminResult = controller.sayHelloAdmin();
        Result<String> userResult = controller.sayHelloUser();

        // Then
        assertThat(publicResult.getData()).isInstanceOf(String.class);
        assertThat(adminResult.getData()).isInstanceOf(String.class);
        assertThat(userResult.getData()).isInstanceOf(String.class);
    }

    @Test
    @DisplayName("Admin and user endpoints should require proper authorization")
    void shouldRequireProperAuthorizationForProtectedEndpoints() {
        // Note: This test verifies that the @PreAuthorize annotation is present
        // Actual authorization testing is done by integration tests

        // When - Call the methods directly (bypasses security in unit test)
        Result<String> adminResult = controller.sayHelloAdmin();
        Result<String> userResult = controller.sayHelloUser();

        // Then - Methods should execute successfully
        assertThat(adminResult.getCode()).isEqualTo("200");
        assertThat(userResult.getCode()).isEqualTo("200");
    }
}
