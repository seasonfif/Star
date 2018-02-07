package com.seasonfif.star.model;

import java.io.Serializable;

/**
 * Created by zhangqiang on 2018/2/6.
 */

public class RepoTag implements Serializable{

  public String name;
  public boolean isEdit = false;

  public RepoTag(String name) {
    this.name = name;
  }
}
