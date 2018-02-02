package com.seasonfif.star.model;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.seasonfif.star.ui.adapter.ExpandableItemAdapter;

/**
 * Created by zhangqiang on 2018/2/1.
 */

public class Level0Item extends AbstractExpandableItem implements MultiItemEntity{

  public String group;

  public Level0Item(){}

  public Level0Item(String group){
    this.group = group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  @Override public int getItemType() {
    return ExpandableItemAdapter.TYPE_LEVEL_0;
  }

  @Override public int getLevel() {
    return 0;
  }
}
