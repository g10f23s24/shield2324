package com.trust.home.security.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import com.trust.home.security.R;

public abstract class BaseDialogFragment<BD extends ViewBinding> extends Dialog {
    protected abstract BD getBinding(LayoutInflater inflater);
    protected BD mBinding;

    public BaseDialogFragment(@NonNull Context context) {
        super(context, R.style.DialogSize90);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = getBinding(LayoutInflater.from(getContext()));
        setContentView(mBinding.getRoot());
        initViews();
    }

    protected abstract void initViews();
}
