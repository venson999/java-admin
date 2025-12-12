package com.java.admin.modules.system.controller;

import com.java.admin.infrastructure.constants.ErrorCode;
import com.java.admin.infrastructure.model.Result;
import com.java.admin.modules.system.model.LoginUser;
import com.java.admin.modules.system.service.SysLoginService;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SysLoginController {

    private final SysLoginService sysLoginService;
    
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginUser loginUser) {

        String token = sysLoginService.login(loginUser.getUsername(), loginUser.getPassword());
        if (token == null) {
            return Result.error(ErrorCode.AUTHENTICATION_ERROR.getCode(), ErrorCode.AUTHENTICATION_ERROR.getMessage());
        }
        return Result.success(token);
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        String token = request.getHeader("access_token");
        if (token != null) {
            sysLoginService.logout(token);
        }
        return Result.success("Logout successful");
    }
}
