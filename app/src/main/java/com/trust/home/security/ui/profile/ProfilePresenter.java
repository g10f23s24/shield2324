package com.trust.home.security.ui.profile;

import com.trust.home.security.base.BasePresenter;
import com.trust.home.security.database.entity.User;
import com.trust.home.security.utils.AppPrefsManager;
import com.trust.home.security.utils.StringUtils;

import kotlinx.coroutines.flow.Flow;

public class ProfilePresenter extends BasePresenter<ProfileView> {
    public Flow<User> userFlow() {
        return database.selectFlowUserWhere(
                AppPrefsManager.getInstance().getUser().getUserName()
        );
    }

    public void logout() {
        AppPrefsManager.getInstance().logout();
    }

    public User getUserLoggedIn() {
        return AppPrefsManager.getInstance().getUser();
    }
}
