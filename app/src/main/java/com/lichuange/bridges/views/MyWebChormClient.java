package com.lichuange.bridges.views;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class MyWebChormClient extends WebChromeClient {

    public static final String LOG_TAG = "MyChormClient";

    @Override
    public boolean onJsAlert(WebView view, String url, String message,
                             JsResult result) {
        // TODO Auto-generated method stub
        Log.i(LOG_TAG, "onJsAlert");
        Log.i("message", message);
        return super.onJsAlert(view, url, message, result);
    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message,
                                    JsResult result) {
        // TODO Auto-generated method stub
        Log.i(LOG_TAG, "onJsBeforeUnload");
        return super.onJsBeforeUnload(view, url, message, result);
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message,
                               JsResult result) {
        // TODO Auto-generated method stub
        Log.i(LOG_TAG, "onJsConfirm");
        Log.i("message", message);
        return super.onJsConfirm(view, url, message, result);
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message,
                              String defaultValue, JsPromptResult result) {
        // TODO Auto-generated method stub
        Log.i(LOG_TAG, "onJsPrompt");
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    @Override
    public boolean onJsTimeout() {
        // TODO Auto-generated method stub
        Log.i(LOG_TAG, "onJsPrompt");
        return super.onJsTimeout();
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        // TODO Auto-generated method stub
        Log.i(LOG_TAG, "--onProgressChanged--");
        if (newProgress == 100) {
            System.out.println("页面加载完成");
        }
        super.onProgressChanged(view, newProgress);
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        // TODO Auto-generated method stub
        Log.i(LOG_TAG, "onReceivedIcon");
        super.onReceivedIcon(view, icon);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        // TODO Auto-generated method stub
        Log.i(LOG_TAG, "onReceivedTitle");
        super.onReceivedTitle(view, title);
    }

    @Override
    public void onRequestFocus(WebView view) {
        // TODO Auto-generated method stub
        Log.i(LOG_TAG, "onRequestFocus");
        super.onRequestFocus(view);
    }
}
