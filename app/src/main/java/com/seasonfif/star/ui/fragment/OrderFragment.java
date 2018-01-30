package com.seasonfif.star.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.seasonfif.star.R;
import com.seasonfif.star.database.DBEngine;
import com.seasonfif.star.model.Repository;
import com.seasonfif.star.ui.helper.DataObserver;
import com.seasonfif.star.ui.helper.EventManager;
import com.seasonfif.star.utils.Navigator;
import com.seasonfif.star.widget.DefineLoadMoreView;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lxy on 2018/1/27.
 */

public class OrderFragment extends Fragment implements DataObserver<Repository>,
    Toolbar.OnMenuItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fab)
    FloatingActionButton mFloatingActionButton;
    @BindView(R.id.content_container)
    SwipeRefreshLayout mContentContainer;

    private int pageIndex = 1;
    private SwipeMenuRecyclerView mRecyclerView;
    private RepoAdapter repoAdapter;
    public static String KEY_TITLE = "title";
    String title;

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
        mToolbar.inflateMenu(R.menu.action_menu);
        mToolbar.setOnMenuItemClickListener(this);

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

    private void scrollToTop() {
        if (null != mRecyclerView)
            mRecyclerView.smoothScrollToPosition(0);
    }

    private View initCustomerView() {
        mContentContainer.setOnRefreshListener(mRefreshListener);

        mRecyclerView = new SwipeMenuRecyclerView(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(getContext(), R.color.divider_color)));

        mRecyclerView.setSwipeItemClickListener(mItemClickListener);
        mRecyclerView.setSwipeMenuCreator(mSwipeMenuCreator);
        mRecyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);

        // 自定义的核心就是DefineLoadMoreView类。
        DefineLoadMoreView loadMoreView = new DefineLoadMoreView(getContext());
        mRecyclerView.addFooterView(loadMoreView); // 添加为Footer。
        mRecyclerView.setLoadMoreView(loadMoreView); // 设置LoadMoreView更新监听。
        mRecyclerView.setLoadMoreListener(mLoadMoreListener); // 加载更多的监听。

        repoAdapter = new RepoAdapter(getContext());
        mRecyclerView.setAdapter(repoAdapter);
        return mRecyclerView;
    }

    private void loadData() {
        Observable.create(new Observable.OnSubscribe<List<Repository>>(){
            @Override public void call(Subscriber<? super List<Repository>> subscriber) {
                List<Repository> list = DBEngine.loadAll(Repository.class);
                subscriber.onNext(list);
                subscriber.onCompleted();
            }
        })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new Subscriber<List<Repository>>() {
            @Override public void onCompleted() {

            }

            @Override public void onError(Throwable e) {

            }

            @Override public void onNext(List<Repository> repositories) {
                repoAdapter.notifyDataSetChanged(repositories);
                mContentContainer.setRefreshing(false);
                Toast.makeText(getContext(), "Total:" + repositories.size(), Toast.LENGTH_SHORT).show();
                mRecyclerView.loadMoreFinish(repositories == null || repositories.size() == 0, false);
            }
        });
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
            Navigator.openRepoProfile(getActivity(), repoAdapter.getItem(position));
        }
    };

    /**
     * RecyclerView的Item中的Menu点击监听。
     */
    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                Toast.makeText(getContext(), "list第" + adapterPosition + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                Toast.makeText(getContext(), "list第" + adapterPosition + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.dp_70);

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            SwipeMenuItem addItem = new SwipeMenuItem(getContext())
                    .setBackground(R.drawable.selector_green)
                    .setImage(R.drawable.ic_launcher_round)
                    .setWidth(width)
                    .setHeight(height);
            swipeLeftMenu.addMenuItem(addItem); // 添加菜单到左侧。

            SwipeMenuItem closeItem = new SwipeMenuItem(getContext())
                    .setBackground(R.drawable.selector_green)
                    .setImage(R.drawable.ic_launcher)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(closeItem); // 添加菜单到右侧。
        }
    };

    @Override public void notifyChanged(int type, Repository repo) {
        if (type == DataObserver.MULTI){
            loadData();
        }
    }

    @Override public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            Toast.makeText(getActivity(), "refresh", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
