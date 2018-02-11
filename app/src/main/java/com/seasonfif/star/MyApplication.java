package com.seasonfif.star;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.ImageView;
import com.amitshekhar.DebugDB;
import com.seasonfif.star.database.GreenDaoManager;
import com.seasonfif.star.ui.activity.SplashActivity;
import com.seasonfif.star.utils.LauncherUtil;
import com.squareup.picasso.Picasso;

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
        GreenDaoManager.getInstance();
        DebugDB.getAddressLog();

        createShortCut();
    }

    private void createShortCut(){

        String appName = getResources().getString(R.string.app_name);
        if (!LauncherUtil.isShortCutExist(this, appName)){
            Intent intent = new Intent(getApplicationContext() , SplashActivity.class);
            //以下两句是为了在卸载应用的时候同时删除桌面快捷方式
            intent.setAction("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.LAUNCHER");

            //创建快捷方式的Intent
            Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            //不允许重复创建
            shortcutintent.putExtra("duplicate", false);
            //需要现实的名称
            shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
            //快捷图片
            Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher);
            shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
            //点击快捷图片，运行的程序主入口
            shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
            //发送广播。OK
            sendBroadcast(shortcutintent);
        }
    }

    public void loadPicture(String url, ImageView view){
        if (TextUtils.isEmpty(url)) {
            url = "";
        }
        Picasso.with(this).load(url).placeholder(R.drawable.image_placeholder).into(view);
    }

    public void loadAvatar(String url, ImageView view){
        if (TextUtils.isEmpty(url)) {
            url = "";
        }
        Picasso.with(this).load(url).placeholder(R.drawable.avatar_holder).into(view);
    }
}
