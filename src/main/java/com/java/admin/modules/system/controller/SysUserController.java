package com.java.admin.modules.system.controller;

import com.java.admin.common.model.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class SysUserController {

    @GetMapping("/info")
    public Result<Object> getUserInfo() {
        return Result.success("user info");
    }
}
