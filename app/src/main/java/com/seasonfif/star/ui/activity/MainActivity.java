package com.seasonfif.star.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.seasonfif.star.R;
import com.seasonfif.star.ui.fragment.BaseFragment;
import com.seasonfif.star.utils.ThemeUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_star:
                            pager.setCurrentItem(0);
                            ThemeUtil.changeTheme(MainActivity.this, ThemeUtil.INDIGO);
                            return true;
                        case R.id.navigation_order:
                            pager.setCurrentItem(1);
                            ThemeUtil.changeTheme(MainActivity.this, ThemeUtil.CYAN);
                            return true;
                        case R.id.navigation_setting:
                            pager.setCurrentItem(2);
                            ThemeUtil.changeTheme(MainActivity.this, ThemeUtil.BLACK);
                            return true;
                    }
                    return false;
                }
            };

    private MenuItem menuItem;

    private ViewPager.OnPageChangeListener pageChangeLinstener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (menuItem != null) {
                menuItem.setChecked(false);
            } else {
                navigation.getMenu().getItem(0).setChecked(false);
            }
            menuItem = navigation.getMenu().getItem(position);
            menuItem.setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private ViewPagerAdapter pagerAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupViewPager();

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //3个以上会有位移效果
//        BottomNavigationViewHelper.disableShiftMode(navigation);
    }

    private void setupViewPager() {
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(BaseFragment.newInstance("111"));
        pagerAdapter.addFragment(BaseFragment.newInstance("222"));
        pagerAdapter.addFragment(BaseFragment.newInstance("333"));
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(pageChangeLinstener);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{

        private BaseFragment current;
        private final List<Fragment> fragments = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment){
            fragments.add(fragment);
        }

        public BaseFragment getCurrent() {
            return current;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            current = (BaseFragment) object;
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
    }
}


