package com.seasonfif.star.ui.helper;

/**
 * Created by zhangqiang on 2018/1/30.
 */

public interface DataObserver<T> {
    /**
     * 单条数据改变
     */
    int SINGLE = 0x0001;

    /**
     * 多条数据改变
     */
    int MULTI = 0x0001;

    void notifyChanged(int type, T t);
}
