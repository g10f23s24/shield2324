package com.trust.home.security;

import android.app.Application;

import com.trust.home.security.database.DatabaseJobViewModel;
import com.trust.home.security.utils.AppPrefsManager;

public class HomeSecurityApplication extends Application {
    public static HomeSecurityApplication instance;
    public static DatabaseJobViewModel database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = new DatabaseJobViewModel(this);
        AppPrefsManager.initialize(this);
    }
}
