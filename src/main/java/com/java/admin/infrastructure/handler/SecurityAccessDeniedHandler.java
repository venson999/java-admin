package com.java.admin.infrastructure.handler;

import com.alibaba.fastjson2.JSON;
import com.java.admin.common.model.Result;
import com.java.admin.common.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SecurityAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        int code = 403;
        String path = request.getRequestURL().toString();
        String msg = String.format("请求访问：%s，鉴权失败，无法访问系统资源", path);
        ServletUtil.renderString(response, JSON.toJSONString(Result.error(code, msg)));
    }
}
