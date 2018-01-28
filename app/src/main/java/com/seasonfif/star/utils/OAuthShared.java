package com.seasonfif.star.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.seasonfif.star.utils.safeSP.SharedWrapper;

/**
 * 创建时间：2016年09月05日19:20 <br>
 * 作者：zhangqiang <br>
 * 描述：认证Token保存
 */
public final class OAuthShared {

  private static final String TAG = "OAuthShared";

  private static final String KEY_OAUTH_TOKEN = "oauth_token";
  private static final String KEY_MY_AVATAR = "my_avatar";

  public static String getToken(@NonNull Context context){
    return SharedWrapper.with(context, TAG).getString(KEY_OAUTH_TOKEN, "");
  }

  public static void saveToken(@NonNull Context context, String token){
    SharedWrapper.with(context, TAG).setString(KEY_OAUTH_TOKEN, token);
  }

  public static String getAvatar(@NonNull Context context){
    return SharedWrapper.with(context, TAG).getString(KEY_MY_AVATAR, "");
  }

  public static void saveAvatar(@NonNull Context context, String avatar){
    SharedWrapper.with(context, TAG).setString(KEY_MY_AVATAR, avatar);
  }
}
