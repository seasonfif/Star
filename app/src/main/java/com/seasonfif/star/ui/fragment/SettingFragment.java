package com.seasonfif.star.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.seasonfif.star.R;
import com.seasonfif.star.constant.Constants;
import com.seasonfif.star.ui.activity.SettingsActivity;
import com.seasonfif.star.utils.SettingShared;
import com.seasonfif.star.widget.CircleImageView;

/**
 * Created by zhangqiang on 2018/1/29.
 */

public class SettingFragment extends Fragment {

  @BindView(R.id.theme_switch)
  CircleImageView themeSwitcher;

  @BindView(R.id.switch_img)
  Switch imgSwitcher;

  @BindView(R.id.btn)
  Button btn;

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
    btn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Intent it = new Intent(getActivity(), SettingsActivity.class);
        startActivity(it);
      }
    });

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
  }
}
