package com.trust.home.security.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.trust.home.security.R;
import com.trust.home.security.base.BaseCustomView;
import com.trust.home.security.databinding.LayoutBottomNavigationBinding;

public class BottomNavigation extends BaseCustomView<LayoutBottomNavigationBinding> {
    private NavigationListener listener;
    private int itemSelectColor;
    private int itemUnSelectColor;

    public void setListener(NavigationListener listener) {
        this.listener = listener;
    }

    public BottomNavigation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected LayoutBottomNavigationBinding getBinding(LayoutInflater inflater, ViewGroup viewGroup) {
        return LayoutBottomNavigationBinding.inflate(inflater, viewGroup, true);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs) {
        itemSelectColor = ContextCompat.getColor(context, R.color.item_selected);
        itemUnSelectColor = ContextCompat.getColor(context, R.color.item_unselected);
    }

    @Override
    protected void initActions() {
        mBinding.tvHome.setOnClickListener(v -> {
            if(listener != null) {
                listener.onHomeClick();
            }
        });

        mBinding.tvNotification.setOnClickListener(v -> {
            if(listener != null) {
                listener.onNotificationClick();
            }
        });
    }

    public void swipeItem(int position) {
        mBinding.tvHome.setTextColor(position == 0 ? itemSelectColor : itemUnSelectColor);
        mBinding.tvNotification.setTextColor(position == 1 ? itemSelectColor : itemUnSelectColor);
    }

    public interface NavigationListener {
        void onHomeClick();
        void onNotificationClick();
    }
}
