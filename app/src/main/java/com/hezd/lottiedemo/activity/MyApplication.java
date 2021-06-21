package com.hezd.lottiedemo.activity;

import android.app.Application;

import com.hezd.lottiedemo.CalculateUtils;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CalculateUtils.initPython(this);
    }
}
