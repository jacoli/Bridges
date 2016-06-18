package com.lichuange.bridges.models;

/**
 * Created by lichuange on 16/3/31.
 */
public class MsgResponseBase {
    private int Status;
    private String Msg;

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

    public boolean isSuccess() {
        return Status == 0;
    }
}
