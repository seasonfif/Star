package com.seasonfif.star.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.text.TextUtils;

import com.seasonfif.star.R;

/**
 * Created by lxy on 2018/1/27.
 */
public class ThemeUtil {

    public static final String INDIGO = "indigo";
    public static final String CYAN = "cyan";
    public static final String BLACK = "black";

    private static String getCurrentTheme(Context ctx){
        return SettingShared.getTheme(ctx, INDIGO);
    }

    public static void changeTheme(Context ctx, String theme){
        if (TextUtils.isEmpty(theme)) return;
        String preTheme = getCurrentTheme(ctx);
        if (!theme.equalsIgnoreCase(preTheme)){
            SettingShared.setTheme(ctx, theme);
            recreate((Activity) ctx);
        }
    }

    @StyleRes
    public static int getTheme(Context ctx) {
        String theme = getCurrentTheme(ctx);
        switch (theme){
            case INDIGO:
                return R.style.App_Indigo;
            case CYAN:
                return R.style.App_Cyan;
            case BLACK:
                return R.style.App_Black;
            default:
                return R.style.App_Indigo;
        }
    }

    public static void recreate(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            activity.recreate();
        } else {
            Intent intent = activity.getIntent();
            intent.setClass(activity, activity.getClass());
            activity.startActivity(intent);
            activity.finish();
            activity.overridePendingTransition(0, 0);
        }
    }
}
