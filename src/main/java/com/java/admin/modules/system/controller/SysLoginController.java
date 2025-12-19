package com.java.admin.modules.system.controller;

import com.java.admin.infrastructure.constants.ErrorCode;
import com.java.admin.infrastructure.model.Result;
import com.java.admin.modules.system.model.LoginUser;
import com.java.admin.modules.system.service.SysLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SysLoginController {

    private final SysLoginService sysLoginService;

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginUser loginUser) {
        log.info("Login controller called - Username: {}", loginUser.getUsername());

        String token = sysLoginService.login(loginUser.getUsername(), loginUser.getPassword());
        if (token == null) {
            log.warn("Login failed - Username: {}", loginUser.getUsername());
            return Result.error(ErrorCode.AUTHENTICATION_ERROR.getCode(), ErrorCode.AUTHENTICATION_ERROR.getMessage());
        }

        log.info("Login successful - Username: {}", loginUser.getUsername());
        return Result.success(token);
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        String token = request.getHeader("access_token");
        log.info("Logout controller called");

        if (token != null) {
            sysLoginService.logout(token);
        }

        log.info("Logout successful");
        return Result.success("Logout successful");
    }
}
