package com.seasonfif.star.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 创建时间：2016年09月08日15:22 <br>
 * 作者：zhangqiang <br>
 * 描述：时间转换工具类
 */
public class DateUtils {

  private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
  public static SimpleDateFormat sdfyyyy_MM = new SimpleDateFormat("yyyy-MM");
  public static SimpleDateFormat sdfyyyy_MM_CH = new SimpleDateFormat("yyyy年MM月");
  public static SimpleDateFormat sdfyyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
  public static SimpleDateFormat sdfyyyy_MM_dd_slash = new SimpleDateFormat("yyyy/MM/dd");
  public static SimpleDateFormat sdfyyyy_MM_dd_hm_slash = new SimpleDateFormat("yyyy/MM/dd HH:mm");
  public static SimpleDateFormat sdfyyyy_MM_dd_ch = new SimpleDateFormat("yyyy年MM月dd日");

  public static Date getDate(String date) {
    try {
      return format.parse(date);
    } catch (ParseException e) {
      return new Date();
    }
  }

  public static int fromNow(String date){
    Date from = getDate(date);
    Calendar cal = Calendar.getInstance();
    cal.setTime(from);
    long time1 = cal.getTimeInMillis();
    cal.setTime(new Date());
    long time2 = cal.getTimeInMillis();
    long between_days=(time2-time1)/(1000*60);

    return Integer.parseInt(String.valueOf(between_days));
  }

  public static String distance(String time){
    String distance = null;
    int minute = fromNow(time);
    int hours = minute/60;
    int days = hours/24;
    int months = days/30;
    int years = months/12;
    if(years == 1){
      distance = years+" year ago";
    }
    else if(years > 1){
      distance = years+" years ago";
    }
    else if(months == 1){
      distance = months+" month ago";
    }
    else if(months > 1){
      distance = months+" months ago";
    }
    else if(days == 1){
      distance = days+" day ago";
    }
    else if(days > 1){
      distance = days+" days ago";
    }
    else if(hours > 1){
      distance = hours + " hours ago";
    }
    else{
      distance = minute + " minutes ago";
    }

    return distance;
  }
}
