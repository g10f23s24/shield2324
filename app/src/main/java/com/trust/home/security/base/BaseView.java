package com.trust.home.security.base;

public interface BaseView {
    void showLoading();
    void hideLoading();
    void onError(String message);
    void onError(Exception exception);
}
