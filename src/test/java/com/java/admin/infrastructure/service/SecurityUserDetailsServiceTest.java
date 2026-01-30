package com.java.admin.infrastructure.service;

import com.java.admin.infrastructure.model.SecurityUserDetails;
import com.java.admin.modules.system.mapper.SysAuthoritiesMapper;
import com.java.admin.modules.system.model.SysUser;
import com.java.admin.modules.system.service.SysUserService;
import com.java.admin.testutil.AbstractMockTest;
import com.java.admin.testutil.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * SecurityUserDetailsService Unit Tests
 *
 * <p>Test Coverage:
 * <ul>
 *   <li>Loading user details (loadUserByUsername)</li>
 *   <li>User exists and not exists scenarios</li>
 *   <li>Authority loading verification</li>
 * </ul>
 *
 * <p>Coverage Target: 90%+
 */
@DisplayName("SecurityUserDetailsService Unit Tests")
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class SecurityUserDetailsServiceTest extends AbstractMockTest {

    @Mock
    private SysUserService sysUserService;

    @Mock
    private SysAuthoritiesMapper sysAuthoritiesMapper;

    @InjectMocks
    private SecurityUserDetailsService securityUserDetailsService;

    private SysUser testUser;
    private ArrayList<String> testAuthorities;

    @BeforeEach
    void setUp() {
        testUser = TestDataFactory.createDefaultUser();
        testAuthorities = createAuthorities("ROLE_USER", "USER_READ");
    }

    // Helper method to create ArrayList
    private ArrayList<String> createAuthorities(String... auths) {
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, auths);
        return list;
    }

    @Test
    @DisplayName("Should successfully load existing user")
    void shouldLoadExistingUserSuccessfully() {
        // Given
        String username = "test-user-name";
        when(sysUserService.getUserByUsername(username)).thenReturn(testUser);
        when(sysAuthoritiesMapper.selectAuthoritiesByUserId(testUser.getUserId()))
                .thenReturn(testAuthorities);

        // When
        SecurityUserDetails userDetails = (SecurityUserDetails)
                securityUserDetailsService.loadUserByUsername(username);

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUserid()).isEqualTo(testUser.getUserId());
        assertThat(userDetails.getUsername()).isEqualTo(testUser.getUserName());
        assertThat(userDetails.getSysAuthorities()).isEqualTo(testAuthorities);

        verify(sysUserService, times(1)).getUserByUsername(username);
        verify(sysAuthoritiesMapper, times(1)).selectAuthoritiesByUserId(testUser.getUserId());
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        String username = "nonexistent";
        when(sysUserService.getUserByUsername(username)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> securityUserDetailsService.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining(username);

        verify(sysUserService, times(1)).getUserByUsername(username);
        verify(sysAuthoritiesMapper, never()).selectAuthoritiesByUserId(anyString());
    }

    @Test
    @DisplayName("Should correctly load user authorities")
    void shouldLoadUserAuthoritiesCorrectly() {
        // Given
        String username = "test-user-name";
        ArrayList<String> authorities = createAuthorities("ROLE_ADMIN", "USER_WRITE", "USER_DELETE");

        when(sysUserService.getUserByUsername(username)).thenReturn(testUser);
        when(sysAuthoritiesMapper.selectAuthoritiesByUserId(testUser.getUserId()))
                .thenReturn(authorities);

        // When
        SecurityUserDetails userDetails = (SecurityUserDetails)
                securityUserDetailsService.loadUserByUsername(username);

        // Then
        assertThat(userDetails.getSysAuthorities()).hasSize(3);
        assertThat(userDetails.getSysAuthorities()).containsExactlyElementsOf(authorities);
        assertThat(userDetails.getAuthorities()).hasSize(3);
    }

    @Test
    @DisplayName("Should handle empty authorities list")
    void shouldHandleEmptyAuthoritiesList() {
        // Given
        String username = "test-user-name";
        when(sysUserService.getUserByUsername(username)).thenReturn(testUser);
        when(sysAuthoritiesMapper.selectAuthoritiesByUserId(testUser.getUserId()))
                .thenReturn(new ArrayList<>());

        // When
        SecurityUserDetails userDetails = (SecurityUserDetails)
                securityUserDetailsService.loadUserByUsername(username);

        // Then
        assertThat(userDetails.getSysAuthorities()).isNotNull();
        assertThat(userDetails.getSysAuthorities()).isEmpty();
        assertThat(userDetails.getAuthorities()).isEmpty();
    }

    @Test
    @DisplayName("Should return correct user details object")
    void shouldReturnCorrectUserDetailsObject() {
        // Given
        String username = "test-user-name";
        when(sysUserService.getUserByUsername(username)).thenReturn(testUser);
        when(sysAuthoritiesMapper.selectAuthoritiesByUserId(testUser.getUserId()))
                .thenReturn(testAuthorities);

        // When
        SecurityUserDetails userDetails = (SecurityUserDetails)
                securityUserDetailsService.loadUserByUsername(username);

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getSysUser()).isEqualTo(testUser);
        assertThat(userDetails.getPassword()).isEqualTo(testUser.getPassword());
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
    }

    @Test
    @DisplayName("Should handle null username")
    void shouldHandleNullUsername() {
        // Given
        when(sysUserService.getUserByUsername(null)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> securityUserDetailsService.loadUserByUsername(null))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    @DisplayName("Should handle empty username")
    void shouldHandleEmptyUsername() {
        // Given
        String emptyUsername = "";
        when(sysUserService.getUserByUsername(emptyUsername)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> securityUserDetailsService.loadUserByUsername(emptyUsername))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    @DisplayName("Should support single authority")
    void shouldSupportSingleAuthority() {
        // Given
        String username = "test-user-name";
        ArrayList<String> singleAuthority = createAuthorities("ROLE_USER");

        when(sysUserService.getUserByUsername(username)).thenReturn(testUser);
        when(sysAuthoritiesMapper.selectAuthoritiesByUserId(testUser.getUserId()))
                .thenReturn(singleAuthority);

        // When
        SecurityUserDetails userDetails = (SecurityUserDetails)
                securityUserDetailsService.loadUserByUsername(username);

        // Then
        assertThat(userDetails.getAuthorities()).hasSize(1);
    }

    @Test
    @DisplayName("Should support multiple authorities")
    void shouldSupportMultipleAuthorities() {
        // Given
        String username = "admin";
        ArrayList<String> multipleAuthorities = createAuthorities(
                "ROLE_ADMIN", "ROLE_USER", "USER_WRITE", "USER_DELETE", "USER_READ"
        );

        when(sysUserService.getUserByUsername(username)).thenReturn(testUser);
        when(sysAuthoritiesMapper.selectAuthoritiesByUserId(testUser.getUserId()))
                .thenReturn(multipleAuthorities);

        // When
        SecurityUserDetails userDetails = (SecurityUserDetails)
                securityUserDetailsService.loadUserByUsername(username);

        // Then
        assertThat(userDetails.getAuthorities()).hasSize(5);
    }

    @Test
    @DisplayName("Should verify GrantedAuthority conversion is correct")
    void shouldConvertToGrantedAuthorityCorrectly() {
        // Given
        String username = "test-user-name";
        ArrayList<String> authorities = createAuthorities("ROLE_USER", "USER_READ");

        when(sysUserService.getUserByUsername(username)).thenReturn(testUser);
        when(sysAuthoritiesMapper.selectAuthoritiesByUserId(testUser.getUserId()))
                .thenReturn(authorities);

        // When
        SecurityUserDetails userDetails = (SecurityUserDetails)
                securityUserDetailsService.loadUserByUsername(username);

        // Then
        assertThat(userDetails.getAuthorities())
                .allMatch(auth -> auth.getAuthority().equals("ROLE_USER") ||
                        auth.getAuthority().equals("USER_READ"));
    }

    @Test
    @DisplayName("Should find user before loading authorities")
    void shouldFindUserBeforeLoadingAuthorities() {
        // Given
        String username = "test-user-name";
        when(sysUserService.getUserByUsername(username)).thenReturn(testUser);
        when(sysAuthoritiesMapper.selectAuthoritiesByUserId(testUser.getUserId()))
                .thenReturn(testAuthorities);

        // When
        securityUserDetailsService.loadUserByUsername(username);

        // Then - Verify call order
        var inOrder = inOrder(sysUserService, sysAuthoritiesMapper);
        inOrder.verify(sysUserService).getUserByUsername(username);
        inOrder.verify(sysAuthoritiesMapper).selectAuthoritiesByUserId(testUser.getUserId());
    }

    @Test
    @DisplayName("Should not load authorities when user not found")
    void shouldNotLoadAuthoritiesWhenUserNotFound() {
        // Given
        String username = "nonexistent";
        when(sysUserService.getUserByUsername(username)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> securityUserDetailsService.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class);

        verify(sysAuthoritiesMapper, never()).selectAuthoritiesByUserId(anyString());
    }

    @Test
    @DisplayName("Should support loading multiple different users")
    void shouldSupportLoadingMultipleDifferentUsers() {
        // Given
        SysUser user1 = TestDataFactory.createAdminUser();
        SysUser user2 = TestDataFactory.createUserWithUsername("test-user-name");

        when(sysUserService.getUserByUsername("admin")).thenReturn(user1);
        when(sysUserService.getUserByUsername("test-user-name")).thenReturn(user2);
        when(sysAuthoritiesMapper.selectAuthoritiesByUserId(user1.getUserId()))
                .thenReturn(createAuthorities("ROLE_ADMIN"));
        when(sysAuthoritiesMapper.selectAuthoritiesByUserId(user2.getUserId()))
                .thenReturn(createAuthorities("ROLE_USER"));

        // When
        SecurityUserDetails userDetails1 = (SecurityUserDetails)
                securityUserDetailsService.loadUserByUsername("admin");
        SecurityUserDetails userDetails2 = (SecurityUserDetails)
                securityUserDetailsService.loadUserByUsername("test-user-name");

        // Then
        assertThat(userDetails1.getUsername()).isEqualTo("admin");
        assertThat(userDetails2.getUsername()).isEqualTo("test-user-name");
        assertThat(userDetails1.getAuthorities()).hasSize(1);
        assertThat(userDetails2.getAuthorities()).hasSize(1);
    }

    @Test
    @DisplayName("Should allow loading same user multiple times")
    void shouldAllowLoadingSameUserMultipleTimes() {
        // Given
        String username = "test-user-name";
        when(sysUserService.getUserByUsername(username)).thenReturn(testUser);
        when(sysAuthoritiesMapper.selectAuthoritiesByUserId(testUser.getUserId()))
                .thenReturn(testAuthorities);

        // When
        SecurityUserDetails userDetails1 = (SecurityUserDetails)
                securityUserDetailsService.loadUserByUsername(username);
        SecurityUserDetails userDetails2 = (SecurityUserDetails)
                securityUserDetailsService.loadUserByUsername(username);

        // Then
        assertThat(userDetails1).isNotNull();
        assertThat(userDetails2).isNotNull();
        verify(sysUserService, times(2)).getUserByUsername(username);
        verify(sysAuthoritiesMapper, times(2)).selectAuthoritiesByUserId(testUser.getUserId());
    }
}
