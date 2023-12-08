package com.trust.home.security.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.trust.home.security.R;
import com.trust.home.security.ui.splash.SplashActivity;
import com.trust.home.security.widgets.DialogMessage;

import org.greenrobot.eventbus.EventBus;

public abstract class BaseActivity<BD extends ViewBinding, P extends BasePresenter<V>, V extends BaseView> extends AppCompatActivity implements BaseView {
    protected abstract BD binding(LayoutInflater inflater);
    protected abstract P getPresenter();
    protected BD mBinding;
    protected P mPresenter;
    protected ProgressDialog mLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(this instanceof SplashActivity) {
            setupSplashScreen(SplashScreen.installSplashScreen(this));
        }
        super.onCreate(savedInstanceState);
        if (!(this instanceof SplashActivity)) {
            mBinding = binding(getLayoutInflater());
            setContentView(mBinding.getRoot());
            mPresenter = getPresenter();
            mPresenter.onAttach(attachPresenter());
            clearStatusBar();
            initViews();
            initActions();
        }
    }

    protected void setStatusBarColor() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @Override
    public void showLoading() {
        if(mLoading == null) {
            mLoading = new ProgressDialog(this, R.style.DialogTheme);
            mLoading.setCancelable(false);
            mLoading.setCanceledOnTouchOutside(false);
            mLoading.show();
        }
    }

    @Override
    public void hideLoading() {
        if(mLoading != null) {
            mLoading.dismiss();
            mLoading = null;
        }
    }

    protected void setupSplashScreen(SplashScreen splash) {}

    protected void clearStatusBar() {
//        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        getWindow().setStatusBarColor(Color.TRANSPARENT);
//        getWindow().getAttributes().flags = getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
    }

    protected int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    protected void pushFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.pull_in_right,
                        R.anim.push_out_left,
                        R.anim.pull_in_left,
                        R.anim.push_out_right
                )
                .add(android.R.id.content, fragment)
                .addToBackStack(null)
                .commit();
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected <T extends Activity> void pushActivity(Class<T> clz) {
        Intent intent = new Intent(this, clz);
        startActivity(intent);
    }

    protected <T extends Activity> void pushActivityAndFinish(Class<T> clz) {
        Intent intent = new Intent(this, clz);
        startActivity(intent);
        finish();
    }

    protected void showDialogMessage(String msg, DialogMessage.DialogMessageListener listener) {
        new DialogMessage(this, listener, msg).show();
    }

    protected abstract void initViews();
    protected abstract void initActions();
    protected abstract V attachPresenter();

    @Override
    public void onError(String message) {
        showDialogMessage(message, null);
    }

    @Override
    public void onError(Exception exception) {
        showDialogMessage(exception.getMessage(), null);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    protected void hideKeyboard() {
        if(this.getCurrentFocus() != null) {
            hideKeyboardInternal(this.getCurrentFocus());
        }
    }

    private void hideKeyboardInternal(View focusedView) {
        try {
            InputMethodManager imm = (InputMethodManager) focusedView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(
                    focusedView.getWindowToken(), 0
            );
        } catch (Exception e) {
          e.printStackTrace();
        }
    }

    protected void registerEvents() {
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    protected void unregisterEvents() {
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
