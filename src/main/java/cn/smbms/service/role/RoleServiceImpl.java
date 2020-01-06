package cn.smbms.service.role;

import cn.smbms.dao.RoleMapper;
import cn.smbms.pojo.Role;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService{
	@Resource
	private RoleMapper roleMapper;

	@Override
	public List<Role> getRoleList() {
		List<Role> roleList = null;
		try {
			roleList = roleMapper.getRoleList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return roleList;
	}
	
}
