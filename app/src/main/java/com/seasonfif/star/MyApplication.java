package com.seasonfif.star;

import android.app.Application;
import android.content.Context;

/**
 * Created by lxy on 2018/1/27.
 */

public class MyApplication extends Application {

    public static MyApplication INSTANCE;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        INSTANCE = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
