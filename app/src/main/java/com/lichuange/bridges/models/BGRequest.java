package com.lichuange.bridges.models;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.lichuange.bridges.models.BGResponse;

/**
 * Created by lichuange on 16/6/18.
 */
public class BGRequest implements BGResponse {


    public void success(MsgResponseBase res) {}
    public void failed(MsgResponseBase res) {}

    public static final int RESPONSE_SUCCESS = 0x1001;
    public static final int RESPONSE_FAILED = 0x1002;

    private OkHttpClient httpClient;
    private String path;
    FormBody.Builder bodyBuider;

    public BGRequest() {
        httpClient = new OkHttpClient();
        bodyBuider = new FormBody.Builder();
    }


    public BGRequest addParam(String paramKey, String paramValue) {
        Log.i("{BG.Request}", "add param " + paramKey + " : " + paramValue);
        bodyBuider.add(paramKey, paramValue);
        return this;
    }

    private String responsePrevProcess(String inString) {
        if (inString != null && inString.length() > 0) {
            if (inString.startsWith("(")) {
                inString = inString.substring(1);
                if (inString.endsWith(")")) {
                    inString = inString.substring(0, inString.length() - 1);
                }
            }
            String outString = inString.replace("\\\"", "\"");
            return outString;
        }
        else {
            return "";
        }
    }

    public boolean send(String url, final Type typeOfT) {
        FormBody body = bodyBuider.build();
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();


        Runnable networkTask = new Runnable() {

            @Override
            public void run() {
                try {
                    Response response = httpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();

                        Log.i("{BG.Request}", "received response = " + responseStr);

                        responseStr = responsePrevProcess(responseStr);

                        Gson gson = new Gson();
                        MsgResponseBase res = gson.fromJson(responseStr, (Type) typeOfT);

                        if (res != null && res.isSuccess()) {
                            success(res);
                        }
                        else {
                            failed(res);
                        }
                    }
                    else {
                        failed(null);
                    }
                }
                catch (IOException e) {
                    failed(null);
                }
            }
        };

        new Thread(networkTask).start();

        Log.i("{BG.Request}", "sending msg " + url);

        return true;
    }
}
