package com.java.admin.modules.system.controller;

import com.java.admin.common.model.Result;
import com.java.admin.modules.system.model.ReqLoginUser;
import com.java.admin.modules.system.service.SysLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SysLoginController {

    private final SysLoginService sysLoginService;

    @PostMapping("/login")
    public Result<String> login(@RequestBody ReqLoginUser reqLoginUser) {
        String token = sysLoginService.login(reqLoginUser.getUsername(), reqLoginUser.getPassword());

        if (token == null || token.isEmpty()) {
            return Result.error(400, "认证失败");
        }
        return Result.success(token);
    }
}
