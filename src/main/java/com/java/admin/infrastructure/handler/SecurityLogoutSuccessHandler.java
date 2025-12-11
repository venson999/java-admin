package com.java.admin.infrastructure.handler;

import com.java.admin.infrastructure.model.Result;
import com.java.admin.infrastructure.util.JwtUtil;
import com.java.admin.infrastructure.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityLogoutSuccessHandler implements LogoutSuccessHandler {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            String token = request.getHeader("access_token");
            String userId = JwtUtil.parseClaims(token).getSubject();
            redisTemplate.delete(String.format("user:%s", userId));
        } catch (Exception e) {
            ServletUtil.renderObject(response, Result.success("Logout successful"), HttpStatus.OK.value());
        }
    }
}
