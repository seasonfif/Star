package com.seasonfif.star.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.seasonfif.star.R;
import com.seasonfif.star.utils.ThemeUtil;

/**
 * Created by zhangqiang on 2018/1/29.
 */

public class SettingFragment2 extends Fragment implements Toolbar.OnMenuItemClickListener {

  private float mSelfHeight = 0;//用以判断是否得到正确的宽高值
  private float mTitleScale;
  private float mSubScribeScale;
  private float mSubScribeScaleX;
  private float mHeadImgScale;

  @BindView(R.id.iv_head)
  ImageView mHeadImage;
  @BindView(R.id.subscription_title)
  TextView mSubscriptionTitle;
  @BindView(R.id.subscribe)
  TextView mSubscribe;
  @BindView(R.id.app_bar)
  AppBarLayout mAppBar;
  @BindView(R.id.toolbar)
  Toolbar mToolbar;

  public static Fragment newInstance() {
    return new SettingFragment2();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_setting2, null);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);

    setHasOptionsMenu(true);
    mToolbar.inflateMenu(R.menu.setting_menu);
    mToolbar.setOnMenuItemClickListener(this);

    final float screenW = getResources().getDisplayMetrics().widthPixels;
    final float toolbarHeight = ThemeUtil.getDimension(getContext(), R.attr.actionBarSize);
    final float initHeight = getResources().getDimension(R.dimen.subscription_head);
    mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
      @Override
      public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        Log.e("verticalOffset", ""+verticalOffset);
        //Log.e("toolbarHeight", ""+toolbarHeight);
        //Log.e("initHeight", ""+initHeight);
        //Log.e("mAppBar", ""+mAppBar.getHeight());
        Log.e("mSubscriptionTitle", mSubscriptionTitle.getY()+"/"+mSubscriptionTitle.getTop());
        //Log.e("mSubscriptionTitle", mSubscriptionTitle.getHeight()+"/"+mSubscriptionTitle.getWidth());
        //Log.e("mSubscribe", mSubscribe.getY()+"/"+mSubscribe.getTop());
        Log.e("Translation1", mSubscriptionTitle.getTranslationX()+"/"+mSubscriptionTitle.getTranslationY());

        if (mSelfHeight == 0) {
          mSelfHeight = mSubscriptionTitle.getHeight();
          //float distanceTitle = mSubscriptionTitle.getTop() - (toolbarHeight - mSelfHeight) / 2.0f;
          float distanceTitle = initHeight - ((toolbarHeight + mSelfHeight) / 2.0f) - mSubscriptionTitle.getTop();
          //float distanceSubscribe = mSubscribe.getY() + (toolbarHeight - mSubscribe.getHeight()) / 2.0f;
          float distanceHeadImg = initHeight - ((toolbarHeight + mHeadImage.getHeight()) / 2.0f) - mHeadImage.getTop();
          //float distanceSubscribeX = screenW / 2.0f - (mSubscribe.getWidth() / 2.0f);
          mTitleScale = distanceTitle / (initHeight - toolbarHeight);
          //mSubScribeScale = distanceSubscribe / (initHeight - toolbarHeight);
          mHeadImgScale = distanceHeadImg / (initHeight - toolbarHeight);
          //mSubScribeScaleX = distanceSubscribeX / (initHeight - toolbarHeight);
        }
        //float scale = 1.0f - (-verticalOffset) / (initHeight - toolbarHeight);
        //mHeadImage.setScaleX(scale);
        //mHeadImage.setScaleY(scale);
        mHeadImage.setTranslationY(-mHeadImgScale * verticalOffset);
        Log.e("Translation2", mTitleScale+"/"+mTitleScale * verticalOffset);
        mSubscriptionTitle.setTranslationY(-mTitleScale * verticalOffset);
        //mSubscribe.setTranslationY(mSubScribeScale * verticalOffset);
        //mSubscribe.setTranslationX(-mSubScribeScaleX * verticalOffset);
      }
    });
  }

  @Override public boolean onMenuItemClick(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_logout) {
      Toast.makeText(getActivity(), "logout", Toast.LENGTH_SHORT).show();
      return true;
    }
    return false;
  }
}
