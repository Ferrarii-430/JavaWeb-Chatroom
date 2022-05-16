package com.example.Spring.DAO;


import com.example.Spring.Entities.ChatData;

/**
 * 处理聊天数据接口
 */
public interface ChatDateMapper {

    /**
     * 甚是大方，居然帮他们保存聊天数据
     */
    int add(ChatData entity);
}
