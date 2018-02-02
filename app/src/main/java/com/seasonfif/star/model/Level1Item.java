package com.seasonfif.star.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.seasonfif.star.ui.adapter.ExpandableItemAdapter;

/**
 * Created by zhangqiang on 2018/2/1.
 */

public class Level1Item implements MultiItemEntity{

  public String name;

  public Level1Item(String name){
    this.name = name;
  }

  @Override public int getItemType() {
    return ExpandableItemAdapter.TYPE_LEVEL_1;
  }

}
