package com.lichuange.bridges.models;

/**
 * Created by lichuange on 16/6/18.
 */
public interface BGResponse {
    public void success(MsgResponseBase res);
    public void failed(MsgResponseBase res);
}
