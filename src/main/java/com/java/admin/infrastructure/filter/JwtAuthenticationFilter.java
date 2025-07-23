package com.java.admin.infrastructure.filter;

import com.java.admin.common.util.JwtUtil;
import com.java.admin.infrastructure.model.SecurityUserDetails;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if ("/login".equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = request.getHeader("access_token");
        if (accessToken == null || accessToken.isEmpty()) {
            throw new BadCredentialsException("token缺失");
        }

        String subject;
        try {
            Claims claims = JwtUtil.parseClaims(accessToken);
            subject = claims.getSubject();
        } catch (Exception e) {
            throw new BadCredentialsException("token错误");
        }

        // get user detail from cache
        SecurityUserDetails user = (SecurityUserDetails) redisTemplate.opsForValue().get("user:" + subject);
        if (user == null) {
            throw new BadCredentialsException("token过期");
        }

        // hold authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
