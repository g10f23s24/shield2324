package com.trust.home.security.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.trust.home.security.database.entity.User;

public class AppPrefsManager {
    private static final String APP_PREFS_NAME = "APP_PREFS_NAME";
    private static final String USER_PREFS = "USER_PREFS";
    private static AppPrefsManager INSTANCE;
    private final Context context;
    private final Gson gson = new Gson();
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    private AppPrefsManager(Context context) {
        this.context = context;

        prefs = context.getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public static synchronized void initialize(Context context) {
        if(INSTANCE == null) {
            INSTANCE = new AppPrefsManager(context);
        }
    }

    public static synchronized AppPrefsManager getInstance() {
        return INSTANCE;
    }

    public void putUser(User user) {
        putString(USER_PREFS, gson.toJson(user));
    }

    public User getUser() {
        String userString = getString(USER_PREFS, "");
        if(StringUtils.valid(userString)) {
            return gson.fromJson(userString, User.class);
        } else return null;
    }

    private void putString(String key, String value) {
        if (editor == null) {
            throw new NullPointerException("ERROR_MSG");
        } else {
            editor.putString(key, value);
            editor.commit();
        }
    }

    private String getString(String key, String defaultValue) {
        if (prefs == null) {
            throw new NullPointerException("ERROR_MSG");
        } else {
            String result = prefs.getString(key, "");
            if(StringUtils.valid(result)) {
                return result;
            } else return defaultValue;
        }
    }

    public void logout() {
        editor.remove(USER_PREFS);
        editor.commit();
    }
}
