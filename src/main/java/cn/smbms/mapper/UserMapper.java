package cn.smbms.mapper;

import cn.smbms.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    /**
     * 添加用户
     * @param user
     * @return
     */
    Integer addUser(User user);

    /**
     * 根据id删除用户
     * @param id
     * @return
     */
    Integer deleteUserById(Integer id);

    /**
     * 根据id修改用户
     * @param user
     * @return
     */
    Integer updateUserById(User user);

    /**
     * 根据id修改密码
     * @param id
     * @param password
     * @return
     */
    Integer updatePasswordById(@Param("id") Integer id, @Param("password") String password);

    /**
     * 根据id获取用户
     * @param id
     * @return
     */
    User getUserById(String id);

    /**
     * 根据用户编码获取用户
     * @param userCode
     * @return
     */
    User getUserByUserCode(String userCode);

    /**
     * 根据参数获取用户总数
     * @param userName
     * @param userRole
     * @return
     */
    Integer getUserListCount(@Param("userName") String userName, @Param("userRole") Integer userRole);

    /**
     * 根据参数分页获取用户列表
     * @param userName
     * @param userRole
     * @param startIndex
     * @param pageSize
     * @return
     */
    List<User> getUserList(@Param("userName") String userName,
                           @Param("userRole") Integer userRole,
                           @Param("startIndex") Integer startIndex,
                           @Param("pageSize") Integer pageSize);
}
