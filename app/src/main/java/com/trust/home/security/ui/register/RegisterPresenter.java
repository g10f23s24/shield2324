package com.trust.home.security.ui.register;

import com.trust.home.security.HomeSecurityApplication;
import com.trust.home.security.base.BasePresenter;
import com.trust.home.security.database.entity.User;
import com.trust.home.security.utils.AppPrefsManager;
import com.trust.home.security.utils.Gender;

import kotlin.Unit;

public class RegisterPresenter extends BasePresenter<RegisterView> {
    public void createUser(String username, String password) {
        database.checkUserIsRegistered(username, isRegistered -> {
            if(isRegistered) {
                view.onRegisterFailure("userName has been registered");
            } else {
                User user = new User(null, username, password, "", "0", Gender.MALE.getValue(), "");
                database.insertUser(user);
                AppPrefsManager.getInstance().putUser(user);
                view.onRegisterSuccess();
            }

            return Unit.INSTANCE;
        });
    }
}
