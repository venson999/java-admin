package com.java.admin.modules.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

@Mapper
public interface SysAuthoritiesMapper {

    @Select("""
            SELECT
            	perm_name
            FROM
            	sys_user u
            LEFT JOIN sys_user_role ur ON u.user_id = ur.user_id
            LEFT JOIN sys_role r       ON ur.role_id = r.role_id
            LEFT JOIN sys_role_perm rp ON r.role_id = rp.role_id
            left join sys_perm p       ON rp.perm_id = p.perm_id
            WHERE u.user_id = #{userId}
            AND p.perm_name IS NOT NULL
            
            UNION ALL
            
            SELECT
            	CONCAT('ROLE_', r.role_name)
            FROM
            	sys_user u
            LEFT JOIN sys_user_role ur ON u.user_id = ur.user_id
            LEFT JOIN sys_role r       ON ur.role_id = r.role_id
            WHERE u.user_id = #{userId}
            AND r.role_name IS NOT NULL
            """)
    ArrayList<String> selectAuthoritiesByUserId(String userId);
}
