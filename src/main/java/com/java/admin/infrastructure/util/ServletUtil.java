package com.java.admin.infrastructure.util;

import com.alibaba.fastjson2.JSON;
import com.java.admin.infrastructure.constants.ErrorCode;
import com.java.admin.infrastructure.model.Result;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ServletUtil {

    public static void renderErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        Result<Void> result = Result.error(errorCode.getCode(), errorCode.getMessage());
        response.getWriter().write(JSON.toJSONString(result));
        response.getWriter().flush();
    }
}
