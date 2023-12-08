package com.trust.home.security.ui.splash;

import android.view.LayoutInflater;

import androidx.core.splashscreen.SplashScreen;
import androidx.viewbinding.ViewBinding;

import com.trust.home.security.base.BaseActivity;
import com.trust.home.security.base.BasePresenter;
import com.trust.home.security.base.BaseView;
import com.trust.home.security.ui.login.LoginActivity;

public class SplashActivity extends BaseActivity {

    @Override
    protected void initViews() {

    }

    @Override
    protected ViewBinding binding(LayoutInflater inflater) {
        return null;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected BaseView attachPresenter() {
        return null;
    }

    @Override
    protected void setupSplashScreen(SplashScreen splash) {
        splash.setOnExitAnimationListener(splashScreenViewProvider -> {
            pushActivityAndFinish(LoginActivity.class);
        });
    }

    @Override
    protected void initActions() {

    }
}
