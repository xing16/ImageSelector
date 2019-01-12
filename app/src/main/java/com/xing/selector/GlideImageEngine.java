package com.xing.selector;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xing.imageselector.interfaces.ImageEngine;

/**
 * 使用 glide 进行加载
 */
public class GlideImageEngine implements ImageEngine {

    @Override
    public void displayImage(String path, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(path)
                .skipMemoryCache(false)
                .into(imageView);
    }
}
