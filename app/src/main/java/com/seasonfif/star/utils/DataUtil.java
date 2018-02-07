package com.seasonfif.star.utils;

import com.seasonfif.star.model.RepoTag;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lxy on 2018/1/27.
 */

public class DataUtil {

  public static List<String> getTagsData(){
    return Arrays.asList(new String[]{"无","控件","框架"});
  }

  public static List<RepoTag> getRepoTags(){
    List<RepoTag> tags = new ArrayList<>();
    tags.add(new RepoTag("控件"));
    tags.add(new RepoTag("框架"));
    return tags;
  }
}
