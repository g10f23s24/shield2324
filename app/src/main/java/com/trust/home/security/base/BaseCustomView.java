package com.trust.home.security.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

public abstract class BaseCustomView <BD extends ViewBinding> extends LinearLayout {
    protected abstract BD getBinding(LayoutInflater inflater, ViewGroup viewGroup);
    protected BD mBinding;

    public BaseCustomView(Context context) {
        super(context);
        init(context, null);
    }

    public BaseCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BaseCustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mBinding = getBinding(LayoutInflater.from(context), this);
        initViews(context, attrs);
        initActions();
    }

    protected abstract void initViews(Context context, AttributeSet attrs);
    protected abstract void initActions();
}
