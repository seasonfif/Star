package com.seasonfif.star.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.seasonfif.star.R;
import com.seasonfif.star.database.DBEngine;
import com.seasonfif.star.model.Level0Item;
import com.seasonfif.star.model.Repository;
import com.seasonfif.star.ui.adapter.ExpandableItemAdapter;
import com.seasonfif.star.ui.adapter.RepoAdapter;
import com.seasonfif.star.ui.helper.DataObserver;
import com.seasonfif.star.ui.helper.EventManager;
import com.seasonfif.star.utils.Navigator;
import com.seasonfif.star.utils.ThemeUtil;
import com.seasonfif.star.utils.ToastUtils;
import com.seasonfif.star.utils.Utils;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuViewBindListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemStateChangedListener;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lxy on 2018/1/27.
 */

public class OrderFragment extends BaseFragment implements DataObserver<Repository>,
    Toolbar.OnMenuItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fab)
    FloatingActionButton mFloatingActionButton;
    @BindView(R.id.content_container)
    SwipeRefreshLayout mContentContainer;

    public static String KEY_TITLE = "title";
    String title;
    private int pageIndex = 1;
    private SwipeMenuRecyclerView mRecyclerView;
    private MenuItem menuItem;
    private SearchView searchView;
    private RepoAdapter repoAdapter;
    private ExpandableItemAdapter expandableItemAdapter;
    private String query;

    public static OrderFragment newInstance(String title) {
        Bundle args = new Bundle();
        OrderFragment fragment = new OrderFragment();
        args.putString(KEY_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventManager.getInstanse().register(this);
        Bundle bundle = getArguments();
        if (bundle != null){
            title = bundle.getString(KEY_TITLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);
        mToolbar.inflateMenu(R.menu.order_menu);
        mToolbar.setOnMenuItemClickListener(this);
        searchViewInit();

        View customerView = initCustomerView();
        if (customerView != null) {
            mContentContainer.addView(customerView);
        }

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollToTop();
            }
        });
    }

    private void searchViewInit() {
        menuItem = mToolbar.getMenu().getItem(0);
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override public boolean onMenuItemActionExpand(MenuItem menuItem) {
                //ToastUtils.with(getContext()).show("SearchView open");
                return true;
            }

            @Override public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                //ToastUtils.with(getContext()).show("SearchView close");
                query = null;
                loadData();
                return true;
            }
        });
        searchView = (SearchView) menuItem.getActionView();
        //searchView.setQueryHint("search condition");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String str) {
                Utils.closeInputMethod(getActivity());
                query = str;
                loadData();
                return true;
            }

            @Override public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void scrollToTop() {
        if (null != mRecyclerView)
            mRecyclerView.smoothScrollToPosition(0);
    }

    private View initCustomerView() {
        mContentContainer.setEnabled(true);
        mContentContainer.setOnRefreshListener(mRefreshListener);

        mRecyclerView = new SwipeMenuRecyclerView(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(getContext(), R.color.divider_color)));

        //打开长按拖拽的开关
        mRecyclerView.setLongPressDragEnabled(true);

