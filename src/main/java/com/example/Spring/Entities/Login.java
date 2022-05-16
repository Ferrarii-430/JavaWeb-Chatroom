package com.example.Spring.Entities;


import java.util.Date;
import java.util.Set;

/**
 * 登录实体
 */
public class Login {
    /**
     * 编号
     */
    String UserID;
    /**
     * 手机号码
     */
    String Phone;
    /**
     * 昵称
     */
    String NickName;
    /**
     * 用户状态
     */
    int State;
    /**
     * 密码
     */
    private String PassWord;

    public Login(String userID, String phone, String nickName,String passWord,int state) {
           this.UserID=userID;
           this.Phone=phone;
           this.PassWord=passWord;
           this.NickName=nickName;
           this.State=state;
    }

    public Login() {

    }

    public String getNickName() {
        return NickName;
    }

    public String getPassWord() {
        return PassWord;
    }

    public String getUserID() {
        return UserID;
    }

    public String getPhone() {
        return Phone;
    }

    public int  getState() {
        return State;
    }

}
