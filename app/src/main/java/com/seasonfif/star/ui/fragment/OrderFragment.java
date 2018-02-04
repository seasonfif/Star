package com.seasonfif.star.ui.fragment;

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
import android.widget.Toast;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.seasonfif.star.R;
import com.seasonfif.star.database.DBEngine;
import com.seasonfif.star.model.Level0Item;
import com.seasonfif.star.model.Repository;
import com.seasonfif.star.ui.adapter.ExpandableItemAdapter;
import com.seasonfif.star.ui.helper.DataObserver;
import com.seasonfif.star.ui.helper.EventManager;
import com.seasonfif.star.utils.ThemeUtil;
import com.seasonfif.star.utils.ToastUtils;
import com.seasonfif.star.utils.Utils;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemStateChangedListener;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        mRecyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);
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
        Observable.create(new Observable.OnSubscribe<List<MultiItemEntity>>(){
            @Override public void call(Subscriber<? super List<MultiItemEntity>> subscriber) {
                try {
                    List<MultiItemEntity> list = generateData();
                    subscriber.onNext(list);
                    subscriber.onCompleted();
                }catch (Exception e){
                    subscriber.onError(e);
                }

            }
        })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new Subscriber<List<MultiItemEntity>>() {
            @Override public void onCompleted() {

            }

            @Override public void onError(Throwable e) {
                mContentContainer.setRefreshing(false);
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override public void onNext(List<MultiItemEntity> entities) {
                expandableItemAdapter.replaceData(entities);
                mContentContainer.setRefreshing(false);
                mRecyclerView.loadMoreFinish(entities == null || entities.size() == 0, false);
            }
        });
    }

    private List<MultiItemEntity> generateData() {
        final List<Repository> rawList;
        if (!TextUtils.isEmpty(query)){
            rawList = DBEngine.loadByName(Repository.class, query);
        }else{
            rawList = DBEngine.loadAll(Repository.class);
        }

        Collections.sort(rawList, new Comparator<Repository>() {
            @Override
            public int compare(Repository repository, Repository t1) {
                return repository.name.compareToIgnoreCase(t1.name);
            }
        });
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.with(getContext()).show("Total:" + rawList.size());
            }
        });
        List<MultiItemEntity> entities = new ArrayList<>();
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

    @Override
    RecyclerView.Adapter getAdapter() {
        return expandableItemAdapter;
    }

    @Override
    Repository getItem(int position) {
        return (Repository) expandableItemAdapter.getItem(position);
    }

    @Override
    protected boolean isShowMenu(int viewType) {
        return viewType == ExpandableItemAdapter.TYPE_LEVEL_1;
    }

    @Override
    void afterRepoInsertDB(Repository repository) {
        loadData();
    }
}
