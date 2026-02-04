package com.java.admin.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java.admin.infrastructure.model.Result;
import com.java.admin.modules.system.model.SysUser;
import com.java.admin.modules.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User management APIs")
@RequiredArgsConstructor
@Slf4j
public class SysUserController {

    private final SysUserService sysUserService;

    @GetMapping("/info")
    @Operation(summary = "Get current user info", description = "Retrieve authenticated user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User info retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token")
    })
    public Result<Object> getUserInfo() {
        return Result.success("user info");
    }

    /**
     * Paginated user list query (ADMIN only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Page users", description = "Get paginated list of users (ADMIN only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not an admin")
    })
    public Result<Page<SysUser>> pageUsers(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Username filter (fuzzy search)")
            @RequestParam(required = false) String username) {

        log.info("Page users request - Page: {}, Size: {}, Username: {}", page, size, username);
        Page<SysUser> result = sysUserService.pageUsers(page, size, username);
        log.info("Page users success - Total: {}", result.getTotal());
        return Result.success(result);
    }
}
