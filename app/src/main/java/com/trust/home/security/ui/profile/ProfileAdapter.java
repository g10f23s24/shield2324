package com.trust.home.security.ui.profile;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.trust.home.security.base.BaseAdapter;
import com.trust.home.security.databinding.ItemProfileBinding;
import com.trust.home.security.network.data.response.HomeData;

public class ProfileAdapter extends BaseAdapter<HomeData, ItemProfileBinding> {
    @Override
    protected void bindView(ItemProfileBinding binding, HomeData itemData, int position) {
        binding.tvStatus.setText(itemData.getName());
        binding.tvLocation.setText(itemData.getLocation());
        binding.tvDate.setText(itemData.getCreatedAt());
    }

    @Override
    protected ItemProfileBinding getViewBinding(LayoutInflater inflater, ViewGroup parent) {
        return ItemProfileBinding.inflate(inflater, parent, false);
    }
}
