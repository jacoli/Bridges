package com.lichuange.bridges.models;

import android.app.Activity;
import android.content.SharedPreferences;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by lichuange on 16/3/27.
 */
public class LoginModel implements Serializable {
    private int Status;
    private String Msg;
    private String token;
    private String ExpirDate;
    private String Explor;
    private String Implement;
    private String Manage;

    private String userName;

    public boolean isLoginSuccess() {
        return Status == 0
                && token.length() > 0;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpirDate() {
        return ExpirDate;
    }

    public void setExpirDate(String expirDate) {
        ExpirDate = expirDate;
    }

    public String getExplor() {
        return Explor;
    }

    public void setExplor(String explor) {
        Explor = explor;
    }

    public String getImplement() {
        return Implement;
    }

    public void setImplement(String implement) {
        Implement = implement;
    }

    public String getManage() {
        return Manage;
    }

    public void setManage(String manage) {
        Manage = manage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}