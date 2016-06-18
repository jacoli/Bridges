package com.lichuange.bridges.models;

import android.app.Activity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lichuange on 16/4/9.
 */
public class Utils {

    static public boolean isCurrentTimeExpired(String expiredTime) {
        if (expiredTime == null || expiredTime.length() < "yyyy-MM-dd HH:mm:ss".length()) {
            return true; // 默认为过期。
        }

        //设定时间的模板
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        boolean isExpired = true;

        try {
            Date expireDate = sdf.parse(expiredTime);
            Date curDate = new Date();
            isExpired = (expireDate.getTime() - curDate.getTime() < 0);
        }
        catch (ParseException e){
        }
        finally {
        }

        return isExpired;
    }

    static public String getUniqueModelId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
        Date curDate = new Date();
        return sdf.format(curDate);
    }

    static public String getCurrentDateStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date();
        return sdf.format(curDate);
    }

    static public long convertStackNumberToDistance(String stackNumber) {
        long distance = -1;

        if (stackNumber != null && stackNumber.length() > 0) {
            if (stackNumber.startsWith("K") || stackNumber.startsWith("k")) {
                stackNumber = stackNumber.substring(1);

                int indexOfPlus = stackNumber.indexOf('+');
                if (indexOfPlus >= 0) {
                    String aStr = stackNumber.substring(0, indexOfPlus);
                    String bStr = stackNumber.substring(indexOfPlus + 1, stackNumber.length());

                    if (aStr.length() == 0) {
                        aStr = "0";
                    }

                    if (bStr.length() == 0) {
                        bStr = "0";
                    }

                    try {
                        long a = Long.valueOf(aStr);
                        long b = Long.valueOf(bStr);

                        distance = a * 1000 + b;
                    }
                    catch (Exception e) {
                        Log.e("", e.toString());
                    }
                }
            }
        }

        return distance;
    }
}
