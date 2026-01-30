package com.java.admin.testutil;

import com.java.admin.modules.system.model.LoginUser;
import com.java.admin.modules.system.model.SysUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Example test class - Verify test infrastructure
 *
 * <p>This class demonstrates how to use:
 * <ul>
 *   <li>AbstractMockTest - Test base class for Mockito tests</li>
 *   <li>TestDataFactory - Test data factory</li>
 *   <li>JUnit 5 - Test framework</li>
 *   <li>AssertJ - Assertion library</li>
 * </ul>
 *
 * <p>This test verifies correct configuration and availability of test infrastructure.
 */
@DisplayName("Test Infrastructure Verification Example")
class TestInfrastructureExample extends AbstractMockTest {

    @Test
    @DisplayName("Should create default user using TestDataFactory")
    void shouldCreateDefaultUserWhenUsingTestDataFactory() {
        // When: Create default user using factory
        SysUser user = TestDataFactory.createDefaultUser();

        // Then: Verify user properties are correctly set
        assertThat(user).isNotNull();
        assertThat(user.getUserId()).isEqualTo("test-user-id");
        assertThat(user.getUserName()).isEqualTo("test-user-name");
        assertThat(user.getPassword()).isNotNull();
        assertThat(user.getPassword()).startsWith("$2a$10$"); // BCrypt format
    }

    @Test
    @DisplayName("Should create user with custom username using TestDataFactory")
    void shouldCreateUserWithCustomUsernameWhenUsingFactoryMethod() {
        // When: Create custom user using factory
        SysUser user = TestDataFactory.createUserWithUsername("customuser");

        // Then: Verify username is correctly set
        assertThat(user).isNotNull();
        assertThat(user.getUserName()).isEqualTo("customuser");
        assertThat(user.getUserId()).isEqualTo("test-user-id");
    }

    @Test
    @DisplayName("Should create admin user using TestDataFactory")
    void shouldCreateAdminUserWhenUsingFactoryMethod() {
        // When: Create admin user using factory
        SysUser admin = TestDataFactory.createAdminUser();

        // Then: Verify admin username
        assertThat(admin).isNotNull();
        assertThat(admin.getUserName()).isEqualTo("admin");
    }

    @Test
    @DisplayName("Should create login user using TestDataFactory")
    void shouldCreateLoginUserWhenUsingFactoryMethod() {
        // When: Create login user using factory
        LoginUser loginUser = TestDataFactory.createDefaultLoginUser();

        // Then: Verify login user properties
        assertThat(loginUser).isNotNull();
        assertThat(loginUser.getUsername()).isEqualTo("test-user-name");
        assertThat(loginUser.getPassword()).isEqualTo("password");
    }

    @Test
    @DisplayName("Should generate unique user IDs")
    void shouldGenerateUniqueIdsWhenCallingFactoryMethod() {
        // When: Generate multiple unique IDs
        String id1 = TestDataFactory.generateUniqueId();
        String id2 = TestDataFactory.generateUniqueId();

        // Then: Verify IDs are unique and non-null
        assertThat(id1).isNotNull().isNotEmpty();
        assertThat(id2).isNotNull().isNotEmpty();
        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    @DisplayName("Should use AssertJ for fluent assertions")
    void shouldUseAssertJFluentAssertionsWhenMakingAssertions() {
        // Given: Create test user
        SysUser user = TestDataFactory.createDefaultUser();

        // Then: Use AssertJ fluent API for multiple assertions
        assertThat(user)
            .isNotNull()
            .hasFieldOrPropertyWithValue("userId", "test-user-id")
            .hasFieldOrPropertyWithValue("userName", "test-user-name")
            .hasFieldOrProperty("password");
    }
}
