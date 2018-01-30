package com.seasonfif.star.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by seasonfif on 2016/9/12.
 */
public class Utils {

  /**
   * 是否有网络连接
   * @param context
   */
  public static boolean isConnectNet(Context context) {
    ConnectivityManager conManager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (conManager == null) {
      return false;
    } else {
      NetworkInfo[] info = conManager.getAllNetworkInfo();
      if (info != null) {
        for (int i = 0; i < info.length; i++) {
          if (info[i].isConnected()) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * 是否是wifi
   * @param context
   */
  public static boolean isWifi(Context context) {
    ConnectivityManager cm = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkINfo = cm.getActiveNetworkInfo();
    if (networkINfo != null
            && networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
      return true;
    }
    return false;
  }

  /**
   * 关闭输入法
   * @param context
   */
  public static void closeInputMethod(Context context) {
    InputMethodManager inputMethodManager = (InputMethodManager) context
        .getSystemService(Context.INPUT_METHOD_SERVICE);
    inputMethodManager.hideSoftInputFromWindow(((Activity) context)
            .getCurrentFocus().getWindowToken(),
        InputMethodManager.HIDE_NOT_ALWAYS);
  }

  public static String b2gb(int size) {
    if (size < 1024){
      return String.valueOf(size) + "B";
    }else{
      return kb2gb(size / 1024);
    }
  }

  public static String kb2gb(int size) {
    if (size < 1024){
      return String.valueOf(size) + "KB";
    }else{
      size /= 1024;
    }
    if (size < 1024) {
      size = size * 100;
      return String.valueOf((size / 100)) + "."
          + String.valueOf((size % 100)) + "MB";
    } else {
      size = size * 100 / 1024;
      return String.valueOf((size / 100)) + "."
          + String.valueOf((size % 100)) + "GB";
    }
  }

  public static int getScreenWidth(Context context){
    WindowManager manager = ((Activity)context).getWindowManager();
    DisplayMetrics outMetrics = new DisplayMetrics();
    manager.getDefaultDisplay().getMetrics(outMetrics);
    int width = outMetrics.widthPixels;
    int height = outMetrics.heightPixels;
    return width;
  }

  public static int getScreenHeight(Context context){
    WindowManager manager = ((Activity)context).getWindowManager();
    DisplayMetrics outMetrics = new DisplayMetrics();
    manager.getDefaultDisplay().getMetrics(outMetrics);
    int width = outMetrics.widthPixels;
    int height = outMetrics.heightPixels;
    return height;
  }
}
