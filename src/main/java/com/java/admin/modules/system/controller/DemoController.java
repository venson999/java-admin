package com.java.admin.modules.system.controller;

import com.java.admin.infrastructure.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
@Tag(name = "Demo", description = "Demo and test APIs")
public class DemoController {

    @GetMapping("/sayHello")
    @Operation(summary = "Public hello endpoint", description = "Returns greeting for anyone")
    public Result<String> sayHello() {
        return Result.success("anyone: hello");
    }

    @GetMapping("/admin/sayHello")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(summary = "Admin hello endpoint", description = "Returns greeting for admin users only")
    public Result<String> sayHelloAdmin() {
        return Result.success("admin: hello");
    }

    @GetMapping("/user/sayHello")
    @PreAuthorize("hasAuthority('common')")
    @Operation(summary = "User hello endpoint", description = "Returns greeting for common users only")
    public Result<String> sayHelloUser() {
        return Result.success("user: hello");
    }
}
