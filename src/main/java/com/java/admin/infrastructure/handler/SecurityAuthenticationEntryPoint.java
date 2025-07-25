package com.java.admin.infrastructure.handler;

import com.alibaba.fastjson2.JSON;
import com.java.admin.common.model.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SecurityAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        int code = 401;
        String path = request.getRequestURL().toString();
        String msg = STR."请求访问：\{path}，认证失败，无法访问系统资源";
        String result = JSON.toJSONString(Result.error(code, msg));

        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(result);
    }
}
