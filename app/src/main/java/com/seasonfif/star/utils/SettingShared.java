package com.seasonfif.star.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import com.seasonfif.star.utils.safeSP.SharedWrapper;

public final class SettingShared {

    private SettingShared() {}

    private static final String TAG = "SettingShared";

    private static final String KEY_THEME = "theme";
    private static final String KEY_HIDE_AVATAR = "hide_avatar";
    private static final String KEY_OPEN_WITH = "open_with";
    private static final String KEY_ENABLE_THEME_DARK = "enableThemeDark";
    private static final String KEY_HAS_NEW_VERSION = "has_new_version";
    private static final String KEY_ENABLE_NOTIFICATION = "enableNotification";

    public static String getTheme(@NonNull Context context, String indigo){
        return SharedWrapper.with(context, TAG).getString(KEY_THEME, indigo);
    }

    public static void setTheme(@NonNull Context context, String theme){
        SharedWrapper.with(context, TAG).setString(KEY_THEME, theme);
    }

    public static boolean isHideAvatar(@NonNull Context context) {
        return SharedWrapper.with(context, TAG).getBoolean(KEY_HIDE_AVATAR, false);
    }

    public static void setHideAvatar(@NonNull Context context, boolean enable) {
        SharedWrapper.with(context, TAG).setBoolean(KEY_HIDE_AVATAR, enable);
    }

    public static boolean isOpenWithGithub(@NonNull Context context) {
        return SharedWrapper.with(context, TAG).getBoolean(KEY_OPEN_WITH, false);
    }

    public static void setOpenWithGithub(@NonNull Context context, boolean enable) {
        SharedWrapper.with(context, TAG).setBoolean(KEY_OPEN_WITH, enable);
    }

    public static boolean hasNewVersion(@NonNull Context context) {
        return SharedWrapper.with(context, TAG).getBoolean(KEY_HAS_NEW_VERSION, false);
    }

    public static void setNewVersion(@NonNull Context context, boolean enable) {
        SharedWrapper.with(context, TAG).setBoolean(KEY_HAS_NEW_VERSION, enable);
    }

    public static boolean isEnableNotification(@NonNull Context context) {
        return SharedWrapper.with(context, TAG).getBoolean(KEY_ENABLE_NOTIFICATION, true);
    }

    public static void setEnableNotification(@NonNull Context context, boolean enable) {
        SharedWrapper.with(context, TAG).setBoolean(KEY_ENABLE_NOTIFICATION, enable);
    }

}
