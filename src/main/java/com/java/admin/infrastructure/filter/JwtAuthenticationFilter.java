package com.java.admin.infrastructure.filter;

import com.java.admin.config.AuthProperties;
import com.java.admin.infrastructure.constants.ErrorCode;
import com.java.admin.infrastructure.model.SecurityUserDetails;
import com.java.admin.infrastructure.util.JwtUtil;
import com.java.admin.infrastructure.util.ServletUtil;
import com.java.admin.modules.system.mapper.SessionMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final SessionMapper sessionMapper;
    private final AuthProperties authProperties;

    @Value("${auth.skip-paths}")
    private Set<String> skipPaths;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.debug("Authentication filter started - URI: {}", requestURI);

        // Skip configured paths
        if (skipPaths.contains(requestURI)) {
            log.debug("Skip authentication - URI: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // Get access token
        String accessToken = request.getHeader("access_token");
        if (accessToken == null || accessToken.isEmpty()) {
            log.warn("Token missing - URI: {}", requestURI);
            ServletUtil.renderErrorResponse(response, ErrorCode.TOKEN_MISSING);
            return;
        }

        // Verify access token and session
        String userId;
        SecurityUserDetails user;
        try {

            // Access token valid
            userId = JwtUtil.parseClaims(accessToken).getSubject();
            user = sessionMapper.find(userId);
            log.debug("Token valid - UserId: {}", userId);

            // Access token expired
        } catch (ExpiredJwtException e) {
            userId = e.getClaims().getSubject();
            log.debug("Token expired - UserId: {}, Attempting refresh", userId);
            user = sessionMapper.find(userId);

            // Session expired
            if (user == null) {
                log.warn("Session expired - UserId: {}", userId);
                ServletUtil.renderErrorResponse(response, ErrorCode.SESSION_EXPIRED);
                return;
            }

            // Verify token fingerprint
            String tokenFingerprint = e.getClaims().getId();
            if (!tokenFingerprint.equals(user.getCurrentTokenFingerprint())) {
                log.warn("Token fingerprint mismatch - UserId: {}, Expected: {}, Actual: {}",
                        userId, user.getCurrentTokenFingerprint(), tokenFingerprint);
                ServletUtil.renderErrorResponse(response, ErrorCode.TOKEN_FINGERPRINT_MISMATCH);
                return;
            }

            // Create new access token
            String newToken = JwtUtil.createToken(userId, authProperties.getAccessExpireMillis());
            String newFingerprint = JwtUtil.parseClaims(newToken).getId();

            // Refresh session
            user.setCurrentTokenFingerprint(newFingerprint);
            sessionMapper.save(user);

            // Return new access token
            response.setHeader("new_access_token", newToken);
            log.debug("Operation [REFRESH_TOKEN] - UserId: {}, NewFingerprint: {}, Success: true", userId, newFingerprint);

            // Access token invalid
        } catch (JwtException e) {
            log.error("Token invalid - URI: {}, Error: {}", requestURI, e.getMessage(), e);
            ServletUtil.renderErrorResponse(response, ErrorCode.TOKEN_INVALID);
            return;
        }

        // session expired
        if (user == null) {
            log.warn("Session not found - UserId: {}", userId);
            ServletUtil.renderErrorResponse(response, ErrorCode.SESSION_EXPIRED);
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));

        log.debug("Authentication successful - UserId: {}, Username: {}, URI: {}",
                user.getUserid(), user.getUsername(), requestURI);

        filterChain.doFilter(request, response);
    }
}
