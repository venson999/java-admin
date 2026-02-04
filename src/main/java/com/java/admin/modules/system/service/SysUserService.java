package com.java.admin.modules.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java.admin.modules.system.mapper.SysUserMapper;
import com.java.admin.modules.system.model.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class SysUserService {

    private final SysUserMapper sysUserMapper;

    public SysUser getUserByUsername(String userName) {
        log.debug("Query user by username started - Username: {}", userName);
        LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysUser::getUserName, userName);
        SysUser user = sysUserMapper.selectOne(queryWrapper);
        log.debug("User query completed - Username: {}, Found: {}", userName, user != null);
        return user;
    }

    /**
     * Paginated user list query
     *
     * @param page     Page number (starts from 0)
     * @param size     Page size
     * @param username Username fuzzy search (optional)
     * @return Paginated results
     */
    public Page<SysUser> pageUsers(int page, int size, String username) {
        log.debug("Page users started - Page: {}, Size: {}, Username: {}", page, size, username);

        // Create pagination object
        Page<SysUser> pageParam = new Page<>(page, size);

        // Build query conditions
        LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.hasText(username)) {
            queryWrapper.like(SysUser::getUserName, username);
        }
        // @TableLogic annotation automatically filters deleted=1 records

        // Execute paginated query
        Page<SysUser> userPage = sysUserMapper.selectPage(pageParam, queryWrapper);

        log.debug("Page users completed - Total: {}, Records: {}", userPage.getTotal(), userPage.getRecords().size());
        return userPage;
    }
}
