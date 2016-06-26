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
    private Boolean Explor;
    private Boolean Scheme;
    private Boolean Audit1;
    private Boolean Audit2;
    private Boolean Audit3;
    private Boolean ExternalAuditor;
    private Boolean Implement;
    private Boolean Manage;

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

    public Boolean getExplor() {
        return Explor;
    }

    public void setExplor(Boolean explor) {
        Explor = explor;
    }

    public Boolean getScheme() {
        return Scheme;
    }

    public void setScheme(Boolean scheme) {
        Scheme = scheme;
    }

    public Boolean getAudit1() {
        return Audit1;
    }

    public void setAudit1(Boolean audit1) {
        Audit1 = audit1;
    }

    public Boolean getAudit2() {
        return Audit2;
    }

    public void setAudit2(Boolean audit2) {
        Audit2 = audit2;
    }

    public Boolean getAudit3() {
        return Audit3;
    }

    public void setAudit3(Boolean audit3) {
        Audit3 = audit3;
    }

    public Boolean getExternalAuditor() {
        return ExternalAuditor;
    }

    public void setExternalAuditor(Boolean externalAuditor) {
        ExternalAuditor = externalAuditor;
    }

    public Boolean getImplement() {
        return Implement;
    }

    public void setImplement(Boolean implement) {
        Implement = implement;
    }

    public Boolean getManage() {
        return Manage;
    }

    public void setManage(Boolean manage) {
        Manage = manage;
    }
}