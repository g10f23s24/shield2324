package com.trust.home.security.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.trust.home.security.base.BaseCustomView;
import com.trust.home.security.databinding.LayoutUserToolbarBinding;
import com.trust.home.security.utils.GlideUtils;

public class UserToolBar extends BaseCustomView<LayoutUserToolbarBinding> {
    private ToolBarListener listener;

    public void setListener(ToolBarListener listener) {
        this.listener = listener;
    }

    public UserToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected LayoutUserToolbarBinding getBinding(LayoutInflater inflater, ViewGroup viewGroup) {
        return LayoutUserToolbarBinding.inflate(inflater, viewGroup, true);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs) {

    }

    public void updateAvatar(String avatar) {
        GlideUtils.loadAvatar(mBinding.imAvatar, avatar);
    }

    @Override
    protected void initActions() {
        mBinding.imAvatar.setOnClickListener(v -> {
            if(listener != null) {
                listener.onOpenProfile();
            }
        });
    }

    public interface ToolBarListener {
        void onOpenProfile();
    }
}
