package com.trust.home.security.ui.loginWithFaceId;

import com.trust.home.security.base.BasePresenter;
import com.trust.home.security.utils.AppPrefsManager;

public class LoginWithFaceIdPresenter extends BasePresenter<LoginWithFaceIdView> {
    public void logout() {
        AppPrefsManager.getInstance().logout();
    }
}
