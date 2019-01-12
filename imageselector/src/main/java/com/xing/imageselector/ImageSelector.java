package com.xing.imageselector;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.xing.imageselector.config.ImageConfig;
import com.xing.imageselector.entity.Image;
import com.xing.imageselector.utils.Constants;
import com.xing.imageselector.widget.ImagePreviewActivity;
import com.xing.imageselector.widget.ImageSelectorActivity;
import com.yalantis.ucrop.UCrop;

import java.util.ArrayList;
import java.util.List;


/**
 * 图片选择器类
 */
public class ImageSelector {

    /**
     * 单选结果回调
     */
    public static final String RESULT_SINGLE = "result_single";
    /**
     * 多选结果回调
     */
    public static final String RESULT_MULTI = "result_multi";


    /**
     * 打开图片选择 activity
     *
     * @param activity
     * @param config
     * @param requestCode
     */
    public static void open(Activity activity, ImageConfig config, int requestCode) {
        if (config == null) {
            return;
        }
        if (config.getImageEngine() == null) {
            throw new IllegalArgumentException("the image engine can't be null");
        }
        Intent intent = new Intent(activity, ImageSelectorActivity.class);
        intent.putExtra(Constants.CONFIG, config);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void open(Fragment fragment, ImageConfig config, int requestCode) {
        if (config == null) {
            return;
        }
        if (config.getImageEngine() == null) {
            throw new IllegalArgumentException("the image engine can't be null");
        }
        Intent intent = new Intent(fragment.getActivity(), ImageSelectorActivity.class);
        intent.putExtra(Constants.CONFIG, config);
        fragment.getActivity().startActivityForResult(intent, requestCode);
    }

    public static void open(android.app.Fragment fragment, ImageConfig config, int requestCode) {
        if (config == null) {
            return;
        }
        if (config.getImageEngine() == null) {
            throw new IllegalArgumentException("the image engine can't be null");
        }
        Intent intent = new Intent(fragment.getActivity(), ImageSelectorActivity.class);
        intent.putExtra(Constants.CONFIG, config);
        fragment.getActivity().startActivityForResult(intent, requestCode);
    }

    /**
     * 预览图片
     *
     * @param activity
     * @param config
     */
    public static void preview(Activity activity, ImageConfig config) {
        if (config == null) {
            return;
        }
        if (config.getImageEngine() == null) {
            throw new IllegalArgumentException("the image engine ca't be null");
        }
        Intent intent = new Intent(activity, ImagePreviewActivity.class);
        activity.startActivity(intent);
    }


    public static void preview(Activity activity, ImageConfig config, List<Image> images) {
        if (config == null) {
            return;
        }
        if (config.getImageEngine() == null) {
            throw new IllegalArgumentException("the image engine ca't be null");
        }
        Intent intent = new Intent(activity, ImagePreviewActivity.class);
        activity.startActivity(intent);
    }


    /**
     * 获取裁剪后的图片 uri
     *
     * @param intent
     * @return
     */
    public static Uri getCropUri(Intent intent) {
        if (intent == null) {
            return null;
        }
        return UCrop.getOutput(intent);
    }

    /**
     * 获取裁剪后的图片的 path
     *
     * @param intent
     * @return
     */
    public static String getCropPath(Intent intent) {
        Uri uri = getCropUri(intent);
        if (uri != null) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * 获取单选的图片路径 path
     *
     * @param intent
     * @return
     */
    public static String getSingleSelectPath(Intent intent) {
        if (intent == null) {
            return null;
        }
        return intent.getStringExtra(ImageSelector.RESULT_SINGLE);
    }

    /**
     * 获取多选的图片路径 path 的集合
     *
     * @param intent
     * @return
     */
    public static ArrayList<String> getMultiSelectPath(Intent intent) {
        if (intent == null) {
            return null;
        }
        return intent.getStringArrayListExtra(ImageSelector.RESULT_MULTI);
    }


}
