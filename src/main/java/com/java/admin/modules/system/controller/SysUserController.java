package com.java.admin.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java.admin.infrastructure.model.Result;
import com.java.admin.modules.system.dto.CreateUserRequestDTO;
import com.java.admin.modules.system.model.SysUser;
import com.java.admin.modules.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    /**
     * Get user by ID (ADMIN or self)
     */
    @GetMapping("/{id}")
    @PreAuthorize("@perm.canAccess(authentication, #id)")
    @Operation(summary = "Get user by ID", description = "Get user details by ID (ADMIN or self)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not authorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public Result<SysUser> getUserById(
            @Parameter(description = "User ID")
            @PathVariable String id) {

        log.info("Get user by ID request - User ID: {}", id);
        SysUser result = sysUserService.getUserById(id);
        log.info("Get user by ID success - User ID: {}, Username: {}", id, result.getUserName());
        return Result.success(result);
    }

    /**
     * Create new user (ADMIN only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create user", description = "Create a new user (ADMIN only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - validation failed or username exists"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not an admin")
    })
    public Result<Void> createUser(
            @Parameter(description = "Create user request")
            @Valid @RequestBody CreateUserRequestDTO dto) {

        log.info("Create user request - username={}, email={}", dto.getUsername(), dto.getEmail());
        sysUserService.createUser(dto);
        log.info("Create user success - user created successfully");
        return Result.success();
    }
}
