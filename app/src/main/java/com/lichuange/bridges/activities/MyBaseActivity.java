package com.lichuange.bridges.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.lichuange.bridges.R;
import com.lichuange.bridges.views.TitleBar;

import java.lang.ref.WeakReference;

public class MyBaseActivity extends Activity {

    static class MyHandler extends Handler {
        WeakReference<MyBaseActivity> weakActivity;

        public MyHandler(MyBaseActivity fragment){
            weakActivity = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (weakActivity != null) {
                weakActivity.get().onResponse(msg.what);
                weakActivity.get().onResponse(msg.what, msg.obj);
            }
        }
    }

    public MyHandler handler = new MyHandler(this);
    public TitleBar titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void createTitleBar() {
        if (hasKitKat() && !hasLollipop()) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        else if (hasLollipop()) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        titleBar = (TitleBar) findViewById(R.id.title_bar);

        titleBar.setImmersive(true);

        titleBar.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.bg_title_bar));

        titleBar.setLeftImageResource(R.mipmap.back_green);
        //titleBar.setLeftText("返回");
        titleBar.setLeftTextColor(Color.WHITE);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //titleBar.setTitle("文章详情\n副标题");
        titleBar.setTitleColor(Color.WHITE);
        titleBar.setSubTitleColor(Color.GREEN);
        titleBar.setDividerColor(Color.GRAY);
    }

    public void onResponse(int msgCode) {
    }

    public void onResponse(int msgCode, Object obj) {
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
