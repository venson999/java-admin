package com.java.admin.infrastructure.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.java.admin.infrastructure.model.SecurityUserDetails;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MyBatis-Plus field auto-fill handler
 * Automatically fills createdAt, updatedAt, createdBy, updatedBy fields
 */
@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdAt", Date.class, new Date());
        this.strictInsertFill(metaObject, "updatedAt", Date.class, new Date());
        this.strictInsertFill(metaObject, "createdBy", String.class, getCurrentUserId());
        this.strictInsertFill(metaObject, "updatedBy", String.class, getCurrentUserId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedAt", Date.class, new Date());
        this.strictUpdateFill(metaObject, "updatedBy", String.class, getCurrentUserId());
    }

    /**
     * Get current user ID from SecurityContext
     * @return user ID or "system" if not logged in or anonymous
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
            && !"anonymousUser".equals(authentication.getPrincipal())) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof SecurityUserDetails) {
                return ((SecurityUserDetails) principal).getUserid();
            }
        }
        return "system";
    }
}
