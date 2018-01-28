package com.seasonfif.star.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.seasonfif.star.R;
import com.seasonfif.star.database.GreenDaoManager;
import com.seasonfif.star.model.Repository;
import com.seasonfif.star.net.PaginationLink;
import com.seasonfif.star.net.RelType;
import com.seasonfif.star.net.RetrofitEngine;
import com.seasonfif.star.net.StarService;
import com.seasonfif.star.widget.DefineLoadMoreView;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by lxy on 2018/1/27.
 */

public class StarFragment extends Fragment {

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

    public static StarFragment newInstance(String title) {
        Bundle args = new Bundle();
        StarFragment fragment = new StarFragment();
        args.putString(KEY_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null){
            title = bundle.getString(KEY_TITLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_star, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);
        mToolbar.setTitle("Star");
        mToolbar.inflateMenu(R.menu.activity_main_menu);

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
        getData(true);
        return mRecyclerView;
    }

    private void getData(final boolean refresh) {
        if (refresh) {
            pageIndex = 1;
            if (repoAdapter.mDataList != null) {
                repoAdapter.mDataList.clear();
            } else {
                repoAdapter.mDataList = new ArrayList<>();
            }
        }
        StarService service = RetrofitEngine.getRetrofit().create(StarService.class);
        Subscription subscription = service.userStarredReposList("seasonfif", "updated", pageIndex, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Response<List<Repository>>, Pair<List<Repository>, Integer>>() {
                    @Override
                    public Pair<List<Repository>, Integer> call(Response<List<Repository>> response) {

                        return new Pair<>(response.body(), getLinkData(response));
                    }
                })
                .subscribe(new Subscriber<Pair<List<Repository>, Integer>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mContentContainer.setRefreshing(false);
                    }

                    @Override
                    public void onNext(Pair<List<Repository>, Integer> pair) {
                        saveDB(pair.first);
                        List<Repository> datas = repoAdapter.mDataList;
                        if (pair.second != null) {
                            pageIndex = pair.second;
                        }
                        datas.addAll(pair.first);
                        repoAdapter.notifyDataSetChanged(datas);
                        mContentContainer.setRefreshing(false);
                        mRecyclerView.loadMoreFinish(pair.first == null || pair.first.size() == 0, pair.second != null);
                    }
                });
    }

    private void saveDB(List<Repository> repos) {
        if (repos == null || repos.size() == 0) return;
        for (Repository repo : repos) {
            GreenDaoManager.getInstance().getDaoSession().insertOrReplace(repo);
        }
    }

    /**
     * 刷新。
     */
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getData(true);
                }
            }, 1000); // 延时模拟请求服务器。
        }
    };

    /**
     * 加载更多。
     */
    private SwipeMenuRecyclerView.LoadMoreListener mLoadMoreListener = new SwipeMenuRecyclerView.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getData(false);
                    /*List<String> strings = createDataList(repoAdapter.getItemCount());
//                    mDataList.addAll(strings);
                    // notifyItemRangeInserted()或者notifyDataSetChanged().
                    repoAdapter.notifyItemRangeInserted(mDataList.size() - strings.size(), strings.size());

                    // 数据完更多数据，一定要掉用这个方法。
                    // 第一个参数：表示此次数据是否为空。
                    // 第二个参数：表示是否还有更多数据。
                    mRecyclerView.loadMoreFinish(false, true);

                    // 如果加载失败调用下面的方法，传入errorCode和errorMessage。
                    // errorCode随便传，你自定义LoadMoreView时可以根据errorCode判断错误类型。
                    // errorMessage是会显示到loadMoreView上的，用户可以看到。
                    // mRecyclerView.loadMoreError(0, "请求网络失败");*/
                }
            }, 1000);
        }
    };

    /**
     * RecyclerView的Item点击监听。
     */
    private SwipeItemClickListener mItemClickListener = new SwipeItemClickListener() {
        @Override
        public void onItemClick(View itemView, int position) {
            Repository repository = repoAdapter.getItem(position);
            Intent it = new Intent();
            it.setAction("com.seasonfif.github.repo.detail");
            it.putExtra("owner", repository.owner.login);
            it.putExtra("repoName", repository.name);
            startActivity(it);
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

    private Integer getLinkData(Response r) {
        if (r != null) {
            String link = r.headers().get("Link");
            if (link != null) {
                String[] parts = link.split(",");
                for (String part : parts){
                    PaginationLink paginationLink = new PaginationLink(part);
                    if (paginationLink.rel == RelType.next){
                        return paginationLink.page;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.activity_main_menu, menu);
//        refreshItem = menu.findItem(R.id.action_refresh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            Toast.makeText(getActivity(), "refresh", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
