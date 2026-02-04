package com.java.admin.modules.system.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("sys_user")
public class SysUser {

    @TableId
    private String userId;
    private String userName;
    @TableField(select = false)
    private String password;
    private String email;

    /**
     * Creation time - autofilled on insert
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    /**
     * Update time - autofilled on insert and update
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;

    /**
     * Creator ID - autofilled on insert
     */
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    /**
     * Updater ID - autofilled on insert and update
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;

    /**
     * Soft delete flag (0=not deleted, 1=deleted)
     */
    @TableLogic
    private Integer deleted;
}
