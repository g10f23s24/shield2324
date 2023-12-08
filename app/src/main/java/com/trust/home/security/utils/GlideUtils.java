package com.trust.home.security.utils;

import android.util.Base64;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.trust.home.security.R;

public class GlideUtils {
    public static void loadAvatar(ImageView imageView, String path) {
        Glide.with(imageView.getContext())
                .load(path)
                .placeholder(R.drawable.ic_default_avatar)
                .into(imageView);
    }
}
