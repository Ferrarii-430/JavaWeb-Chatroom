package com.example.Spring.DAO;

import com.example.Spring.Entities.UserData;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * 处理用户注册接口
 */
public interface RegisterMapper{

    /**
     * 用户注册
     */
    int register_adduser(@Param("phone")String phone,@Param("nickname")String nickname,@Param("email")String email,@Param("password")String password);

    /**
     * 用户注册
     */
    void register_adduser_class(UserData userData);

    /**
     * 添加数据前查重
     */
    int repeat_check(@Param("phone")String phone,@Param("nickname")String nickname,@Param("email")String email);


    /**
     * 添加数据前查重
     */
    int repeat_check_no_phone(@Param("nickname")String nickname,@Param("email")String email);

    /**
     * 查询邮箱是否重复
     */
    int repeat_check_to_email(@Param("email")String email);
}