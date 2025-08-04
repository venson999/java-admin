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
        String msg = STR."请求访问：\{path}，鉴权失败，无法访问系统资源";
        ServletUtil.renderString(response, JSON.toJSONString(Result.error(code, msg)));
    }
}
