package com.trust.home.security.ui.notification;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.trust.home.security.base.BaseFragment;
import com.trust.home.security.databinding.FragmentNotificationBinding;
import com.trust.home.security.network.data.response.Notification;

public class NotificationFragment extends BaseFragment<FragmentNotificationBinding, NotificationPresenter, NotificationView> implements NotificationView {
    private final NotificationAdapter mAdapter = new NotificationAdapter();

    @Override
    protected FragmentNotificationBinding binding(LayoutInflater inflater, ViewGroup container) {
        return FragmentNotificationBinding.inflate(inflater, container, false);
    }

    @Override
    protected NotificationPresenter getPresenter() {
        return new NotificationPresenter();
    }

    @Override
    protected void initViews() {
        mAdapter.setData(
                new Notification("Stay Home is On", "10:15:11 AM"),
                new Notification("Arm is OFF", "11:15:11 AM"),
                new Notification("Intrusion warning", "02:15:11 AM"),
                new Notification("Face ID successfully", "10:15:11 AM"),
                new Notification("Face ID successfully", "06:00:00 PM"),
                new Notification("Face ID successfully", "08:30:10 AM"),
                new Notification("Stay Home is On", "10:15:11 AM"),
                new Notification("Arm is OFF", "11:15:11 AM"),
                new Notification("Intrusion warning", "02:15:11 AM"),
                new Notification("Face ID successfully", "10:15:11 AM"),
                new Notification("Face ID successfully", "06:00:00 PM"),
                new Notification("Face ID successfully", "08:30:10 AM")
        );
        mBinding.rvNotification.setAdapter(mAdapter);
    }

    @Override
    protected void initActions() {

    }

    @Override
    protected NotificationView attachView() {
        return this;
    }
}
