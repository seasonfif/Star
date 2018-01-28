package com.seasonfif.star;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.amitshekhar.DebugDB;
import com.seasonfif.star.database.GreenDaoManager;
import com.seasonfif.star.utils.OAuthShared;
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
        OAuthShared.saveAvatar(this, "51a3eb73f6554123c636d1c0b2d1035df531ae91");
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
