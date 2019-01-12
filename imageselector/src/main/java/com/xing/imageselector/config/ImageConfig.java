package com.xing.imageselector.config;


import com.xing.imageselector.interfaces.ImageEngine;

import java.io.Serializable;

/**
 * 文件夹选择器配置类
 */
public class ImageConfig implements Serializable {

    /**
     * 单选
     */
    public static final int MODE_SINGLE = 0x10;
    /**
     * 多选
     */
    public static final int MODE_MULTI = 0x11;
    /**
     * 状态栏颜色
     */
    private int statusBarColor;
    /**
     * 标题栏背景颜色
     */
    private int titleBarColor;
    /**
     * 标题栏文字颜色
     */
    private int titleBarTextColor;
    /**
     * 是否显示拍照按钮
     */
    private boolean showCamera;
    /**
     * 图片选择模式，只有两种： 单选， 多选
     */
    private int selectMode;
    /**
     * 多选模式下，最多能选几张
     */
    private int maxSelectable;
    /**
     * 选择之后，是否需要裁剪
     */
    private boolean needCrop;
    /**
     * 列表显示的列数
     */
    private int column;
    /**
     * 图片加载引擎类
     */
    private ImageEngine imageEngine;
    /**
     * 相机 icon 资源id
     */
    private int cameraResId;
    /**
     * 底部菜单背景颜色
     */
    private int bottomMenuBackgroundColor;
    /**
     * 底部菜单文字颜色
     */
    private int bottomMenuTextColor;


    public static class Builder implements Serializable {
        int statusBarColor;
        int titleBarColor;
        int titleBarTextColor;
        boolean showCamera;
        int selectMode;
        int maxSelectable;
        boolean needCrop;
        int column = 3;
        ImageEngine imageEngine;
        int cameraResId;

        public Builder(ImageEngine imageEngine) {
            this.imageEngine = imageEngine;
        }

        /**
         * 设置状态栏颜色
         *
         * @param statusBarColor
         * @return
         */
        public Builder statusBarColor(int statusBarColor) {
            this.statusBarColor = statusBarColor;
            return this;
        }

        /**
         * 设置标题栏背景颜色
         *
         * @param titleBarColor
         * @return
         */
        public Builder titleBarColor(int titleBarColor) {
            this.titleBarColor = titleBarColor;
            return this;
        }

        /**
         * 设置标题栏文字颜色
         *
         * @param titleBarTextColor
         * @return
         */
        public Builder titleBarTextColor(int titleBarTextColor) {
            this.titleBarTextColor = titleBarTextColor;
            return this;
        }

        /**
         * 设置是否显示拍照按钮
         *
         * @param showCamera
         * @return
         */
        public Builder showCamera(boolean showCamera) {
            this.showCamera = showCamera;
            return this;
        }

        /**
         * 设置图片选择模式
         *
         * @param selectMode
         * @return
         */
        public Builder selectMode(int selectMode) {
            if (selectMode != MODE_SINGLE && selectMode != MODE_MULTI) {
                throw new IllegalArgumentException("The select mode must be ImageConfig.MODE_SINGLE or ImageConfig.MODE_MULTI");
            }
            this.selectMode = selectMode;
            return this;
        }

        /**
         * 设置最大选择数量
         *
         * @param maxSelectable
         * @return
         */
        public Builder maxSelectable(int maxSelectable) {
            if (maxSelectable < 1) {
                throw new IllegalArgumentException("maxSelect can't be set low than 0");
            }
            this.maxSelectable = maxSelectable;
            return this;
        }

        /**
         * 设置是否需要裁剪
         *
         * @param needCrop
         * @return
         */
        public Builder needCrop(boolean needCrop) {
            this.needCrop = needCrop;
            return this;
        }

        public Builder column(int column) {
            this.column = column;
            return this;
        }

        public ImageConfig build() {
            ImageConfig imageConfig = new ImageConfig();
            imageConfig.statusBarColor = statusBarColor;
            imageConfig.titleBarColor = titleBarColor;
            imageConfig.titleBarTextColor = titleBarTextColor;
            imageConfig.showCamera = showCamera;
            imageConfig.selectMode = selectMode;
            imageConfig.maxSelectable = maxSelectable;
            imageConfig.needCrop = needCrop;
            imageConfig.column = column;
            imageConfig.imageEngine = imageEngine;
            imageConfig.cameraResId = cameraResId;
            return imageConfig;
        }
    }

    public int getStatusBarColor() {
        return statusBarColor;
    }

    public int getTitleBarColor() {
        return titleBarColor;
    }

    public int getTitleBarTextColor() {
        return titleBarTextColor;
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    public int getSelectMode() {
        return selectMode;
    }

    public int getMaxSelectable() {
        return maxSelectable;
    }

    public boolean isNeedCrop() {
        return needCrop;
    }

    public int getColumn() {
        return column;
    }

    public ImageEngine getImageEngine() {
        return imageEngine;
    }

    public int getCameraResId() {
        return cameraResId;
    }
}
