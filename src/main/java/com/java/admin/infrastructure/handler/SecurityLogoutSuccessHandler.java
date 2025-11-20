package com.java.admin.infrastructure.handler;

import com.alibaba.fastjson2.JSON;
import com.java.admin.common.model.Result;
import com.java.admin.common.util.JwtUtil;
import com.java.admin.common.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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

        String token = request.getHeader("access_token");
        String userId = JwtUtil.parseClaims(token).getSubject();
        redisTemplate.delete(String.format("user:%s", userId));
        ServletUtil.renderString(response, JSON.toJSONString(Result.success("登出成功")));
    }
}