//        这个点击事件会影响expandableItemAdapter的事件
//        mRecyclerView.setSwipeItemClickListener(mItemClickListener);
        mRecyclerView.setSwipeMenuCreator(mSwipeMenuCreator);
        mRecyclerView.setSwipeMenuViewBindListener(mSwipeMenuViewBindListener);
        //mRecyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);
        mRecyclerView.setOnItemMoveListener(mOnItemMoveListener);
        mRecyclerView.setOnItemStateChangedListener(mOnItemStateChangedListener);

        /*// 自定义的核心就是DefineLoadMoreView类。
        DefineLoadMoreView loadMoreView = new DefineLoadMoreView(getContext());
        mRecyclerView.addFooterView(loadMoreView); // 添加为Footer。
        mRecyclerView.setLoadMoreView(loadMoreView); // 设置LoadMoreView更新监听。
        mRecyclerView.setLoadMoreListener(mLoadMoreListener); // 加载更多的监听。*/

        //repoAdapter = new RepoAdapter(getContext());
        //mRecyclerView.setAdapter(repoAdapter);
        expandableItemAdapter = new ExpandableItemAdapter(getContext(), null);
        mRecyclerView.setAdapter(expandableItemAdapter);
        loadData();
        return mRecyclerView;
    }

    private void loadData() {
        if(expandableItemAdapter == null){
            return;
        }
        /*Observable.create(new Observable.OnSubscribe<List<Repository>>(){
            @Override public void call(Subscriber<? super List<Repository>> subscriber) {
                try {
                    //List<Repository> list = DBEngine.loadAll(Repository.class);
                    List<Repository> list = DBEngine.loadByLike(Repository.class, 1);
                    subscriber.onNext(list);
                    subscriber.onCompleted();
                }catch (Exception e){
                    subscriber.onError(e);
                }

            }
        })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new Subscriber<List<Repository>>() {
            @Override public void onCompleted() {

            }

            @Override public void onError(Throwable e) {
                mContentContainer.setRefreshing(false);
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override public void onNext(List<Repository> repositories) {
                repoAdapter.notifyDataSetChanged(repositories);
                mContentContainer.setRefreshing(false);
                Toast.makeText(getContext(), "Total:" + repositories.size(), Toast.LENGTH_SHORT).show();
                mRecyclerView.loadMoreFinish(repositories == null || repositories.size() == 0, false);
            }
        });*/
        Collection<? extends MultiItemEntity> entities = generateData();
        expandableItemAdapter.replaceData(entities);
        mContentContainer.setRefreshing(false);
        mRecyclerView.loadMoreFinish(entities == null || entities.size() == 0, false);
    }

    private Collection<? extends MultiItemEntity> generateData() {
        List<Repository> rawList;
        if (!TextUtils.isEmpty(query)){
            rawList = DBEngine.loadByName(Repository.class, query);
        }else{
            rawList = DBEngine.loadAll(Repository.class);
        }

        ToastUtils.with(getContext()).show("Total:" + rawList.size());
        List<MultiItemEntity> entities = new ArrayList<>();
        Map<Integer, Level0Item> likes = new HashMap<>();
        Level0Item lv0like = new Level0Item("Favorite");
        Level0Item lv0NoGroup = new Level0Item("未添加任何标签");
        entities.add(lv0like);
        Map<String, Level0Item> tags = new HashMap<>();
        for (int i = 0; i < rawList.size(); i++) {
            Repository repository = rawList.get(i);
            if (repository.like == 1){
                lv0like.addSubItem(repository);
            }

            if (!TextUtils.isEmpty(repository.group)){
                String tag = repository.group;
                //如果已经被添加进favorite列表，为了防止重复添加（重复添加在展开收起时只会定位到列表中的第一个对象，引发bug）
                //故这里需要新对象
                if (repository.like == 1){
                    repository = repository.clone();
                }
                if (tags.containsKey(tag)){
                    Level0Item lv0 = tags.get(tag);
                    lv0.addSubItem(repository);
                }else{
                    Level0Item lv0 = new Level0Item();
                    lv0.setGroup(tag);
                    lv0.addSubItem(repository);
                    tags.put(tag, lv0);
                    entities.add(lv0);
                }
            }

            if (repository.like == 0 && TextUtils.isEmpty(repository.group)){
                lv0NoGroup.addSubItem(repository);
            }
        }
        entities.add(lv0NoGroup);
        return entities;
    }

    /**
     * 刷新。
     */
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    loadData();
                }
            });
        }
    };

    /**
     * 加载更多。
     */
    private SwipeMenuRecyclerView.LoadMoreListener mLoadMoreListener = new SwipeMenuRecyclerView.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    };

    /**
     * RecyclerView的Item点击监听。
     */
    private SwipeItemClickListener mItemClickListener = new SwipeItemClickListener() {
        @Override
        public void onItemClick(View itemView, int position) {
            if(expandableItemAdapter.getItemViewType(position) == ExpandableItemAdapter.TYPE_LEVEL_1){
                Navigator.openRepoProfile(getActivity(), repoAdapter.getItem(position));
            }
        }
    };

    /**
     * RecyclerView的Item中的Menu点击监听。
     */
    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            //menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            Repository repository = repoAdapter.getItem(adapterPosition);
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
            }

            //if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
            //    Toast.makeText(getContext(), "list第" + adapterPosition + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            //} else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
            //    Toast.makeText(getContext(), "list第" + adapterPosition + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            //}
        }
    };

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            if (viewType == ExpandableItemAdapter.TYPE_LEVEL_0) return;
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

    private SwipeMenuViewBindListener mSwipeMenuViewBindListener = new SwipeMenuViewBindListener() {
        @Override public void onBindMenuView(int position, int menuPosition, View view) {
            Repository repository = (Repository) expandableItemAdapter.getItem(position);
            SwipeMenuBridge menuBridge = (SwipeMenuBridge) view.getTag();

            ImageView imageView = menuBridge.getImageView();
            if (menuBridge.getDirection() == SwipeMenuRecyclerView.LEFT_DIRECTION){
                if (repository.like == 1){
                    imageView.setImageDrawable(ThemeUtil.tintDrawable(R.drawable.ic_like, Color.RED));
                }else{
                    imageView.setImageDrawable(ThemeUtil.tintDrawable(R.drawable.ic_unlike, Color.RED));
                }
            }
        }
    };

    private OnItemMoveListener mOnItemMoveListener = new OnItemMoveListener() {
        @Override public boolean onItemMove(RecyclerView.ViewHolder srcHolder,
            RecyclerView.ViewHolder targetHolder) {
            // 不同的ViewType不能拖拽换位置。
            if (srcHolder.getItemViewType() != targetHolder.getItemViewType()) return false;

            // 真实的Position：通过ViewHolder拿到的position都需要减掉HeadView的数量。
            int fromPosition = srcHolder.getAdapterPosition() - mRecyclerView.getHeaderItemCount();
            int toPosition = targetHolder.getAdapterPosition() - mRecyclerView.getHeaderItemCount();

            Collections.swap(expandableItemAdapter.getData(), fromPosition, toPosition);
            expandableItemAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {

        }
    };

    /**
     * Item的拖拽/侧滑删除时，手指状态发生变化监听。
     */
    private OnItemStateChangedListener mOnItemStateChangedListener = new OnItemStateChangedListener() {
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState == OnItemStateChangedListener.ACTION_STATE_DRAG) {
                mToolbar.setSubtitle("状态：拖拽");

                // 拖拽的时候背景就透明了，这里我们可以添加一个特殊背景。
                viewHolder.itemView.setBackgroundColor(ThemeUtil.getColor(getContext(), R.attr.app_accent_color));
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_SWIPE) {
                mToolbar.setSubtitle("状态：滑动删除");
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
                mToolbar.setSubtitle("状态：手指松开");

                // 在手松开的时候还原背景。
                ViewCompat.setBackground(viewHolder.itemView, ThemeUtil.getDrawable(getContext(), android.R.attr.selectableItemBackground));
            }
        }
    };

    @Override public void notifyChanged(int type, Repository repo) {
        if (type == DataObserver.MULTI){
            loadData();
        } else if (type == DataObserver.SINGLE){
            loadData();
        }
    }

    @Override public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        return false;
    }

    @Override protected void onHideAvatar() {
        loadData();
    }

    @Override
    protected void onThemeChange() {
        if (menuItem.isActionViewExpanded()){
            menuItem.collapseActionView();
        }
    }

    @Override public void setUserVisibleHint(boolean isVisibleToUser) {
        if (!isVisibleToUser){
            Utils.closeInputMethod(getActivity());
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        EventManager.getInstanse().unregister(this);
    }
}
