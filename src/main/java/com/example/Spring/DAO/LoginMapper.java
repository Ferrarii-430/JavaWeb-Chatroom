package com.example.Spring.DAO;
import com.example.Spring.Entities.Login;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 登录验证接口
 */

public interface LoginMapper {

    /**
     * 登录查询
     */
    Login IDLogin(@Param("ID") String id,@Param("PassWord") String password);

    /**
     * 判断用户状态
     */
    int Judgment(@Param("ID") String id);

    /**
     * 查找用户真实ID
     */
    int getUserID(@Param("ID") String id);
    /**
     * 查找用户真实昵称
     */
    String getNickname(@Param("ID") String id);

    /**
     * 根据用户ID List 查找用户真实昵称
     */
    List getNickname_list(@Param("List") List list);

    /**
     * 修改用户的状态 500
     */
    void setUserJudgment_500(@Param("ID") String id);

    /**
     * 修改用户的状态 200
     */
    void setUserJudgment_200(@Param("ID") String id);

    /**
     * 根据用户ID List 查找用户真实昵称  这个比较特殊，查询的表为tourist   暂时弃用
     */
    List getTourist_list();

    /**
     * 修查询多个用户状态 多用于游客账号的使用
     */
    List getJudgment_list(@Param("List") List list);
}
