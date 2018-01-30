package com.seasonfif.star.ui.helper;

import java.util.HashSet;
import java.util.Set;

/**
 * 数据库数据变化监听管理器
 * Created by zhangqiang on 2018/1/30.
 */

public class EventManager {

    private static Set<DataObserver> observers;

    private static volatile EventManager instanse;

    private EventManager(){
      observers = new HashSet<>();
    }

    public static EventManager getInstanse(){
        if (instanse == null){
          synchronized (EventManager.class){
            if (instanse == null){
              instanse = new EventManager();
            }
          }
        }
        return instanse;
    }

  /**
   * 注册事件监听者
   * @param observer
   * @param <T>
   */
    public <T> void register(DataObserver<T> observer) {
      observers.add(observer);
    }

  /**
   * 在数据改变时通知所有监听者
   * @param type
   * @param t
   * @param <T>
   */
    public <T> void notifyAll(int type, T t) {
      for (DataObserver observer : observers) {
        observer.notifyChanged(type, t);
      }
    }
}
