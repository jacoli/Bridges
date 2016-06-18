package com.lichuange.bridges.views;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.HttpAuthHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient {

    // 打开链接前的事件
    // 返回: return true; webview处理url是根据程序来执行的。
    // 返回: return false; webview处理url是在webview内部执行。
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            Log.i("niu.com.cn", Uri.parse(url).getHost());
//            view.loadUrl(url);
//            return true;


        return super.shouldOverrideUrlLoading(view, url);


        // True if the host application wants to leave the current WebView and
        // handle the url itself, otherwise return false.
//        if (Uri.parse(url).getHost().equals("192.168.0.102")) {
//            // 站内网页
//            Log.i("192.168.0.102", Uri.parse(url).getHost());
//            view.loadUrl(url);
//            return true;
//        } else if (Uri.parse(url).getHost().equals("niutv.com.cn")) {
//            Log.i("niu.com.cn", Uri.parse(url).getHost());
//            view.loadUrl(url);
//            return true;
//        } else {
//            // 默认处理
//            return super.shouldOverrideUrlLoading(view, url);
//        }
    }

    // 载入页面完成的事件
    @Override
    public void onPageFinished(WebView view, String url) {
        // TODO Auto-generated method stub
        Log.i("WebViewClient", "onPageFinished");
        super.onPageFinished(view, url);
    }

    // 载入页面开始的事件
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        // TODO Auto-generated method stub
        Log.i("WebViewClient", "onPageStarted");
        super.onPageStarted(view, url, favicon);
    }

    // 接收到Http请求的事件
    @Override
    public void onReceivedHttpAuthRequest(WebView view,
                                          HttpAuthHandler handler, String host, String realm) {
        // TODO Auto-generated method stub
        Log.i("WebViewClient", "onReceivedHttpAuthRequest");
        super.onReceivedHttpAuthRequest(view, handler, host, realm);
    }


}
