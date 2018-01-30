package com.seasonfif.star.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import com.seasonfif.star.R;
import com.seasonfif.star.utils.SettingShared;
import com.seasonfif.star.utils.ThemeUtil;
import com.seasonfif.star.utils.Utils;
import com.seasonfif.star.widget.CircleImageView;

public class ThemeSwitchDialogFragment extends DialogFragment {

  public static final String TAG = ThemeSwitchDialogFragment.class.getSimpleName();
  private GridView grid;

  public static ThemeSwitchDialogFragment newInstance() {
    return new ThemeSwitchDialogFragment();
  }

  public ThemeSwitchDialogFragment() {
    super();
  }

  @Override
  public void onAttach(final Activity activity) {
    super.onAttach(activity);
  }

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public Dialog onCreateDialog(final Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity(), getTheme())
            .setView(onSetupView(LayoutInflater.from(getActivity()), savedInstanceState))
            .create();
  }

  @Override
  public void onActivityCreated(final Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Window window = getDialog().getWindow();
//    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    window.setLayout(Utils.getScreenWidth(getActivity())*5/6, Utils.getScreenHeight(getActivity())/2);
    initChoice();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }

  public void show(FragmentManager fm) {
    show(fm, TAG);
  }

  public void show(FragmentTransaction ft) {
    show(ft, TAG);
  }


  protected View onSetupView(final LayoutInflater inflater, final Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_theme_switch, null, false);
    grid = view.findViewById(R.id.gridview);
    grid.setAdapter(new ColorAadapter());
    return view;
  }


  private void initChoice() {

    grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        getDialog().dismiss();
        String theme = getThemeById(i);
        ThemeUtil.changeTheme(getActivity(), theme);
        SettingShared.setTheme(getActivity(), theme);
      }
    });
  }

  private String getThemeById(int i) {
    if (i == 0){
      return ThemeUtil.INDIGO;
    } else if(i == 1){
      return ThemeUtil.RED;
    } else if(i == 2){
      return ThemeUtil.CYAN;
    } else if(i == 3){
      return ThemeUtil.BLACK;
    }
    return ThemeUtil.INDIGO;
  }

  private class ColorAadapter extends BaseAdapter {

    int[] colors = new int[]{
        R.color.colorPrimary_indigo, R.color.colorPrimary_red,
        R.color.colorPrimary_cyan, R.color.colorPrimary_black
    };

    ColorAadapter(){
      super();
    }

    @Override public int getCount() {
      return colors.length;
    }

    @Override public Object getItem(int i) {
      return colors[i];
    }

    @Override public long getItemId(int i) {
      return i;
    }

    @Override public View getView(int i, View view, ViewGroup viewGroup) {
      View v = View.inflate(getContext(), R.layout.color_item, null);
      CircleImageView circle = v.findViewById(R.id.color);
      circle.setImageDrawable(new ColorDrawable(getContext().getResources().getColor(colors[i])));
      return v;
    }
  }
}
