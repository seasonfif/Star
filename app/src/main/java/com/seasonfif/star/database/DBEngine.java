package com.seasonfif.star.database;

import com.seasonfif.star.database.gen.RepositoryDao;
import com.seasonfif.star.model.RepoTag;

import java.util.List;

/**
 * 数据库操作类
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

  public static <T> List<T> loadAll(Class<T> cls){
    return GreenDaoManager.getInstance().getDaoSession().loadAll(cls);
  }

  /**
   * 仓库名模糊搜索
   * @param cls
   * @param key
   * @param <T>
   * @return
   */
  public static <T> List<T> loadByName(Class<T> cls, String key) {
    return GreenDaoManager.getInstance().getDaoSession().queryBuilder(cls).where(RepositoryDao.Properties.Name.like("%"+key+"%")).list();
  }

  public static <T> List<T> loadByTag(Class<T> cls, String tag) {
    return GreenDaoManager.getInstance().getDaoSession().queryBuilder(cls).where(RepositoryDao.Properties.Group.eq(tag)).list();
  }

  public static <T> T loadById(Class<T> cls, long id) {
    return GreenDaoManager.getInstance().getDaoSession().queryBuilder(cls).where(RepositoryDao.Properties.Id.eq(id)).unique();
  }

  public static void deleteTag(RepoTag repoTag) {
    GreenDaoManager.getInstance().getDaoSession().getRepoTagDao().delete(repoTag);
  }
}
