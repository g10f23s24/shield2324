package com.trust.home.security.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.trust.home.security.R;
import com.trust.home.security.base.BaseCustomView;
import com.trust.home.security.databinding.LayoutAppToolbarBinding;

public class AppToolBar extends BaseCustomView<LayoutAppToolbarBinding> {
    private AppToolbarStartIconListener startIconListener;
    private AppToolbarEndIconListener endIconListener;

    public void setStartIconListener(AppToolbarStartIconListener startIconListener) {
        this.startIconListener = startIconListener;
    }

    public void setEndIconListener(AppToolbarEndIconListener endIconListener) {
        this.endIconListener = endIconListener;
    }

    public AppToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected LayoutAppToolbarBinding getBinding(LayoutInflater inflater, ViewGroup viewGroup) {
        return LayoutAppToolbarBinding.inflate(inflater, viewGroup, true);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AppToolBar);
        String title = typedArray.getString(R.styleable.AppToolBar_atb_title);
        boolean visibleEndIcon = typedArray.getBoolean(R.styleable.AppToolBar_atb_visible_end_icon, false);
        typedArray.recycle();
        mBinding.tvTitle.setText(title);
        mBinding.imEnd.setVisibility(visibleEndIcon ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void initActions() {
        mBinding.imBack.setOnClickListener(v -> {
            if(startIconListener != null) {
                startIconListener.onStartIconClicked();
            }
        });

        mBinding.imEnd.setOnClickListener(v -> {
            if(endIconListener != null) {
                endIconListener.onEndIconClicked();
            }
        });
    }

    public interface AppToolbarStartIconListener {
        void onStartIconClicked();
    }

    public interface AppToolbarEndIconListener {
        void onEndIconClicked();
    }
}
