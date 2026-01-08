package com.java.admin.modules.system.controller;

import com.java.admin.infrastructure.constants.ErrorCode;
import com.java.admin.infrastructure.model.Result;
import com.java.admin.infrastructure.model.SecurityUserDetails;
import com.java.admin.modules.system.model.LoginUser;
import com.java.admin.modules.system.service.SysLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SysLoginController {

    private final SysLoginService sysLoginService;

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginUser loginUser) {
        String token = sysLoginService.login(loginUser.getUsername(), loginUser.getPassword());
        if (token == null) {
            return Result.error(ErrorCode.AUTHENTICATION_ERROR);
        }
        return Result.success(token);
    }

    @DeleteMapping("/logout")
    public Result<String> logout(Authentication authentication) {
        String userId = ((SecurityUserDetails) authentication.getPrincipal()).getUserid();
        sysLoginService.revoke(userId);
        return Result.success();
    }

    @DeleteMapping("/revoke/{userId}")
    @PreAuthorize("hasAuthority('admin')")
    public Result<String> revoke(@PathVariable String userId) {
        sysLoginService.revoke(userId);
        return Result.success();
    }
}
