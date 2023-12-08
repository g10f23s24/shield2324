package com.trust.home.security.ui.main;

import android.view.LayoutInflater;

import androidx.viewpager.widget.ViewPager;

import com.trust.home.security.base.BaseActivity;
import com.trust.home.security.base.BasePagerAdapter;
import com.trust.home.security.databinding.ActivityMainBinding;
import com.trust.home.security.ui.home.HomeFragment;
import com.trust.home.security.ui.notification.NotificationFragment;
import com.trust.home.security.widgets.BottomNavigation;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainPresenter, MainView> implements MainView {
    private BasePagerAdapter pagerAdapter;
    @Override
    protected MainPresenter getPresenter() {
        return new MainPresenter();
    }

    @Override
    protected ActivityMainBinding binding(LayoutInflater inflater) {
        return ActivityMainBinding.inflate(inflater);
    }

    @Override
    protected MainView attachPresenter() {
        return this;
    }

    @Override
    protected void initViews() {
        pagerAdapter = new BasePagerAdapter(getSupportFragmentManager());
        pagerAdapter.setFragments(
                HomeFragment.newInstance(HomeFragment.class),
                NotificationFragment.newInstance(NotificationFragment.class)
        );

        mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mBinding.bottomNav.swipeItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mBinding.viewPager.setAdapter(pagerAdapter);
        mBinding.bottomNav.setListener(new BottomNavigation.NavigationListener() {
            @Override
            public void onHomeClick() {
                mBinding.viewPager.setCurrentItem(0, true);
            }

            @Override
            public void onNotificationClick() {
                mBinding.viewPager.setCurrentItem(1, true);
            }
        });
    }

    @Override
    protected void initActions() {

    }
}