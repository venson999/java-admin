package com.java.admin.modules.system.controller;

import com.java.admin.infrastructure.model.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/sayHello")
    public Result<String> sayHello() {
        return Result.success("anyone: hello");
    }

    @GetMapping("/admin/sayHello")
    @PreAuthorize("hasAuthority('admin')")
    public Result<String> sayHelloAdmin() {
        return Result.success("admin: hello");
    }

    @GetMapping("/user/sayHello")
    @PreAuthorize("hasAuthority('common')")
    public Result<String> sayHelloUser() {
        return Result.success("user: hello");
    }
}
