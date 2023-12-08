package com.trust.home.security.ui.editProfile;

import com.trust.home.security.base.BaseView;
import com.trust.home.security.database.entity.User;

public interface EditProfileView extends BaseView {
    void onGetUserSuccess(User user);
}
