package com.trust.home.security.ui.login;

import com.trust.home.security.base.BaseView;

public interface LoginView extends BaseView {
    void onLoginSuccess();
    void goToRegisterFace();
    void onLoginFailure(String message);
}
