package com.trust.home.security.ui.login;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.trust.home.security.R;
import com.trust.home.security.base.BaseActivity;
import com.trust.home.security.databinding.ActivityLoginBinding;
import com.trust.home.security.helpers.events.LoginFaceSuccess;
import com.trust.home.security.ui.loginWithFaceId.LoginWithFaceIdFragment;
import com.trust.home.security.ui.register.RegisterFragment;
import com.trust.home.security.ui.registerFaceId.RegisterFaceIdFragment;
import com.trust.home.security.utils.AppPrefsManager;
import com.trust.home.security.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LoginActivity extends BaseActivity<ActivityLoginBinding, LoginPresenter, LoginView> implements LoginView {
    @Override
    protected LoginPresenter getPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected ActivityLoginBinding binding(LayoutInflater inflater) {
        return ActivityLoginBinding.inflate(inflater);
    }

    @Override
    protected LoginView attachPresenter() {
        return this;
    }

    @Override
    protected void initViews() {
        registerEvents();
        mBinding.tvRegister.setMovementMethod(LinkMovementMethod.getInstance());
        mBinding.tvRegister.setText(getRegisterSpan());
        mPresenter.checkUserLoggedIn();
    }

    @Override
    protected void initActions() {
        mBinding.tvSignIn.setOnClickListener(v -> {
            String username = mBinding.edtUsername.getText().toString().trim();
            String password = mBinding.inputPassword.getPassword();

            if(StringUtils.valid(username) && StringUtils.valid(password)) {
                mPresenter.login(username, password);
            } else Toast.makeText(this, "You need to enter enough information", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void goToRegisterFace() {
        hideKeyboard();
        pushFragment(RegisterFaceIdFragment.newInstance(RegisterFaceIdFragment.class));
    }

    @Override
    public void onLoginFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private SpannableString getRegisterSpan() {
        String fullText = getString(R.string.login_create_account);
        String createAccount = "Create a account";
        SpannableString sp = new SpannableString(fullText);
        int start = fullText.indexOf(createAccount);
        int end = start + createAccount.length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                pushFragment(RegisterFragment.newInstance(RegisterFragment.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(true);
            }
        };
        sp.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
    }

    @Override
    public void onLoginSuccess() {
        hideKeyboard();
        pushFragment(LoginWithFaceIdFragment.newInstance(LoginWithFaceIdFragment.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecognizeSuccess(LoginFaceSuccess loginFaceSuccess) {
        finish();
    }

    @Override
    protected void onDestroy() {
        unregisterEvents();
        super.onDestroy();
    }
}
