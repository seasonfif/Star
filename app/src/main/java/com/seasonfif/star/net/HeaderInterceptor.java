package com.seasonfif.star.net;

import android.text.TextUtils;
import android.util.Log;

import com.seasonfif.star.MyApplication;
import com.seasonfif.star.utils.OAuthShared;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 创建时间：2016年09月05日16:35 <br>
 * 作者：zhangqiang <br>
 * 描述：Http报文头信息
 */
public class HeaderInterceptor implements Interceptor {

  private static String TAG = HeaderInterceptor.class.getSimpleName();
  public HeaderInterceptor() {
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    String token = OAuthShared.getToken(MyApplication.INSTANCE);
    Request.Builder builder = chain.request()
        .newBuilder()
        .header("Accept", "application/vnd.github.v3+json")
        .addHeader("User-Agent", "android")
        .addHeader("Seasonfif-App-Id", "Seasonfif/Github");

    if (TextUtils.isEmpty(token)) {
      Log.e(TAG, String.format("当前请求地址:%s Token is null，更多详细信息:%s",
          chain.request().url(), chain.request().toString()));
    } else {
      builder.addHeader("Authorization", "token " + token);
    }

    return chain.proceed(builder.build());
  }
}
