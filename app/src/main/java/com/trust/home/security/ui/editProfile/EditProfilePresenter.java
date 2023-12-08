package com.trust.home.security.ui.editProfile;

import com.trust.home.security.base.BasePresenter;
import com.trust.home.security.database.entity.User;
import com.trust.home.security.utils.AppPrefsManager;

import kotlin.Unit;

public class EditProfilePresenter extends BasePresenter<EditProfileView> {
    private User currentUser;

    public long getUserLoggedId() {
        if(currentUser != null && currentUser.getId() != null) {
            return currentUser.getId();
        } else return -1L;
    }

    public void getLoggedUser() {
        User prefsUser = AppPrefsManager.getInstance().getUser();
        database.selectUserWhere(prefsUser.getUserName(), user -> {
            this.currentUser = user;
            view.onGetUserSuccess(user);
            return Unit.INSTANCE;
        });
    }

    public void saveNewUserData(
            String fullName,
            String age,
            int gender
    ) {
        if(currentUser != null) {
            currentUser.setFullName(fullName);
            currentUser.setAge(age);
            currentUser.setGender(gender);
            saveUser(currentUser);
        }
    }

    public void setAvatar(String avatar) {
        if(currentUser != null) {
            currentUser.setAvatar(avatar);
            saveUser(currentUser);
        }
    }

    private void saveUser(User user) {
        AppPrefsManager.getInstance().putUser(user);
        database.updateUser(user);
    }
}
