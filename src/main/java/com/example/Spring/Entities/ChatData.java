package com.example.Spring.Entities;


import org.springframework.lang.NonNull;

/**
 * 单体聊天数据实体   暂时好像没啥用
 */
public class ChatData {
    /**
     * 发送者ID
     */
    @NonNull
     String SendID;

    /**
     * 接收接ID
     */
    @NonNull
     String ReceiptID;

    /**
     * 聊天数据
     */
    @NonNull
     String Txtdate;

    /**
     * 时间
     */
    @NonNull
    String Time;

    public ChatData(String sendID, String receiptID, String txtdate, String time){
        this.SendID=sendID;
        this.ReceiptID=receiptID;
        this.Txtdate=txtdate;
        this.Time=time;
    }

    public String getTxtdate() {
        return Txtdate;
    }

    public String getSendID() {
        return SendID;
    }

    public String getReceiptID() {
        return ReceiptID;
    }

    public String getTime() {
        return Time;
    }
}
