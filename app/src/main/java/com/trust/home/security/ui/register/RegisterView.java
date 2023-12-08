package com.trust.home.security.ui.register;

import com.trust.home.security.base.BaseView;

public interface RegisterView extends BaseView {
    void onRegisterSuccess();
    void onRegisterFailure(String message);
}
