package com.example.Spring.DAO;

import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface FriendMapper {

    /**
     * 查询好友关系列表
     */
    List<String> friend_list(@Param("ID")String id);

    /**
     * 添加好友关系
     */
    void add_friendRelationship(@Param("UserID")String userid,@Param("ReceiveID")String receiveid);

}
