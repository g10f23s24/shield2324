package com.trust.home.security.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.trust.home.security.HomeSecurityApplication;

public class Preferences {
    private static Preferences INSTANCE;
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;
    private final Gson gson = new Gson();

    private Preferences() {
        prefs = HomeSecurityApplication
                .instance.getSharedPreferences(
                        Preferences.class.getName(), Context.MODE_PRIVATE
                );
        editor = prefs.edit();
    }

    public static synchronized Preferences getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Preferences();
        }
        return INSTANCE;
    }

    public <T> void put(String key, T value) {
        if(value instanceof String) {
            editor.putString(key, (String) value);
        } else if(value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if(value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if(value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if(value instanceof Long) {
            editor.putLong(key, (Long) value);
        }
    }

    public <T> T get(Class<T> clz, String key) {
        if(String.class.isAssignableFrom(clz)) {
            return (T) prefs.getString(key, "");
        } else if(Long.class.isAssignableFrom(clz)) {
            return (T) ((Long) prefs.getLong(key, 0));
        } else if(Float.class.isAssignableFrom(clz)) {
            return (T) ((Float) prefs.getFloat(key, 0f));
        }  else if(Integer.class.isAssignableFrom(clz)) {
            return (T) ((Integer) prefs.getInt(key, 0));
        }  else if(Boolean.class.isAssignableFrom(clz)) {
            return (T) ((Boolean) prefs.getBoolean(key, false));
        } else return gson.fromJson(get(String.class, key), clz);
    }
}