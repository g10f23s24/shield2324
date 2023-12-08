package com.trust.home.security.base;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

public class BaseViewHolder <BD extends ViewBinding> extends RecyclerView.ViewHolder {
    protected final BD binding;

    public BaseViewHolder(BD binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
