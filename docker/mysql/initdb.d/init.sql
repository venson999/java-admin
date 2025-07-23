-- ----------------------------
-- 用户信息表
-- ----------------------------
drop table if exists sys_user;
create table sys_user (
  user_id           bigint(20)      not null auto_increment    comment '用户ID',
  user_name         varchar(30)     not null                   comment '用户账号',
  password          varchar(100)    default ''                 comment '密码',
  primary key (user_id)
) engine=innodb auto_increment=100 comment = '用户信息表';

-- ----------------------------
-- 初始化-用户信息表数据
-- ----------------------------
insert into sys_user values(1, 'admin', '$2a$10$rzvwIEtaoGJ/rJyuES1iL.zn42G2vHmjfeXrZXuArrUgIR/8SKHSG');
insert into sys_user values(2, 'user',  '$2a$10$rzvwIEtaoGJ/rJyuES1iL.zn42G2vHmjfeXrZXuArrUgIR/8SKHSG');

-- ----------------------------
-- 角色信息表
-- ----------------------------
drop table if exists sys_role;
create table sys_role (
  role_id           bigint(20)      not null auto_increment    comment '角色ID',
  role_name         varchar(30)     not null                   comment '角色名称',
  role_desc         varchar(100)    not null                   comment '角色说明',
  primary key (role_id)
) engine=innodb auto_increment=100 comment = '角色信息表';

-- ----------------------------
-- 初始化-角色信息表数据
-- ----------------------------
insert into sys_role values('1', 'ADMIN', '超级管理员');
insert into sys_role values('2', 'USER',  '普通角色');

-- ----------------------------
-- 权限信息表
-- ----------------------------
drop table if exists sys_perm;
create table sys_perm (
  perm_id           bigint(20)      not null auto_increment    comment '权限ID',
  perm_name         varchar(30)     not null                   comment '权限名称',
  perm_desc         varchar(100)    not null                   comment '权限说明',
  primary key (perm_id)
) engine=innodb auto_increment=100 comment = '权限信息表';

-- ----------------------------
-- 初始化-角色信息表数据
-- ----------------------------
insert into sys_perm values('1', 'admin',  '超级管理员');
insert into sys_perm values('2', 'common', '普通角色');

-- ----------------------------
-- 用户和角色关联表  用户N-1角色
-- ----------------------------
drop table if exists sys_user_role;
create table sys_user_role (
  user_id   bigint(20) not null comment '用户ID',
  role_id   bigint(20) not null comment '角色ID',
  primary key(user_id, role_id)
) engine=innodb comment = '用户和角色关联表';

-- ----------------------------
-- 初始化-用户和角色关联表数据
-- ----------------------------
insert into sys_user_role values ('1', '1');
insert into sys_user_role values ('2', '2');

-- ----------------------------
-- 角色和权限关联表  角色N-1权限
-- ----------------------------
drop table if exists sys_role_perm;
create table sys_role_perm (
   role_id   bigint(20) not null comment '用户ID',
   perm_id   bigint(20) not null comment '权限ID',
   primary key(role_id, perm_id)
) engine=innodb comment = '脚色和权限关联表';

-- ----------------------------
-- 初始化-角色和权限关联表数据
-- ----------------------------
insert into sys_role_perm values ('1', '1');
insert into sys_role_perm values ('2', '2');
