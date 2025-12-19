package com.java.admin.infrastructure.service;

import com.java.admin.infrastructure.model.SecurityUserDetails;
import com.java.admin.modules.system.mapper.SysAuthoritiesMapper;
import com.java.admin.modules.system.model.SysUser;
import com.java.admin.modules.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityUserDetailsService implements UserDetailsService {

    private final SysUserService sysUserService;
    private final SysAuthoritiesMapper sysAuthoritiesMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user details - Username: {}", username);

        SysUser sysUser = sysUserService.getUserByUsername(username);
        if (sysUser == null) {
            log.warn("User not found - Username: {}", username);
            throw new UsernameNotFoundException(username);
        }

        List<String> sysAuthorities = sysAuthoritiesMapper.selectAuthoritiesByUserId(sysUser.getUserId());

        log.debug("User details loaded - Username: {}, Authorities: {}", username, sysAuthorities.size());
        return new SecurityUserDetails(sysUser, sysAuthorities);
    }
}
