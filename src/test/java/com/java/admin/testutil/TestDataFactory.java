package com.java.admin.testutil;

import com.java.admin.infrastructure.model.SecurityUserDetails;
import com.java.admin.modules.system.model.LoginUser;
import com.java.admin.modules.system.model.SysUser;

import java.util.List;
import java.util.UUID;

/**
 * Test data factory
 *
 * <p>Provides standard methods for creating test data using builder pattern and defaults,
 * avoiding duplication and improving test readability.
 *
 * <p>Usage:
 * <pre>{@code
 * // Create user with defaults
 * SysUser user = TestDataFactory.createDefaultUser();
 *
 * // Customize specific fields
 * SysUser admin = TestDataFactory.createUserWithUsername("admin");
 * }</pre>
 */
public class TestDataFactory {

    private static final String DEFAULT_USER_ID = "test-user-id";
    private static final String DEFAULT_USERNAME = "test-user-name";
    private static final String DEFAULT_PASSWORD = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi"; // BCrypt encrypted "password"

    /**
     * Create SysUser with defaults
     *
     * @return SysUser with default values
     */
    public static SysUser createDefaultUser() {
        SysUser user = new SysUser();
        user.setUserId(DEFAULT_USER_ID);
        user.setUserName(DEFAULT_USERNAME);
        user.setPassword(DEFAULT_PASSWORD);
        return user;
    }

    /**
     * Create LoginUser with defaults
     *
     * @return LoginUser with default values
     */
    public static LoginUser createDefaultLoginUser() {
        LoginUser loginUser = new LoginUser();
        loginUser.setUsername(DEFAULT_USERNAME);
        loginUser.setPassword("password");
        return loginUser;
    }

    /**
     * Generate unique ID for test isolation
     *
     * @return unique UUID string
     */
    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Create user with specific username
     *
     * @param username the username
     * @return SysUser with configured username
     */
    public static SysUser createUserWithUsername(String username) {
        SysUser user = new SysUser();
        user.setUserId(DEFAULT_USER_ID);
        user.setUserName(username);
        user.setPassword(DEFAULT_PASSWORD);
        return user;
    }

    /**
     * Create admin user for permission tests
     *
     * @return SysUser with admin username
     */
    public static SysUser createAdminUser() {
        return createUserWithUsername("admin");
    }

    /**
     * Create SecurityUserDetails with defaults
     *
     * @return SecurityUserDetails with default values
     */
    public static SecurityUserDetails createDefaultSecurityUserDetails() {
        return createSecurityUserDetailsWithAuthorities(DEFAULT_USER_ID, List.of("ROLE_USER"));
    }

    /**
     * Create SecurityUserDetails with specific user ID
     *
     * @param userId the user ID
     * @return configured SecurityUserDetails
     */
    public static SecurityUserDetails createSecurityUserDetails(String userId) {
        return createSecurityUserDetailsWithAuthorities(userId, List.of("ROLE_USER"));
    }

    /**
     * Create SecurityUserDetails with authorities
     *
     * @param userId the user ID
     * @param authorities list of authorities
     * @return configured SecurityUserDetails
     */
    public static SecurityUserDetails createSecurityUserDetailsWithAuthorities(
            String userId,
            List<String> authorities) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setUserName(DEFAULT_USERNAME);
        user.setPassword(DEFAULT_PASSWORD);
        return new SecurityUserDetails(user, authorities);
    }
}
