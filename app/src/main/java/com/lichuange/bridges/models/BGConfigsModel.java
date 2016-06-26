package com.lichuange.bridges.models;

import java.io.Serializable;

/**
 * Created by lichuange on 16/6/26.
 */
public class BGConfigsModel implements Serializable {
    static public String configFileName = "bg_configs_file";

    private boolean rememberUserName;
    private boolean rememberPassword;
    private String serverAddress;
    private String userName;
    private String password;

    public boolean isRememberUserName() {
        return rememberUserName;
    }

    public void setRememberUserName(boolean rememberUserName) {
        this.rememberUserName = rememberUserName;
    }

    public boolean isRememberPassword() {
        return rememberPassword;
    }

    public void setRememberPassword(boolean rememberPassword) {
        this.rememberPassword = rememberPassword;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
