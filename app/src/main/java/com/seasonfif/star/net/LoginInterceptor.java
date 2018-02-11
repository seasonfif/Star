package com.seasonfif.star.net;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 创建时间：2016年09月05日16:35 <br>
 * 作者：zhangqiang <br>
 * 描述：Http报文头信息
 */
public class LoginInterceptor implements Interceptor {

  private static String TAG = LoginInterceptor.class.getSimpleName();
  private String baseToken;

  public LoginInterceptor(String baseToken) {
    this.baseToken = baseToken;
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request.Builder builder = chain.request()
        .newBuilder()
        .header("Accept", "application/vnd.github.v3+json")
        .addHeader("User-Agent", "android")
        .addHeader("Seasonfif-App-Id", "seasonfif/Star")
        .addHeader("Authorization", baseToken.trim());
    return chain.proceed(builder.build());
  }
}
