package com.java.admin.infrastructure.handler;

import com.java.admin.infrastructure.model.SecurityUserDetails;
import com.java.admin.testutil.AbstractMockTest;
import com.java.admin.testutil.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PermissionHandler Unit Tests
 *
 * <p>Test Coverage:
 * <ul>
 *   <li>Admin role verification</li>
 *   <li>Role checking functionality</li>
 *   <li>Current user ID extraction</li>
 *   <li>Resource access control (admin or owner)</li>
 *   <li>Resource ownership verification</li>
 *   <li>Edge cases (null, unauthenticated, wrong types)</li>
 * </ul>
 *
 * <p>Coverage Target: 100%
 */
@DisplayName("PermissionHandler Unit Tests")
class PermissionHandlerTest extends AbstractMockTest {

    private final PermissionHandler permissionHandler = new PermissionHandler();

    // ==================== isAdmin() Tests ====================

    @Test
    @DisplayName("Should return true when user has ADMIN role")
    void shouldReturnTrueWhenUserHasAdminRole() {
        // Given
        Authentication auth = createAuthenticationWithAuthorities(
                TestDataFactory.createSecurityUserDetailsWithAuthorities("user-1", List.of("ROLE_ADMIN"))
        );

        // When
        boolean result = permissionHandler.isAdmin(auth);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when user has USER role")
    void shouldReturnFalseWhenUserHasUserRole() {
        // Given
        Authentication auth = createAuthenticationWithAuthorities(
                TestDataFactory.createSecurityUserDetailsWithAuthorities("user-1", List.of("ROLE_USER"))
        );

        // When
        boolean result = permissionHandler.isAdmin(auth);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when authentication is null")
    void shouldReturnFalseWhenAuthenticationIsNull() {
        // When
        boolean result = permissionHandler.isAdmin(null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when user is not authenticated")
    void shouldReturnFalseWhenUserNotAuthenticated() {
        // Given
        Authentication auth = createUnauthenticatedAuthentication();

        // When
        boolean result = permissionHandler.isAdmin(auth);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when user has no authorities")
    void shouldReturnFalseWhenUserHasNoAuthorities() {
        // Given
        Authentication auth = createAuthenticationWithAuthorities(
                TestDataFactory.createSecurityUserDetailsWithAuthorities("user-1", List.of())
        );

        // When
        boolean result = permissionHandler.isAdmin(auth);

        // Then
        assertThat(result).isFalse();
    }

    // ==================== hasRole() Tests ====================

    @Test
    @DisplayName("Should return true when user has the specified role with ROLE_ prefix")
    void shouldReturnTrueWhenUserHasRoleWithPrefix() {
        // Given
        Authentication auth = createAuthenticationWithAuthorities(
                TestDataFactory.createSecurityUserDetailsWithAuthorities("user-1", List.of("ROLE_USER", "ROLE_ADMIN"))
        );

        // When
        boolean result = permissionHandler.hasRole(auth, "ROLE_ADMIN");

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return true when user has the specified role without ROLE_ prefix")
    void shouldReturnTrueWhenUserHasRoleWithoutPrefix() {
        // Given
        Authentication auth = createAuthenticationWithAuthorities(
                TestDataFactory.createSecurityUserDetailsWithAuthorities("user-1", List.of("ROLE_USER"))
        );

        // When
        boolean result = permissionHandler.hasRole(auth, "USER");

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when user does not have the specified role")
    void shouldReturnFalseWhenUserDoesNotHaveRole() {
        // Given
        Authentication auth = createAuthenticationWithAuthorities(
                TestDataFactory.createSecurityUserDetailsWithAuthorities("user-1", List.of("ROLE_USER"))
        );

        // When
        boolean result = permissionHandler.hasRole(auth, "ROLE_ADMIN");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when authentication is null for hasRole")
    void shouldReturnFalseWhenAuthenticationIsNullForHasRole() {
        // When
        boolean result = permissionHandler.hasRole(null, "ROLE_ADMIN");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when user is not authenticated for hasRole")
    void shouldReturnFalseWhenUserNotAuthenticatedForHasRole() {
        // Given
        Authentication auth = createUnauthenticatedAuthentication();

        // When
        boolean result = permissionHandler.hasRole(auth, "ROLE_USER");

        // Then
        assertThat(result).isFalse();
    }

    // ==================== currentUserId() Tests ====================

    @Test
    @DisplayName("Should return user ID when authentication is valid")
    void shouldReturnUserIdWhenAuthenticationIsValid() {
        // Given
        String expectedUserId = "test-user-123";
        Authentication auth = createAuthenticationWithAuthorities(
                TestDataFactory.createSecurityUserDetails(expectedUserId)
        );

        // When
        String result = permissionHandler.currentUserId(auth);

        // Then
        assertThat(result).isEqualTo(expectedUserId);
    }

    @Test
    @DisplayName("Should return null when authentication is null")
    void shouldReturnNullWhenAuthenticationIsNull() {
        // When
        String result = permissionHandler.currentUserId(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should return null when user is not authenticated")
    void shouldReturnNullWhenUserNotAuthenticated() {
        // Given
        Authentication auth = createUnauthenticatedAuthentication();

        // When
        String result = permissionHandler.currentUserId(auth);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should return null when principal is not SecurityUserDetails")
    void shouldReturnNullWhenPrincipalIsNotSecurityUserDetails() {
        // Given
        Authentication auth = new UsernamePasswordAuthenticationToken("not-security-user-details", null);

        // When
        String result = permissionHandler.currentUserId(auth);

        // Then
        assertThat(result).isNull();
    }

    // ==================== canAccess() Tests ====================

    @Test
    @DisplayName("Should return true when admin user tries to access any resource")
    void shouldReturnTrueWhenAdminUserTriesToAccessAnyResource() {
        // Given
        String adminUserId = "admin-1";
        String resourceOwnerId = "user-2";
        Authentication auth = createAuthenticationWithAuthorities(
                TestDataFactory.createSecurityUserDetailsWithAuthorities(adminUserId, List.of("ROLE_ADMIN"))
        );

        // When
        boolean result = permissionHandler.canAccess(auth, resourceOwnerId);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return true when user tries to access their own resource")
    void shouldReturnTrueWhenUserTriesToAccessOwnResource() {
        // Given
        String userId = "user-1";
        Authentication auth = createAuthenticationWithAuthorities(
                TestDataFactory.createSecurityUserDetailsWithAuthorities(userId, List.of("ROLE_USER"))
        );

        // When
        boolean result = permissionHandler.canAccess(auth, userId);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when regular user tries to access another user's resource")
    void shouldReturnFalseWhenRegularUserTriesToAccessAnotherUsersResource() {
        // Given
        String userId = "user-1";
        String otherUserId = "user-2";
        Authentication auth = createAuthenticationWithAuthorities(
                TestDataFactory.createSecurityUserDetailsWithAuthorities(userId, List.of("ROLE_USER"))
        );

        // When
        boolean result = permissionHandler.canAccess(auth, otherUserId);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when authentication is null for canAccess")
    void shouldReturnFalseWhenAuthenticationIsNullForCanAccess() {
        // When
        boolean result = permissionHandler.canAccess(null, "resource-1");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when user is not authenticated for canAccess")
    void shouldReturnFalseWhenUserNotAuthenticatedForCanAccess() {
        // Given
        Authentication auth = createUnauthenticatedAuthentication();

        // When
        boolean result = permissionHandler.canAccess(auth, "resource-1");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return true when resource owner ID matches current user ID")
    void shouldReturnTrueWhenResourceOwnerIdMatchesCurrentUserId() {
        // Given
        String userId = "user-123";
        Authentication auth = createAuthenticationWithAuthorities(
                TestDataFactory.createSecurityUserDetails(userId)
        );

        // When
        boolean result = permissionHandler.canAccess(auth, userId);

        // Then
        assertThat(result).isTrue();
    }

    // ==================== isOwner() Tests ====================

    @Test
    @DisplayName("Should return true when user ID matches resource owner ID")
    void shouldReturnTrueWhenUserIdMatchesResourceOwnerId() {
        // Given
        String userId = "owner-1";
        Authentication auth = createAuthenticationWithAuthorities(
                TestDataFactory.createSecurityUserDetails(userId)
        );

        // When
        boolean result = permissionHandler.isOwner(auth, userId);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when user ID does not match resource owner ID")
    void shouldReturnFalseWhenUserIdDoesNotMatchResourceOwnerId() {
        // Given
        String userId = "user-1";
        String otherUserId = "user-2";
        Authentication auth = createAuthenticationWithAuthorities(
                TestDataFactory.createSecurityUserDetails(userId)
        );

        // When
        boolean result = permissionHandler.isOwner(auth, otherUserId);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when authentication is null for isOwner")
    void shouldReturnFalseWhenAuthenticationIsNullForIsOwner() {
        // When
        boolean result = permissionHandler.isOwner(null, "resource-1");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when user is not authenticated for isOwner")
    void shouldReturnFalseWhenUserNotAuthenticatedForIsOwner() {
        // Given
        Authentication auth = createUnauthenticatedAuthentication();

        // When
        boolean result = permissionHandler.isOwner(auth, "resource-1");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when principal is not SecurityUserDetails for isOwner")
    void shouldReturnFalseWhenPrincipalIsNotSecurityUserDetailsForIsOwner() {
        // Given
        Authentication auth = new UsernamePasswordAuthenticationToken("not-security-user-details", null);

        // When
        boolean result = permissionHandler.isOwner(auth, "resource-1");

        // Then
        assertThat(result).isFalse();
    }

    // ==================== Edge Cases and Scenarios ====================

    @Test
    @DisplayName("Should handle admin accessing own resource")
    void shouldHandleAdminAccessingOwnResource() {
        // Given
        String adminId = "admin-1";
        Authentication auth = createAuthenticationWithAuthorities(
                TestDataFactory.createSecurityUserDetailsWithAuthorities(adminId, List.of("ROLE_ADMIN"))
        );

        // When
        boolean result = permissionHandler.canAccess(auth, adminId);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should handle regular user accessing admin resource")
    void shouldHandleRegularUserAccessingAdminResource() {
        // Given
        String userId = "user-1";
        String adminId = "admin-1";
        Authentication auth = createAuthenticationWithAuthorities(
                TestDataFactory.createSecurityUserDetailsWithAuthorities(userId, List.of("ROLE_USER"))
        );

        // When
        boolean result = permissionHandler.canAccess(auth, adminId);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should handle user with multiple roles including ADMIN")
    void shouldHandleUserWithMultipleRolesIncludingAdmin() {
        // Given
        String userId = "super-admin-1";
        Authentication auth = createAuthenticationWithAuthorities(
                TestDataFactory.createSecurityUserDetailsWithAuthorities(
                        userId,
                        List.of("ROLE_USER", "ROLE_ADMIN", "ROLE_AUDITOR")
                )
        );

        // When
        boolean isAdmin = permissionHandler.isAdmin(auth);
        boolean hasAuditorRole = permissionHandler.hasRole(auth, "AUDITOR");

        // Then
        assertThat(isAdmin).isTrue();
        assertThat(hasAuditorRole).isTrue();
    }

    @Test
    @DisplayName("Should handle empty string resource owner ID")
    void shouldHandleEmptyStringResourceOwnerId() {
        // Given
        String userId = "user-1";
        Authentication auth = createAuthenticationWithAuthorities(
                TestDataFactory.createSecurityUserDetails(userId)
        );

        // When
        boolean result = permissionHandler.isOwner(auth, "");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should handle case-sensitive role matching")
    void shouldHandleCaseSensitiveRoleMatching() {
        // Given
        Authentication auth = createAuthenticationWithAuthorities(
                TestDataFactory.createSecurityUserDetailsWithAuthorities("user-1", List.of("ROLE_ADMIN"))
        );

        // When
        boolean lowercaseResult = permissionHandler.hasRole(auth, "admin");  // Becomes ROLE_admin
        boolean uppercaseResult = permissionHandler.hasRole(auth, "ADMIN");  // Becomes ROLE_ADMIN
        boolean withPrefixResult = permissionHandler.hasRole(auth, "ROLE_ADMIN");  // Stays ROLE_ADMIN

        // Then - role matching is case-sensitive
        assertThat(lowercaseResult).isFalse();  // "ROLE_admin" != "ROLE_ADMIN"
        assertThat(uppercaseResult).isTrue();   // "ROLE_ADMIN" == "ROLE_ADMIN"
        assertThat(withPrefixResult).isTrue();  // "ROLE_ADMIN" == "ROLE_ADMIN"
    }

    // ==================== Helper Methods ====================

    /**
     * Create Authentication with SecurityUserDetails principal
     */
    private Authentication createAuthenticationWithAuthorities(SecurityUserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    /**
     * Create unauthenticated Authentication
     */
    private Authentication createUnauthenticatedAuthentication() {
        return new UsernamePasswordAuthenticationToken(
                null,
                null,
                List.of()
        ) {
            @Override
            public boolean isAuthenticated() {
                return false;
            }
        };
    }
}
