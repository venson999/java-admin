package com.java.admin.modules.system.service;

import com.java.admin.config.AuthProperties;
import com.java.admin.infrastructure.model.SecurityUserDetails;
import com.java.admin.infrastructure.util.JwtUtil;
import com.java.admin.modules.system.mapper.SessionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SysLoginService {

    private final AuthenticationManager authenticationManager;
    private final SessionMapper sessionMapper;
    private final AuthProperties authProperties;

    public String login(String username, String password) {
        log.info("User login attempt - Username: {}", username);

        UsernamePasswordAuthenticationToken authRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(username, password);

        // Perform authentication
        Authentication authentication = authenticationManager.authenticate(authRequest);
        if (authentication.isAuthenticated()) {

            // Get user details
            SecurityUserDetails userDetails = (SecurityUserDetails) authentication.getPrincipal();

            // Create access token
            String token = JwtUtil.createToken(userDetails.getUserid(), authProperties.getAccessExpireMillis());
            String tokenFingerprint = JwtUtil.parseClaims(token).getId();

            // Save session
            userDetails.setCurrentTokenFingerprint(tokenFingerprint);
            sessionMapper.save(userDetails);

            log.info("Operation [LOGIN] - UserId: {}, Username: {}, Success: true, TokenFingerprint: {}",
                    userDetails.getUserid(), username, tokenFingerprint);
            return token;
        }

        return null;
    }

    public void revoke(String userId) {
        sessionMapper.delete(userId);
        log.info("Operation [REVOKE_SESSION] - UserId: {}, Success: true", userId);
    }
}
