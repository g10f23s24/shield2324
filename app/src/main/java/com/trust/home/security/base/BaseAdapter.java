package com.trust.home.security.base;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseAdapter <T, BD extends ViewBinding> extends RecyclerView.Adapter<BaseViewHolder<BD>> {
    private final List<T> items = new ArrayList<>();
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public void setData(List<T> newData) {
        items.clear();
        items.addAll(newData);
        mHandler.post(this::notifyDataSetChanged);
    }

    public void setData(T... newData) {
        items.clear();
        items.addAll(Arrays.asList(newData));
        mHandler.post(this::notifyDataSetChanged);
    }

    @NonNull
    @Override
    public BaseViewHolder<BD> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new BaseViewHolder<>(getViewBinding(inflater, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<BD> holder, int position) {
        bindView(holder.binding, items.get(position), position);
    }

    protected abstract void bindView(BD binding, T itemData, int position);
    protected abstract BD getViewBinding(LayoutInflater inflater, ViewGroup parent);

    @Override
    public int getItemCount() {
        return items.size();
    }
}
