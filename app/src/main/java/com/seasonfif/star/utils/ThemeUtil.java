package com.seasonfif.star.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;

import com.seasonfif.star.MyApplication;
import com.seasonfif.star.R;

/**
 * Created by lxy on 2018/1/27.
 */
public class ThemeUtil {

    public static final String INDIGO = "indigo";
    public static final String RED = "red";
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
            case RED:
                return R.style.App_Red;
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

    public static int getColor(@NonNull Context context, int attr){
        int colorHex;
        Resources resources = context.getResources();
        try {
            colorHex = resources.getColor(getResIdByAttr(context, attr));
        }catch (Resources.NotFoundException e){
            colorHex = R.color.colorAccent_indigo;
        }
        return colorHex;
    }

    public static float getDimension(@NonNull Context context, int attr){
        float colorHex;
        Resources resources = context.getResources();
        try {
            colorHex = resources.getDimension(getResIdByAttr(context, attr));
        }catch (Resources.NotFoundException e){
            colorHex = 0f;
        }
        return colorHex;
    }

    public static int getResIdByAttr(@NonNull Context context, int attr) {
        Resources.Theme theme = context.getTheme();
        TypedArray typedArray = theme.obtainStyledAttributes(new int[]{attr});
        int resId = typedArray.getResourceId(0, -1);
        typedArray.recycle();
        return resId;
    }

    public static Drawable tintDrawable(int res, int color) {
        Drawable dr = MyApplication.INSTANCE.getResources().getDrawable(res);
        final Drawable wrappedDrawable = DrawableCompat.wrap(dr);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }
}
