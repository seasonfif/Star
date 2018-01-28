package com.seasonfif.star.constant;

/**
 * 创建时间：2016年09月07日15:25 <br>
 * 作者：zhangqiang <br>
 * 描述：常量
 */
public class Constants {

  public final static String API_URL = "https://api.github.com";
  public final static String DB_NAME_STAR = "my_star";

  public final static String CLIENT_ID = "17ff3dcc7b25df9df856";
  public final static String CLIENT_SECRET = "4e03426b6a9250c1c7f833e7c6907307d56641d8";

  public final static int PER_PAGE = 10;

  public final static String LOGIN = "login";

  public final static String OBJECT = "object";

  public static final String COLUMN = "column";

  public static final String PAGE_INDEX = "page_index";

  public static final String THEME = "com.seasonfif.github.theme_change";
  public static final String APP_EXIT = "com.seasonfif.github.app_exit";
  public static final String REFRESH_EXPLORE = "com.seasonfif.github.refresh_explore";
  public static final String PRIVATE = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5D4DOHtZ8NMVLOj59HMHOVpQKeLIbuS9fUIzZf27J5n8rIRF0gl7zg1pMN//9l1nSb7T1oYh1jiiFHfITIY3xvphasz/6eOl5ruybOwVOiKqCJTSXM6tdZD+nlSL1uHDMugfQU9QlhUe3v8tNx7aqmiQrllgWalxDprFXZSg04h1r5wGrfwxgnFOq1XaelgcfOP6bZasVU4EHv01gpNnWquIgXvGjpsW3nThN99i7g6d0KYhDYxM3lF8WxoV+jYr1Gla2jFKHHJRiEK4YCgB8lpSvWuPVQ+ERfFK31bdbUNIi4/21NEP6ZXip99Iysn8MCtRNqE7u7yAh82VIHY+AQIDAQAB";

  public interface Channel{
    String BAIDU = "baidu";
    String TENCENT = "tencent";
    String C360 = "c360";
    String XIAOMI = "xiaomi";
    String GOOGLE = "google";
    String GOOGLEPRO = "googlepro";
  }

  public interface FileSuffix{
    String IMAGE_PNG = ".png";
    String IMAGE_JPG = ".jpg";
    String IMAGE_JPEG = ".jpeg";
    String IMAGE_GIF = ".gif";
    String MARKDOWN = ".md";
    String APK = ".apk";
  }
}
