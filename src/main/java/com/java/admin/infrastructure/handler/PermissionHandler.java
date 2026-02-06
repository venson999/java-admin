package com.java.admin.infrastructure.handler;

import com.java.admin.infrastructure.model.SecurityUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Permission Handler - Spring Security SpEL Helper Class
 * Used for permission checks in @PreAuthorize annotations
 */
@Component("perm")
public class PermissionHandler {

    /**
     * Check if current user has admin role
     *
     * @param authentication authentication information
     * @return true if user is admin
     */
    public boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
    }

    /**
     * Check if current user has the specified role
     *
     * @param authentication authentication information
     * @param role           role name (ROLE_ prefix not required)
     * @return true if user has the role
     */
    public boolean hasRole(Authentication authentication, String role) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> roleWithPrefix.equals(auth.getAuthority()));
    }

    /**
     * Get current logged-in user ID
     *
     * @param authentication authentication information
     * @return user ID
     */
    public String currentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof SecurityUserDetails details) {
            return details.getUserid();
        }
        return null;
    }

    /**
     * Check if current user can access the specified resource
     * Admin can access all resources, regular users can only access their own resources
     *
     * @param authentication  authentication information
     * @param resourceOwnerId resource owner ID
     * @return true if user can access
     */
    public boolean canAccess(Authentication authentication, String resourceOwnerId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return isAdmin(authentication) || resourceOwnerId.equals(currentUserId(authentication));
    }

    /**
     * Check if current user is the resource owner
     *
     * @param authentication  authentication information
     * @param resourceOwnerId resource owner ID
     * @return true if user is the owner
     */
    public boolean isOwner(Authentication authentication, String resourceOwnerId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return resourceOwnerId.equals(currentUserId(authentication));
    }
}
