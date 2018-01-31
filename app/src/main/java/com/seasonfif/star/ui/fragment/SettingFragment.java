package com.seasonfif.star.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.seasonfif.star.MyApplication;
import com.seasonfif.star.R;
import com.seasonfif.star.constant.Constants;
import com.seasonfif.star.utils.Navigator;
import com.seasonfif.star.utils.SettingShared;
import com.seasonfif.star.utils.ThemeUtil;
import com.seasonfif.star.widget.CircleImageView;

/**
 * Created by zhangqiang on 2018/1/29.
 */

public class SettingFragment extends Fragment implements Toolbar.OnMenuItemClickListener {

  @BindView(R.id.app_bar)
  AppBarLayout mAppBarLayout;
  @BindView(R.id.collapsingToolbarLayout)
  CollapsingToolbarLayout mCollapsingToolbarLayout;
  @BindView(R.id.toolbar)
  Toolbar mToolbar;
  @BindView(R.id.iv_avatar)
  CircleImageView mAvatar;
  @BindView(R.id.tv_name)
  TextView mName;
  @BindView(R.id.theme_switch)
  CircleImageView themeSwitcher;
  @BindView(R.id.switch_img)
  Switch imgSwitcher;
  @BindView(R.id.switch_openwith)
  Switch openwithSwitcher;

  public static Fragment newInstance() {
    return new SettingFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_setting, null);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);

    setHasOptionsMenu(true);
    mToolbar.inflateMenu(R.menu.setting_menu);
    mToolbar.setOnMenuItemClickListener(this);

    MyApplication.INSTANCE.loadAvatar("https://avatars2.githubusercontent.com/u/6039633?v=4", mAvatar);
    mAvatar.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Navigator.openUserProfile(getActivity(), "seasonfif");
      }
    });

    showAnim();

    themeSwitcher.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        ThemeSwitchDialogFragment.newInstance().show(getChildFragmentManager());
      }
    });

    imgSwitcher.setChecked(SettingShared.isHideAvatar(getContext()));
    imgSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        SettingShared.setHideAvatar(getActivity(), b);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(Constants.HIDE_AVATAR));
      }
    });

    openwithSwitcher.setChecked(SettingShared.isOpenWithGithub(getContext()));
    openwithSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        SettingShared.setOpenWithGithub(getActivity(), b);
      }
    });
  }

  private float mSelfHeight = 0;//用以判断是否得到正确的宽高值
  private float mHeadImgScaleX;
  private float mHeadImgScale;
  private float mNameScaleX;
  private float mNameScaleY;

  private void showAnim() {
    final float toolbarHeight = ThemeUtil.getDimension(getContext(), R.attr.actionBarSize);
    final float initHeight = getResources().getDimension(R.dimen.dimen_225);
    final float space = getResources().getDimension(R.dimen.dimen_7);
    mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
      @Override
      public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mSelfHeight == 0) {
          mSelfHeight = mAvatar.getHeight();
          float distanceHeadImgX = mSelfHeight/2.0f - toolbarHeight/3.0f;
          float distanceHeadImgY = initHeight - (mSelfHeight + toolbarHeight) / 2.0f - mAvatar.getY();
          float distanceNameX = toolbarHeight*2/3 + space;
          float distanceNameY = initHeight - ((toolbarHeight + mName.getHeight()) / 2.0f) - mName.getY();
          mHeadImgScaleX = distanceHeadImgX / (initHeight - toolbarHeight);
          mHeadImgScale = distanceHeadImgY / (initHeight - toolbarHeight);

          mNameScaleX = distanceNameX / (initHeight - toolbarHeight);
          mNameScaleY = distanceNameY / (initHeight - toolbarHeight);
        }

        float x = (mSelfHeight - toolbarHeight*2/3) / mSelfHeight;
        float scale = 1.0f - x*((-verticalOffset) / (initHeight - toolbarHeight));
        mAvatar.setScaleX(scale);
        mAvatar.setScaleY(scale);
        mAvatar.setTranslationX(mHeadImgScaleX * verticalOffset);
        mAvatar.setTranslationY(-mHeadImgScale * verticalOffset);

        mName.setTranslationX(-mNameScaleX * verticalOffset);
        mName.setTranslationY(-mNameScaleY * verticalOffset);
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
