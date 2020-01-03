package cn.smbms.controller;

import cn.smbms.constant.FileUpLoadConsts;
import cn.smbms.pojo.Role;
import cn.smbms.pojo.User;
import cn.smbms.service.role.RoleService;
import cn.smbms.service.user.UserService;
import cn.smbms.util.Constants;
import cn.smbms.util.PageSupport;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;

    /**
     * 处理请求登录页面
     * @return
     */
    @RequestMapping(path = {"/login","/login.html","/login.jsp"})
    public String login(){
        return "login";
    }

    /**
     * 处理登录请求
     * @param request
     * @param userCode
     * @param userPassword
     * @return
     */
    @RequestMapping(path = "/doLogin", method = RequestMethod.POST)
    public String doLogin(HttpServletRequest request, String userCode, String userPassword){
        // 调用service方法，进行用户匹配
        User user = userService.login(userCode,userPassword);
        if(null != user){//登录成功
            // 存入session
            request.getSession().setAttribute(Constants.USER_SESSION, user);
            // 重定向（frame.jsp）
            return "redirect:/frame";
        }else{
            // 页面跳转（login.jsp）带出提示信息--转发
            request.setAttribute("error", "用户名或密码不正确");
            return "login";
        }
    }

    /**
     * 处理请求后台首页
     * @param session
     * @return
     */
    @RequestMapping(path = {"/frame", "/frame.jsp", "/frame.html"})
    public String frame(HttpSession session){
        // 判断是否登录
        if (session.getAttribute(Constants.USER_SESSION) == null) {
            return "redirect:login.jsp";
        }
        return "frame";
    }

    /**
     * 处理注销请求
     * @param session
     * @return
     */
    @RequestMapping(path = "/logout")
    public String logout(HttpSession session){
        // 清除session
        session.removeAttribute(Constants.USER_SESSION);
        return "redirect:login";
    }

    /**
     * 处理查询用户列表请求
     * @param request
     * @param queryname
     * @param queryUserRole
     * @param pageIndex
     * @return
     */
    @RequestMapping(path = {"/userList", "/userList.jsp", "/userList.html"})
    public String userList(HttpServletRequest request, String queryname,String queryUserRole, String pageIndex){
        //查询用户列表
        String queryUserName = queryname;
        String temp = queryUserRole;
        int userRole = 0;
        List<User> userList = null;
        //设置页面容量
        int pageSize = Constants.pageSize;
        //当前页码
        int currentPageNo = 1;
        System.out.println("queryUserName servlet--------"+queryUserName);
        System.out.println("queryUserRole servlet--------"+userRole);
        System.out.println("query pageIndex--------- > " + pageIndex);
        if(queryUserName == null){
            queryUserName = "";
        }
        if(temp != null && !temp.equals("")){
            userRole = Integer.parseInt(temp);
        }
        if(pageIndex != null){
            try{
                currentPageNo = Integer.valueOf(pageIndex);
            }catch(NumberFormatException e){
                return "redirect:error";
            }
        }
        //总数量（表）
        int totalCount	= userService.getUserCount(queryUserName,userRole);
        //总页数
        PageSupport pages=new PageSupport();
        pages.setCurrentPageNo(currentPageNo);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);
        int totalPageCount = pages.getTotalPageCount();
        //控制首页和尾页
        if(currentPageNo < 1){
            currentPageNo = 1;
        }else if(currentPageNo > totalPageCount){
            currentPageNo = totalPageCount;
        }
        userList = userService.getUserList(queryUserName,userRole,currentPageNo, pageSize);
        request.setAttribute("userList", userList);
        List<Role> roleList = null;
        roleList = roleService.getRoleList();
        request.setAttribute("roleList", roleList);
        request.setAttribute("queryUserName", queryUserName);
        request.setAttribute("queryUserRole", userRole);
        request.setAttribute("totalPageCount", totalPageCount);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("currentPageNo", currentPageNo);
        return "userlist";
    }

    /**
     * 处理添加用户请求
     * @param session
     * @param user
     * @return
     */
    @RequestMapping(path = "/doAddUser")
    public String addUser(HttpSession session, User user,
                          @RequestParam(value = "picPath", required = false)MultipartFile attach){
        System.out.println("UserController================addUser");
        String idPicPath = null;
        if (!attach.isEmpty()) {
            String originalFileName = attach.getOriginalFilename();
            String suffix = FilenameUtils.getExtension(originalFileName);
            if (attach.getSize()> FileUpLoadConsts.fileSize){
                return "useradd";
            } else if (suffix.equalsIgnoreCase("jpg")
                    || suffix.equalsIgnoreCase("png")
                    || suffix.equalsIgnoreCase("jpeg")){
                String fileName = UUID.randomUUID().toString().toUpperCase().replace("-","")+"."+suffix;
                File targetFile = new File(FileUpLoadConsts.path, fileName);
                if (!targetFile.exists()){
                    targetFile.mkdirs();
                }
                try {
                    attach.transferTo(targetFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    return "useradd";
                }
                idPicPath = FileUpLoadConsts.path+File.separator+fileName;
            }else {
                return "useradd";
            }
        }
        user.setCreationDate(new Date());
        user.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        user.setIdPicPath(idPicPath);
        if(userService.add(user)){
            return "redirect:/userList";
        }else{
            return "useradd";
        }
    }

    @RequestMapping(path = {"/addUser", "/addUser.jsp", "/addUser.html"})
    public String addUser(){
        return "useradd";
    }

    /**
     * ajax通过id删除用户
     * @param uid
     */
    @ResponseBody
    @RequestMapping(path = "/deleteUser")
    public String deleteUser(String uid) {
        System.out.println("UserController================deleteUser");
        String id = uid;
        Integer delId = 0;
        try{
            delId = Integer.parseInt(id);
        }catch (Exception e) {
            // TODO: handle exception
            delId = 0;
        }
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(delId <= 0){
            resultMap.put("delResult", "notexist");
        }else{
            if(userService.deleteUserById(delId)){
                resultMap.put("delResult", "true");
            }else{
                resultMap.put("delResult", "false");
            }
        }
        //把resultMap转换成json对象输出
        return JSONArray.toJSONString(resultMap);
    }

    @RequestMapping(path = "/userView/{id}", method = RequestMethod.GET)
    public String userView(Model model, @PathVariable String id){
        System.out.println("UserController================userView");
        if(!StringUtils.isNullOrEmpty(id)){
            //调用后台方法得到user对象
            User user = userService.getUserById(id);
            model.addAttribute("user", user);
            return "userview";
        }
        return "redirect:userList";
    }

    @ResponseBody
    @RequestMapping(path = "/ajaxUserView/{id}", method = RequestMethod.GET)
    public User ajaxUserView(@PathVariable String id){
        System.out.println("UserController================userView");
        if(!StringUtils.isNullOrEmpty(id)){
            //调用后台方法得到user对象
            return userService.getUserById(id);
        }
        return null;
    }

    /**
     * ajax查询用户编码是否可用
     * @param userCode
     * @return
     */
    @ResponseBody
    @RequestMapping(path = "/userCodeExist")
    public String userCodeExist(String userCode){
        System.out.println("UserController================userCodeExist");
        //判断用户账号是否可用
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(StringUtils.isNullOrEmpty(userCode)){
            resultMap.put("userCode", "exist");
        }else{
            User user = userService.selectUserCodeExist(userCode);
            if(null != user){
                resultMap.put("userCode","exist");
            }else{
                resultMap.put("userCode", "notexist");
            }
        }
        return JSONArray.toJSONString(resultMap);
    }

    @RequestMapping(path = "/updatePassword")
    public String updatePassword(){
        return "pwdmodify";
    }

    /**
     * ajax验证旧密码
     * @param session
     * @param oldpassword
     */
    @ResponseBody
    @RequestMapping(path = "/verifyOldPassword")
    public String verifyOldPassword(HttpSession session, String oldpassword) {
        Object o = session.getAttribute(Constants.USER_SESSION);
        Map<String, String> resultMap = new HashMap<String, String>();
        if(null == o ){//session过期
            resultMap.put("result", "sessionerror");
        }else if(StringUtils.isNullOrEmpty(oldpassword)){//旧密码输入为空
            resultMap.put("result", "error");
        }else{
            String sessionPwd = ((User)o).getUserPassword();
            if(oldpassword.equals(sessionPwd)){
                resultMap.put("result", "true");
            }else{//旧密码输入不正确
                resultMap.put("result", "false");
            }
        }
        return JSONArray.toJSONString(resultMap);
    }

    /**
     * 处理修改密码请求
     * @param request
     * @param newpassword
     * @return
     */
    @RequestMapping(path = "/doUpdatePassword")
    public String doUpdatePassword(HttpServletRequest request, String newpassword){
        Object o = request.getSession().getAttribute(Constants.USER_SESSION);
        boolean flag = false;
        if(o != null && !StringUtils.isNullOrEmpty(newpassword)){
            flag = userService.updatePwd(((User)o).getId(),newpassword);
            if(flag){
                request.setAttribute(Constants.SYS_MESSAGE, "修改密码成功,请退出并使用新密码重新登录！");
                return "redirect:logout"; // session注销
            }else{
                request.setAttribute(Constants.SYS_MESSAGE, "修改密码失败！");
            }
        }else{
            request.setAttribute(Constants.SYS_MESSAGE, "修改密码失败！");
        }
        return "redirect:updatePassword";
    }

    /**
     * 处理修改用户页面请求
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(path = "/updateUser/{id}")
    public String updateUser(Model model, @PathVariable String id){
        if(!StringUtils.isNullOrEmpty(id)){
            //调用后台方法得到user对象
            User user = userService.getUserById(id);
            model.addAttribute("user", user);
            return "usermodify";
        }
        return "redirect:userList";
    }

    /**
     * 处理修改用户请求
     * @param session
     * @param user
     * @return
     */
    @RequestMapping(path = "/doUpdateUser")
    public String doUpdateUser(HttpSession session, User user){
        user.setModifyBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        user.setModifyDate(new Date());
        if(userService.modify(user)){
            // 修改成功 重定向
            return "redirect:userList";
        }else{
            // 修改失败返回
            return "updateUser";
        }
    }
}
