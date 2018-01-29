package com.seasonfif.star.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.seasonfif.star.utils.ThemeUtil;

/**
 * Created by lxy on 2018/1/27.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(ThemeUtil.getTheme(this));
        super.onCreate(savedInstanceState);
    }
}