package com.seasonfif.star.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.seasonfif.star.R;
import com.seasonfif.star.constant.Constants;
import com.seasonfif.star.database.DBEngine;
import com.seasonfif.star.model.Repository;
import com.seasonfif.star.utils.DataUtil;
import com.seasonfif.star.utils.ThemeUtil;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuViewBindListener;

import java.util.List;

/**
 * Created by zhangqiang on 2018/1/30.
 */

public abstract class BaseFragment extends Fragment {

    private IntentFilter intentFilter;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.HIDE_AVATAR.equals(action)){
              onHideAvatar();
            }else if (Constants.THEME_CHANGE.equals(action)){
                onThemeChange();
            }
        }
    };

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.HIDE_AVATAR);
        intentFilter.addAction(Constants.THEME_CHANGE);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, intentFilter);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    protected void onHideAvatar() {
    }

    protected void onThemeChange() {
    }


    @Override public void onDestroyView() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
        super.onDestroyView();
    }

    abstract RecyclerView.Adapter getAdapter();
    abstract Repository getItem(int position);
    abstract void afterRepoInsertDB(Repository repository);

    protected boolean isShowMenu(int viewType){
        return true;
    }

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    protected SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {

            if (!isShowMenu(viewType)) return;

            int width = getResources().getDimensionPixelSize(R.dimen.dimen_80);

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            SwipeMenuItem addItem = new SwipeMenuItem(getContext())
                    .setBackgroundColorResource(R.color.menu_bg)
                    .setImage(R.drawable.ic_unlike)
                    .setWidth(width)
                    .setHeight(height);
            swipeLeftMenu.addMenuItem(addItem); // 添加菜单到左侧。

            SwipeMenuItem closeItem = new SwipeMenuItem(getContext())
                    .setBackgroundColorResource(R.color.menu_bg)
                    .setImage(R.drawable.ic_untag)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(closeItem); // 添加菜单到右侧。
        }
    };

    /**
     * viewbind的时候修改menu的属性
     */
    protected SwipeMenuViewBindListener mSwipeMenuViewBindListener = new SwipeMenuViewBindListener() {
        @Override public void onBindMenuView(int position, int menuPosition, View view) {
            if (!isShowMenu(getAdapter().getItemViewType(position))) return;

            Repository repository = getItem(position);
            SwipeMenuBridge menuBridge = (SwipeMenuBridge) view.getTag();

            ImageView imageView = menuBridge.getImageView();
            if (menuBridge.getDirection() == SwipeMenuRecyclerView.LEFT_DIRECTION){
                if (repository.like == 1){
                    imageView.setImageDrawable(ThemeUtil.tintDrawable(R.drawable.ic_like, Color.RED));
                }else{
                    imageView.setImageDrawable(ThemeUtil.tintDrawable(R.drawable.ic_unlike, Color.RED));
                }
            }

            if (menuBridge.getDirection() == SwipeMenuRecyclerView.RIGHT_DIRECTION){
                if (TextUtils.isEmpty(repository.group)){
                    imageView.setImageDrawable(ThemeUtil.tintDrawable(R.drawable.ic_untag, Color.RED));
                }else{
                    imageView.setImageDrawable(ThemeUtil.tintDrawable(R.drawable.ic_tag, Color.RED));
                }
            }
        }
    };

    /**
     * RecyclerView的Item中的Menu点击监听。
     */
    protected SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            Repository repository = getItem(adapterPosition);
            //左边like按钮点击事件
            if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION){
                if (repository.like == 0){
                    repository.like = 1;
                    menuBridge.getImageView().setImageDrawable(ThemeUtil.tintDrawable(R.drawable.ic_like, Color.RED));
                }else{
                    repository.like = 0;
                    menuBridge.getImageView().setImageDrawable(ThemeUtil.tintDrawable(R.drawable.ic_unlike, Color.RED));
                }
                DBEngine.insertOrReplace(repository);
                afterRepoInsertDB(repository);
            }

            //右边like按钮点击事件
            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION){
                menuBridge.closeMenu();
                chooseTag(repository, adapterPosition);
            }
        }
    };

    private List<String> tags = DataUtil.getTagsData();

    protected void chooseTag(final Repository repository, final int position){

        final String oldTag = repository.group;
        int index;
        if (TextUtils.isEmpty(repository.group)){
            index = 0;
        }else{
            index = getIndexByTag(repository.group);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        //设置标题
        builder.setTitle("请选择");
        //设置图标
        builder.setIcon(ThemeUtil.tintDrawable(R.drawable.ic_tag, Color.RED));
        builder.setSingleChoiceItems(tags.toArray(new String[]{}), index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){
                    repository.group = "";
                }else{
                    repository.group = tags.get(i);
                }
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override public void onDismiss(DialogInterface dialogInterface) {
                if (!repository.group.equals(oldTag)){
                    getAdapter().notifyItemChanged(position);
                    DBEngine.insertOrReplace(repository);
                    afterRepoInsertDB(repository);
                }
            }
        });
        builder.create();
        builder.show();
    }

    private int getIndexByTag(String tag) {
        int index = tags.indexOf(tag);
        if (index == -1)  index = 0;
        return index;
    }


    @Override public void onDestroy() {
        super.onDestroy();
    }

    @Override public void onDetach() {
        super.onDetach();
    }
}
