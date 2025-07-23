package com.java.admin.infrastructure.service;

import com.java.admin.infrastructure.model.SecurityUserDetails;
import com.java.admin.modules.system.mapper.SysAuthoritiesMapper;
import com.java.admin.modules.system.model.SysUser;
import com.java.admin.modules.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {

    private final SysUserService sysUserService;
    private final SysAuthoritiesMapper sysAuthoritiesMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SysUser sysUser = sysUserService.getUserByUsername(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException(username);
        }

        List<String> sysAuthorities = sysAuthoritiesMapper.selectAuthoritiesByUserId(sysUser.getUserId());

        return new SecurityUserDetails(sysUser, sysAuthorities);
    }
}
