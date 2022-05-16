package com.example.Spring.Entities;

public class UserData {
    int UserID;
    String Phone;
    String NickName;
    String Email;
    String Password;
    int State;

    public int getUserID() {
        return UserID;
    }

    public String getNickName() {
        return NickName;
    }

    public String getPhone() {
        return Phone;
    }

    public String getPassword() {
        return Password;
    }

    public String getEmail() {
        return Email;
    }

    public int getState() {
        return State;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setState(int state) {
        State = state;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }
}
