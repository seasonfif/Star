package com.seasonfif.star.database;

import java.util.List;

/**
 * Created by zhangqiang on 2018/1/30.
 */

public class DBEngine {

  public static <T> void insertOrReplace(T entity){
    GreenDaoManager.getInstance().getDaoSession().insertOrReplace(entity);
  }

  public static <T> void insertOrReplaceTx(final List<T> entities){
    GreenDaoManager.getInstance().getDaoSession().runInTx(new Runnable() {
      @Override public void run() {
        for (T entity: entities) {
          insertOrReplace(entity);
        }
      }
    });
  }

  public static <T> List<T> loadAll(Class cls){
    return GreenDaoManager.getInstance().getDaoSession().loadAll(cls);
  }
}
