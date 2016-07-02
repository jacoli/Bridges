package com.lichuange.bridges.models;

import android.content.ContextWrapper;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by lichuange on 16/6/26.
 */
public class BGConfigsModel implements Serializable {
    static public String configFileName = "bg_configs_file";

    private boolean rememberUserName;
    private boolean rememberPassword;
    private String serverAddress;
    private String serverPort;
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



    public void persist(ContextWrapper contextWrapper) {
        try {
            FileOutputStream stream = contextWrapper.openFileOutput(BGConfigsModel.configFileName, ContextWrapper.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(stream);
            oos.writeObject(this);
        }
        catch (Exception e) {
            Log.i("MainService", e.toString());
        }
        finally {
        }
    }

    public static BGConfigsModel fetch(ContextWrapper contextWrapper) {
        BGConfigsModel configsModel = null;
        try {
            FileInputStream stream = contextWrapper.openFileInput(BGConfigsModel.configFileName);
            ObjectInputStream ois = new ObjectInputStream(stream);
            configsModel = (BGConfigsModel)ois.readObject();
        }
        catch (Exception e) {
            Log.i("MainService", e.toString());
        }
        finally {
        }

        if (configsModel == null) {
            configsModel = new BGConfigsModel();
            configsModel.setRememberUserName(true);
            configsModel.setRememberPassword(false);
            configsModel.setServerAddress("139.196.200.114");
            configsModel.setServerPort("8888");
        }

        return configsModel;

    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }
}
