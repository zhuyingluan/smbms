package cn.smbms.mapper;

import cn.smbms.pojo.Role;

import java.util.List;

public interface RoleMapper {
    /**
     * 获取角色列表
     * @return
     */
    List<Role> getRoleList();
}
