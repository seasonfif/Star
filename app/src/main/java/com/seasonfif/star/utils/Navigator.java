package com.seasonfif.star.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.seasonfif.star.R;
import com.seasonfif.star.model.Repository;

/**
 * Created by zhangqiang on 2018/1/29.
 */

public class Navigator {

  public static void openRepoProfile(Activity activity, Repository repository){
    if (SettingShared.isOpenWithGithub(activity)){
      openRepoProfileWithHub(activity, repository);
    }else{
      openRepoProfileWithBrowser(activity, repository);
    }
  }

  public static void openUserProfile(Activity activity, String name){
    if (SettingShared.isOpenWithGithub(activity)){
      openUserProfileWithHub(activity, name);
    }else{
      openUserProfileWithBrowser(activity, name);
    }
  }

  private static void openRepoProfileWithHub(Activity activity, Repository repository){
    Intent it = new Intent();
    it.setAction("com.seasonfif.github.repo.detail");
    it.putExtra("owner", repository.login);
    it.putExtra("repoName", repository.name);
    activity.startActivity(it);
  }

  private static void openUserProfileWithHub(Activity activity, String name){
    Intent it = new Intent();
    it.setAction("com.seasonfif.github.user.detail");
    it.putExtra("name", name);
    activity.startActivity(it);
  }

  private static void openRepoProfileWithBrowser(Activity activity, Repository repository){
    String url = "https://github.com/" + repository.login + "/" +repository.name;
    openInBrowser(activity, url);
  }

  private static void openUserProfileWithBrowser(Activity activity, String name){
    String url = "https://github.com/" + name;
    openInBrowser(activity, url);
  }

  private static void openInBrowser(@NonNull Context context, @NonNull String url) {
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    if (intent.resolveActivity(context.getPackageManager()) != null) {
      context.startActivity(intent);
    } else {
      ToastUtils.with(context).show(R.string.no_browser_install_in_system);
    }
  }
}
