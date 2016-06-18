package com.lichuange.bridges.views;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by lichuange on 16/4/4.
 */
public class MyToast {
    private static MyToast ourInstance = new MyToast();
    public static MyToast getInstance() {
        return ourInstance;
    }

    public static void showMessage(Context context, String message) {
        if (MyToast.getInstance().getToast() != null) {
            MyToast.getInstance().getToast().cancel();
        }

        Toast toast = Toast.makeText(context,message, Toast.LENGTH_SHORT);
        toast.show();
        MyToast.getInstance().setToast(toast);
    }

    private Toast toast;


    private MyToast() {
    }

    public Toast getToast() {
        return toast;
    }

    public void setToast(Toast toast) {
        this.toast = toast;
    }
}
