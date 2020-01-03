package cn.smbms.controller;

import cn.smbms.pojo.Role;
import cn.smbms.service.role.RoleService;
import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
public class RoleController {
    @Resource
    private RoleService roleService;

    @ResponseBody
    @RequestMapping(path = "/roleList")
    public String roleList(HttpServletResponse response){
        List<Role> roleList = null;
        roleList = roleService.getRoleList();
        return JSONArray.toJSONString(roleList);
    }
}
