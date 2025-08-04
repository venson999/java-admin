package com.java.admin.modules.system.service;

import com.java.admin.common.util.JwtUtil;
import com.java.admin.infrastructure.model.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SysLoginService {

    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, Object> redisTemplate;

    public String login(String username, String password) {

        UsernamePasswordAuthenticationToken authRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        try {

            // authentication
            Authentication authenticate = authenticationManager.authenticate(authRequest);
            if (authenticate.isAuthenticated()) {

                // hold authentication info
                SecurityContextHolder.getContext().setAuthentication(authenticate);

                // user detail
                SecurityUserDetails userDetails = (SecurityUserDetails) authenticate.getPrincipal();

                // jwt
                String token = JwtUtil.createToken(
                        userDetails.getUserid(),
                        1000 * 60 * 5);

                // cache user detail
                redisTemplate.opsForValue().set(
                        "user:" + userDetails.getUserid(),
                        userDetails,
                        1000 * 60 * 5,
                        TimeUnit.MILLISECONDS);

                return token;
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
