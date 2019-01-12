package com.xing.imageselector.interfaces;

import android.widget.ImageView;

import java.io.Serializable;

/**
 * 图片加载引擎类
 */
public interface ImageEngine extends Serializable {

    void displayImage(String path, ImageView imageView);
}
