package com.java.admin.modules.system.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/sayHello")
    public String sayHello() {
        return "anyone: hello";
    }

    @GetMapping("/admin/sayHello")
    @PreAuthorize("hasAuthority('admin')")
    public String sayHelloAdmin() {
        return "admin: hello";
    }

    @GetMapping("/user/sayHello")
    @PreAuthorize("hasAuthority('common')")
    public String sayHelloUser() {
        return "user: hello";
    }
}
