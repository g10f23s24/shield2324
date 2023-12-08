package com.trust.home.security.base;

import com.trust.home.security.HomeSecurityApplication;
import com.trust.home.security.database.DatabaseJobViewModel;
import com.trust.home.security.helpers.Preferences;

public class BasePresenter <V extends BaseView> {
    protected V view;
    protected final Preferences preferences;
    protected final DatabaseJobViewModel database;

    public BasePresenter() {
        preferences = Preferences.getInstance();
        database = HomeSecurityApplication.database;
    }

    public void onAttach(V view) {
        this.view = view;
    }

    public void onDetach() {

    }
}
