package com.java.admin.modules.system.controller;

import com.java.admin.infrastructure.constants.ErrorCode;
import com.java.admin.infrastructure.model.Result;
import com.java.admin.infrastructure.model.SecurityUserDetails;
import com.java.admin.modules.system.model.LoginUser;
import com.java.admin.modules.system.service.SysLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login and logout APIs")
public class SysLoginController {

    private final SysLoginService sysLoginService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public Result<String> login(@RequestBody LoginUser loginUser) {
        String token = sysLoginService.login(loginUser.getUsername(), loginUser.getPassword());
        if (token == null) {
            return Result.error(ErrorCode.AUTHENTICATION_ERROR);
        }
        return Result.success(token);
    }

    @DeleteMapping("/logout")
    @Operation(summary = "User logout", description = "Logout current user and revoke session")
    public Result<String> logout(Authentication authentication) {
        String userId = ((SecurityUserDetails) authentication.getPrincipal()).getUserid();
        sysLoginService.revoke(userId);
        return Result.success();
    }

    @DeleteMapping("/revoke/{userId}")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(summary = "Revoke user session", description = "Revoke session for specific user (admin only)")
    public Result<String> revoke(@PathVariable String userId) {
        sysLoginService.revoke(userId);
        return Result.success();
    }
}
