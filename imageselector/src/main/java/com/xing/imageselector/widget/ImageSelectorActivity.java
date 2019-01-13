package com.xing.imageselector.widget;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xing.imageselector.ImageSelector;
import com.xing.imageselector.R;
import com.xing.imageselector.adapter.ImageAdapter;
import com.xing.imageselector.config.ImageConfig;
import com.xing.imageselector.entity.Folder;
import com.xing.imageselector.entity.Image;
import com.xing.imageselector.interfaces.OnImageLoadCallback;
import com.xing.imageselector.provider.ImageLoadTask;
import com.xing.imageselector.utils.Constants;
import com.xing.imageselector.utils.PackageUtil;
import com.xing.imageselector.utils.StatusBarUtil;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * 图片选择器 activity
 */
public class ImageSelectorActivity extends FragmentActivity implements
        BottomDialogFragment.OnFolderSelectedListener, View.OnClickListener {

    private static final int PERMISSION_STORAGE_CODE = 100;
    private static final int PERMISSION_CAMERA_CODE = 101;
    private static final int REQUEST_TAKE_PHOTO = 200;
    private static final int REQUEST_IMAGE_PREVIEW = 201;

    private ImageView backView;
    private TextView confirmTextView;
    private TextView titleTextView;
    private TextView bottomFolderNameTxtView;
    private RecyclerView recyclerView;
    private ImageConfig imageConfig;
    private ImageAdapter imageAdapter;
    private int selectMode;
    private ArrayList<Folder> folderList = new ArrayList<>();
    private int curSelectedPosition;
    private View titleBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selector);
        initView();
        initData();
    }

    /**
     * 初始化 view
     */
    private void initView() {
        titleBarLayout = findViewById(R.id.rl_title_layout);
        backView = findViewById(R.id.iv_imageselector_back);
        confirmTextView = findViewById(R.id.tv_iv_imageselector_confirm);
        titleTextView = findViewById(R.id.tv_imageselector_title);
        bottomFolderNameTxtView = findViewById(R.id.tv_bottom_foler_name);
        recyclerView = findViewById(R.id.rv_bottom_gallery);
    }

    private void initData() {
        imageConfig = (ImageConfig) getIntent().getSerializableExtra(Constants.CONFIG);
        int statusBarColor = imageConfig.getStatusBarColor();
        int titleBarColor = imageConfig.getTitleBarColor();
        int titleBarTextColor = imageConfig.getTitleBarTextColor();
        if (statusBarColor != 0) {
            StatusBarUtil.setColor(this, getResources().getColor(statusBarColor));
        }
        if (titleBarColor != 0) {
            titleBarLayout.setBackgroundColor(getResources().getColor(titleBarColor));
        }
        if (titleBarTextColor != 0) {
            titleTextView.setTextColor(getResources().getColor(titleBarTextColor));
        }
        selectMode = imageConfig.getSelectMode();
        if (selectMode == ImageConfig.MODE_SINGLE) {
            confirmTextView.setVisibility(View.GONE);
        } else {
            confirmTextView.setVisibility(View.VISIBLE);
        }
        // 进入时显示全部图片
        titleTextView.setText("全部图片");
        bottomFolderNameTxtView.setText("全部图片");
        recyclerView.setLayoutManager(new GridLayoutManager(this, imageConfig.getColumn()));
        imageAdapter = new ImageAdapter(this, imageConfig);
        recyclerView.setAdapter(imageAdapter);

        // Android 6.0 动态权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int storagePermissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            // 没有授权
            if (storagePermissionState != PackageManager.PERMISSION_GRANTED) {
                // 申请权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE_CODE);
            } else {
                getImagesList();
            }
        } else {
            getImagesList();
        }

        setListener();
    }

    /**
     * 设置监听
     */
    private void setListener() {
        backView.setOnClickListener(this);
        confirmTextView.setOnClickListener(this);
        bottomFolderNameTxtView.setOnClickListener(this);
        // 打开相机
        imageAdapter.setOnCameraClickListener(new ImageAdapter.OnCameraClickListener() {
            @Override
            public void onCameraClick() {
                takePhoto();
            }
        });
        // 选择照片，预览
        imageAdapter.setOnImageClickListener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(Image image, int position) {
                if (selectMode == ImageConfig.MODE_SINGLE) {   // 单选裁剪
                    if (imageConfig.isNeedCrop()) {
                        // 进入裁剪页面
                        crop(image.getPath(), UCrop.REQUEST_CROP);
                    } else {     // 单选，不裁剪,直接返回路径
                        Intent intent = new Intent();
                        intent.putExtra(ImageSelector.RESULT_SINGLE, image.getPath());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } else {    // 多选
                    // 多选点击图片时，进入预览界面
                    gotoImagePreviewActivity(imageAdapter.getImageList(), imageAdapter.getImageSelectedList(), position);
                }
            }
        });
        // 选中，取消选中的回调
        imageAdapter.setOnImageSelectedListener(new ImageAdapter.OnImageSelectedListener() {
            @Override
            public void onImageSelected(Image image, int selectCount, boolean isSelected) {
                if (selectCount > 0) {
                    confirmTextView.setText("确定(" + selectCount + ")");
                } else {
                    confirmTextView.setText("确定");
                }
                if (selectCount > 0) {
                    confirmTextView.setEnabled(true);
                } else {
                    confirmTextView.setEnabled(false);
                }
            }
        });
    }

    /**
     * 跳转到图片预览界面
     */
    private void gotoImagePreviewActivity(ArrayList<Image> images, ArrayList<Image> selectedImages, int position) {
        Intent intent = new Intent(this, ImagePreviewActivity.class);
        intent.putExtra(Constants.CONFIG, imageConfig);
        intent.putParcelableArrayListExtra(Constants.IMAGE_LIST, images);
        intent.putParcelableArrayListExtra(Constants.IMAGE_SELECTED_LIST, selectedImages);
        intent.putExtra(Constants.POSITION, position);
        startActivityForResult(intent, REQUEST_IMAGE_PREVIEW);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_imageselector_back) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (v.getId() == R.id.tv_bottom_foler_name) {
            BottomDialogFragment dialogFragment = BottomDialogFragment.newsInstance(imageConfig, folderList, curSelectedPosition);
            dialogFragment.show(getSupportFragmentManager(), "dialog");
        } else if (v.getId() == R.id.tv_iv_imageselector_confirm) {
            ArrayList<Image> selectedImageList = imageAdapter.getImageSelectedList();
            // 将 Image 类型转成 path
            ArrayList<String> selectedPathList = convertSelectedImageList(selectedImageList);
            Intent intent = new Intent();
            intent.putStringArrayListExtra(ImageSelector.RESULT_MULTI, selectedPathList);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    /**
     * 将 Image 类型转成 path
     *
     * @param selectedImageList
     */
    private ArrayList<String> convertSelectedImageList(ArrayList<Image> selectedImageList) {
        ArrayList<String> pathList = new ArrayList<>();
        if (selectedImageList == null) {
            return pathList;
        }
        for (Image image : selectedImageList) {
            pathList.add(image.getPath());
        }
        return pathList;
    }


    /**
     * 裁剪
     *
     * @param path
     * @param requestCode
     */
    private void crop(String path, int requestCode) {
        //选择之后剪切
        Uri selectUri = Uri.fromFile(new File(path));
        SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String imageName = timeFormatter.format(new Date());
        UCrop uCrop = UCrop.of(selectUri, Uri.fromFile(new File(getCacheDir(), imageName + ".jpg")));
        UCrop.Options options = new UCrop.Options();
//        if (cropMode == 2) {
//            options.setCircleDimmedLayer(true);//是否显示圆形裁剪的layer
//            options.setShowCropGrid(false);//是否显示分割线
//            options.setShowCropFrame(false);//是否显示矩形边框
//        }
//        options.setCircleDimmedLayer(true);//是否显示圆形裁剪的layer
//        options.setDimmedLayerColor();//设置圆形的背景色
//        options.setShowCropGrid(false);//是否显示分割线
//        options.setCropGridColor();//设置分割线的颜色
//        options.setCropGridStrokeWidth();
//        options.setCropGridColumnCount();//设置分割线的列数
//        options.setCropGridRowCount();//设置分割线的行数
//        options.setShowCropFrame(false);//是否显示矩形边框
//        options.setCropFrameStrokeWidth();//设置矩形边框的宽度
//        options.setCropFrameColor();//设置矩形边框的颜色
        options.setFreeStyleCropEnabled(true);//设置裁剪框可移动，具体可以设置为true运行看效果
//        options.setMaxScaleMultiplier();//设置图片放大的倍数，必须大于1
        options.setHideBottomControls(true);//是否显示底部控制菜单
//        options.setToolbarCropDrawable();//设置裁剪确定按钮的背景图片
//        options.setToolbarCancelDrawable();//设置裁剪取消按钮的背景图片
//        options.setImageToCropBoundsAnimDuration();//设置图片移动到矩形框的动画时间单位毫秒数
//        options.setToolbarWidgetColor();//设置toobar的view的颜色为透明颜色
//        options.setCompressionFormat();//设置裁剪之后的图片的格式
//        options.setRootViewBackgroundColor();//设置裁剪页面的根布局的颜色
//        options.withAspectRatio();//设置图片左右拉伸的长度
//        options.withMaxResultSize();//值小了会模糊
        options.setToolbarTitle("");
        options.setToolbarColor(Color.BLACK);
        options.setStatusBarColor(Color.BLACK);
        options.setActiveWidgetColor(Color.BLACK);
        options.setCompressionQuality(100);
        uCrop.withOptions(options);
        uCrop.start(ImageSelectorActivity.this, requestCode);
    }


    /**
     * 调用系统相机拍照
     */
    private void takePhoto() {
        // Android 6.0 以上,动态申请相机权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int cameraPermissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            // 没有授权
            if (cameraPermissionState != PackageManager.PERMISSION_GRANTED) {
                // 申请相机权限
                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_CODE);
            } else {
                openCamera();
            }
        } else {
            openCamera();
        }
    }

    /**
     * 打开系统相机
     */
    public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        // 拍照产生的图片的父路径
        File storageDir = new File(Environment.getExternalStorageDirectory(), "ImageSelector");
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date()) + ".jpg";
        File imageFile = new File(storageDir, fileName);
        if (imageFile.exists()) {
            imageFile.delete();
        }
        Uri fileUri;
        // Android 7.0 以上，使用 fileprovider 方式共享文件
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 第二个参数 authority 需要和 AndroidManifest.xml 中 android:authorities 属性值保持一致
            String authority = PackageUtil.getPackageName(this) + ".fileprovider";
            fileUri = FileProvider.getUriForFile(this, authority, imageFile);
        } else {
            // 文件地址转换成 uri
            fileUri = Uri.fromFile(imageFile);
        }
        // 设置系统相机拍照完成后，图片的存放地址
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }


    /**
     * 权限申请的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_STORAGE_CODE) {
            // 同意授权
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImagesList();
            } else {
                Toast.makeText(this, "Sdcard 权限拒绝,可在设置中打开", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_CAMERA_CODE) {
            // 同意授权
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "相机权限拒绝,可在设置中打开", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 获取图片
     */
    private void getImagesList() {
        ImageLoadTask imageLoadTask = new ImageLoadTask(this);
        imageLoadTask.setOnLoadImageCallback(new OnImageLoadCallback() {
            @Override
            public void onSuccess(ArrayList<Folder> folders) {
                // 初始化文件夹列表集合
                folderList = folders;
                // 文件夹集合中的第一个元素为 "全部图片"
                ArrayList<Image> imageList = folders.get(0).getImageList();
                imageAdapter.setData(imageList, true);
            }
        });
        imageLoadTask.execute();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:     // 拍照返回
                    if (data != null) {

                    }
                    break;
                case UCrop.REQUEST_CROP:     // 裁剪返回
                    //选择之后裁剪，获取到裁剪的数据直接返回
                    if (data != null) {
                        setResult(RESULT_OK, data);
                        finish();
                    } else {
//                        //如果选择之后没有裁剪 用户按返回键的话，那么data就是null, 做下判断，就刷新当前页面。
//                        mAdapter.notifyDataSetChanged();
//                        setSelectImageCount(mAdapter.getSelectImages().size());
                    }
                    break;
                case REQUEST_IMAGE_PREVIEW:
                    if (data != null) {
                        ArrayList<Image> selectedImages = data.getParcelableArrayListExtra(Constants.IMAGE_SELECTED_LIST);
                        // 如果点击的是确定，则直接返回给调用方,否则刷新界面，将新增，取消选中的更新到界面上
                        boolean isConfirm = data.getBooleanExtra(Constants.IS_CONFIRM, false);
                        if (isConfirm) {
                            // 将 Image 类型转成 path
                            ArrayList<String> selectedPathList = convertSelectedImageList(selectedImages);
                            Intent intent = new Intent();
                            intent.putStringArrayListExtra(ImageSelector.RESULT_MULTI, selectedPathList);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            if (selectedImages != null && selectedImages.size() > 0) {
                                confirmTextView.setEnabled(true);
                                confirmTextView.setText("确定(" + selectedImages.size() + ")");
                            } else {
                                confirmTextView.setText("确定");
                            }
                            imageAdapter.setImageSelectedList(selectedImages);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 接收到 fragment 传递过来的数据
     *
     * @param position
     */
    @Override
    public void onFolderSelected(int position) {
        if (position < 0 || position > folderList.size() - 1) {
            return;
        }
        this.curSelectedPosition = position;
        bottomFolderNameTxtView.setText(folderList.get(curSelectedPosition).getName());
        titleTextView.setText(folderList.get(curSelectedPosition).getName());
        imageAdapter.setData(folderList.get(curSelectedPosition).getImageList(), curSelectedPosition == 0);
    }
}
