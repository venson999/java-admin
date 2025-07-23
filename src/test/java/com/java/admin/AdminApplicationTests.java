package com.java.admin;

import com.java.admin.modules.system.model.SysUser;
import com.java.admin.modules.system.service.SysUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class AdminApplicationTests {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void contextLoads() {
        System.out.println(passwordEncoder.encode("123456"));
    }

    @Test
    void passwordTest() {
        SysUser sysUser = sysUserService.getUserByUsername("ry");
        System.out.println(passwordEncoder.matches("123456", sysUser.getPassword()));
    }

    @Test
    void redisTest() {

        System.out.println(redisTemplate.opsForValue().get("demo"));
    }

    @Test
    void writeRedis() {
        redisTemplate.opsForValue().set("demo", "demo");
    }
}
