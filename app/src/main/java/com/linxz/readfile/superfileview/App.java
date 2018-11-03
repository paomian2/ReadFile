package com.linxz.readfile.superfileview;

import android.app.Application;

import com.linxz.readlib.ReadSdkManager;

/**
 * <p>
 * Function： TODO
 * <p>
 * ver     date      		author
 * ──────────────────────────────────
 * V1.0   2018年11月03日18:04  lin_xiao_zhang@163.com
 * <p>
 * Copyright (c) 2018,  All Rights Reserved.
 *
 * @author linxz
 */
public class App extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        ReadSdkManager.getInstance().onCreate(this);
    }
}
