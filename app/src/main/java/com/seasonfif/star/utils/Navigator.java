package com.seasonfif.star.utils;

import android.app.Activity;
import android.content.Intent;
import com.seasonfif.star.model.Repository;

/**
 * Created by zhangqiang on 2018/1/29.
 */

public class Navigator {

  public static void openRepoProfile(Activity activity, Repository repository){

    Intent it = new Intent();
    it.setAction("com.seasonfif.github.repo.detail");
    it.putExtra("owner", repository.login);
    it.putExtra("repoName", repository.name);
    activity.startActivity(it);
  }
}
