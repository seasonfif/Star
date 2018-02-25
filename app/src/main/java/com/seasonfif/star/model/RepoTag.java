package com.seasonfif.star.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

/**
 * Created by zhangqiang on 2018/2/6.
 */

@Entity
public class RepoTag implements Serializable{

  private static final long serialVersionUID = 1654124683987838529L;

  @Id
  public long id;

  public String name;

  @Transient
  public boolean isEdit = false;

  @Transient
  public boolean isNew = false;

  @Generated(hash = 1770273390)
public RepoTag(long id, String name) {
    this.id = id;
    this.name = name;
}

public RepoTag(String name, boolean isNew) {
    this.name = name;
    this.isNew = isNew;
  }

  @Generated(hash = 1318599142)
  public RepoTag() {
  }

  public String getName() {
      return this.name;
  }

  public void setName(String name) {
      this.name = name;
  }

public long getId() {
    return this.id;
}

public void setId(long id) {
    this.id = id;
}
}
