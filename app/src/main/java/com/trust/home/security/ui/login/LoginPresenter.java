package com.trust.home.security.ui.login;

import com.trust.home.security.HomeSecurityApplication;
import com.trust.home.security.base.BasePresenter;
import com.trust.home.security.database.entity.User;
import com.trust.home.security.ui.loginWithFaceId.LoginWithFaceIdFragment;
import com.trust.home.security.utils.AppPrefsManager;

import kotlin.Unit;

public class LoginPresenter extends BasePresenter<LoginView> {
    public void login(String username, String password) {
        database.selectUserWhere(username, password, user -> {
            if(user != null) {
                AppPrefsManager.getInstance().putUser(user);
                checkUserRegisteredFace(username);
            } else view.onLoginFailure("username or password is incorrect");
            return Unit.INSTANCE;
        });
    }

    private void checkUserRegisteredFace(String username) {
        database.checkUserIsRegisteredFace(username, isRegistered -> {
            if (isRegistered) {
                view.onLoginSuccess();
            } else view.goToRegisterFace();
            return Unit.INSTANCE;
        });
    }

    public void checkUserLoggedIn() {
        User user = AppPrefsManager.getInstance().getUser();
        if(user != null) {
            checkUserRegisteredFace(user.getUserName());
        }
    }
}
