package com.linxz.readlib;

/**
 * Created by 12457 on 2017/8/2.
 */

import android.app.Application;
import android.content.Context;

import com.tencent.smtt.sdk.QbSdk;

/**
 * Created by ljh
 * on 2016/12/22.
 */
public class ReadSdkManager {

    private ReadSdkManager(){}

    private static class ReadSdkManagerHolder{
        private final static ReadSdkManager INSTANCE=new ReadSdkManager();
    }

    public static ReadSdkManager getInstance(){
        return ReadSdkManagerHolder.INSTANCE;
    }

    public void onCreate(Context context) {
        QbSdk.initX5Environment(context,null);
    }


}
