package com.java.admin.infrastructure.filter;

import com.java.admin.infrastructure.constants.ErrorCode;
import com.java.admin.infrastructure.model.SecurityUserDetails;
import com.java.admin.infrastructure.util.JwtUtil;
import com.java.admin.infrastructure.util.ServletUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${auth.skip-paths}")
    private ArrayList<String> skipPaths;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip configured paths
        String requestURI = request.getRequestURI();
        if (skipPaths.contains(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get JWT token
        String accessToken = request.getHeader("access_token");
        if (accessToken == null || accessToken.isEmpty()) {
            ServletUtil.renderErrorResponse(response, ErrorCode.TOKEN_MISSING);
            return;
        }

        String subject;
        try {
            // Parse JWT token to get user identifier
            Claims claims = JwtUtil.parseClaims(accessToken);
            subject = claims.getSubject();
        } catch (Exception e) {
            ServletUtil.renderErrorResponse(response, ErrorCode.TOKEN_INVALID);
            return;
        }

        // Get user details from cache
        SecurityUserDetails user = (SecurityUserDetails) redisTemplate.opsForValue().get("user:" + subject);
        if (user == null) {
            ServletUtil.renderErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
            return;
        }

        // Set user authentication info to Spring Security context
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}
