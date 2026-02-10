package com.java.admin.modules.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java.admin.infrastructure.constants.ErrorCode;
import com.java.admin.infrastructure.exception.AppException;
import com.java.admin.modules.system.dto.CreateUserRequestDTO;
import com.java.admin.modules.system.dto.UpdateUserRequestDTO;
import com.java.admin.modules.system.mapper.SysUserMapper;
import com.java.admin.modules.system.model.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class SysUserService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Escape special characters in username for safe LIKE query
     * Escapes % and _ which are wildcards in SQL LIKE clauses
     *
     * @param username the username to escape
     * @return escaped username safe for LIKE queries
     */
    private String escapeUsernameForLike(String username) {
        return username.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

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
            // Escape special characters to prevent SQL injection in LIKE queries
            String escapedUsername = escapeUsernameForLike(username);
            queryWrapper.like(SysUser::getUserName, escapedUsername);
        }
        // @TableLogic annotation automatically filters deleted=1 records

        // Execute paginated query
        Page<SysUser> userPage = sysUserMapper.selectPage(pageParam, queryWrapper);

        log.debug("Page users completed - Total: {}, Records: {}", userPage.getTotal(), userPage.getRecords().size());
        return userPage;
    }

    /**
     * Query user by ID
     *
     * @param userId User ID
     * @return User entity
     * @throws AppException if user not found
     */
    public SysUser getUserById(String userId) {
        log.debug("Get user by ID started - User ID: {}", userId);

        // Query user (automatically filters deleted=1 due to @TableLogic)
        SysUser user = sysUserMapper.selectById(userId);

        if (user == null) {
            log.warn("User not found - User ID: {}", userId);
            throw new AppException(ErrorCode.DATA_NOT_FOUND, "User not found");
        }

        log.debug("Get user by ID completed - User ID: {}, Username: {}", userId, user.getUserName());
        return user;
    }

    /**
     * Create a new user
     *
     * @param dto Create user request DTO
     * @throws AppException if username already exists
     */
    public void createUser(CreateUserRequestDTO dto) {
        log.debug("Create user started - Username: {}", dto.getUsername());

        // Check username uniqueness (including deleted users)
        LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysUser::getUserName, dto.getUsername());
        // Use selectCount to avoid @TableLogic filtering
        Long count = sysUserMapper.selectCount(queryWrapper);

        if (count > 0) {
            log.warn("Username already exists - Username: {}", dto.getUsername());
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        // Create user entity
        SysUser user = new SysUser();
        user.setUserName(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());

        // Insert user (audit fields will be auto-filled by MybatisPlusMetaObjectHandler)
        int insertResult = sysUserMapper.insert(user);

        if (insertResult <= 0) {
            log.error("Failed to insert user - Username: {}", dto.getUsername());
            throw new AppException(ErrorCode.SYSTEM_ERROR, "Failed to create user");
        }

        log.debug("Create user completed - User ID: {}, Username: {}",
                user.getUserId(), user.getUserName());
    }

    /**
     * Update user information
     *
     * @param userId User ID
     * @param dto    Update user request DTO
     * @throws AppException if user not found
     */
    public void updateUser(String userId, UpdateUserRequestDTO dto) {
        log.debug("Update user started - User ID: {}", userId);

        // Check if user exists
        SysUser existingUser = sysUserMapper.selectById(userId);
        if (existingUser == null) {
            log.warn("User not found for update - User ID: {}", userId);
            throw new AppException(ErrorCode.DATA_NOT_FOUND, "User not found");
        }

        // Update email (password field is ignored as per requirement)
        if (StringUtils.hasText(dto.getEmail())) {
            existingUser.setEmail(dto.getEmail());
        }

        // Update user (audit fields will be auto-filled by MybatisPlusMetaObjectHandler)
        int updateResult = sysUserMapper.updateById(existingUser);

        if (updateResult <= 0) {
            log.error("Failed to update user - User ID: {}", userId);
            throw new AppException(ErrorCode.SYSTEM_ERROR, "Failed to update user");
        }

        log.debug("Update user completed - User ID: {}, Username: {}", userId, existingUser.getUserName());
    }
}
