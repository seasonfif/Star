package com.seasonfif.star.net;

import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seasonfif.star.constant.Constants;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 创建时间：2016年09月05日15:36 <br>
 * 作者：zhangqiang <br>
 * 描述：Retrofit网络请求引擎
 */
public class RetrofitEngine {

  public static final int CONNECT_TIMEOUT_MILLIS = 30 * 1000;
  public static final int READ_TIMEOUT_MILLIS = 30 * 1000;

  public static Retrofit getRetrofitWithBaseToken(String baseToken){

    Gson gson = new GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .create();
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(Constants.API_URL)
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(initOkHttpClient(baseToken)).build();
    return retrofit;
  }

  public static Retrofit getRetrofit(){
    Gson gson = new GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .create();
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(Constants.API_URL)
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(initOkHttpClient(null)).build();
    return retrofit;
  }

  private static OkHttpClient initOkHttpClient(String baseToken) {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    builder.connectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
    builder.readTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
    builder.writeTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
    if (TextUtils.isEmpty(baseToken)){
      builder.addInterceptor(new HeaderInterceptor());
    }else{
      builder.addInterceptor(new LoginInterceptor(baseToken));
    }
    return builder.build();
  }
}
