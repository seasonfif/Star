package com.seasonfif.star.utils;

import com.google.gson.Gson;
import com.seasonfif.star.database.DBEngine;
import com.seasonfif.star.model.RepoTag;
import com.seasonfif.star.model.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lxy on 2018/1/27.
 */

public class DataUtil {

  public static String toJson(User me){
    return new Gson().toJson(me);
  }

  public static <T> T getObject(String json, Class<T> cls){
    return new Gson().fromJson(json, cls);
  }

  public static List<RepoTag> TAGS;

  public static List<RepoTag> getRepoTags(){
    if (TAGS == null || TAGS.size() == 0){
      TAGS = DBEngine.loadAll(RepoTag.class);
    }
    return TAGS;
  }

  public static void getTags(){
    TAGS = DBEngine.loadAll(RepoTag.class);
    /*tags.add(new RepoTag("控件"));
    tags.add(new RepoTag("框架"));
    return tags;*/
  }


  public static void clearRepoTags() {
    if (TAGS != null){
      TAGS.clear();
    }
  }
}
