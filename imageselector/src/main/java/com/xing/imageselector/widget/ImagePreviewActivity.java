package com.xing.imageselector.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xing.imageselector.R;
import com.xing.imageselector.adapter.ImagePreviewAdapter;
import com.xing.imageselector.adapter.ImageThumbnailAdapter;
import com.xing.imageselector.config.ImageConfig;
import com.xing.imageselector.entity.Image;
import com.xing.imageselector.utils.Constants;
import com.xing.imageselector.utils.StatusBarUtil;

import java.util.ArrayList;


/**
 * 圖片预览类,预览界面的 imageconfig 可以与 选择界面配置不一样
 */
public class ImagePreviewActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = "ImagePreviewActivity";
    private RelativeLayout titleLayout;
    private ImageView backView;
    private TextView countTextView;
    private TextView confirmTextView;
    private LinearLayout bottomLayout;
    private CheckBox checkBox;
    private RecyclerView bottomRecyclerView;
    private RecyclerView previewRecyclerView;
    private ImageConfig imageConfig;
    private ImagePreviewAdapter imagePreviewAdapter;
    private ArrayList<Image> imageList = new ArrayList<>();
    private ArrayList<Image> imageSelectedList = new ArrayList<>();
    private int curPosition;
    private LinearLayoutManager previewLayoutManager;
    private int titleLayoutHeight;
    private int bottomLayoutHeight;
    // 当前标题栏，底部操作栏是否显示
    private boolean isTitleBottomLayoutShowing = true;
    /**
     * 底部缩略图
     */
    private ImageThumbnailAdapter imageThumbnailAdapter;
    private int titleBarColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        titleLayout = findViewById(R.id.rl_preview_title_layout);
        backView = findViewById(R.id.iv_preview_back);
        countTextView = findViewById(R.id.tv_count);
        confirmTextView = findViewById(R.id.tv_preview_confirm);
        bottomLayout = findViewById(R.id.rl_preview_bottom_layout);
        checkBox = findViewById(R.id.cb_preview_check);
        previewRecyclerView = findViewById(R.id.rv_preview);
        bottomRecyclerView = findViewById(R.id.rv_bottom_thumbnail);
    }

    private void initData() {
        imageConfig = (ImageConfig) getIntent().getSerializableExtra(Constants.CONFIG);
        imageList = getIntent().getParcelableArrayListExtra(Constants.IMAGE_LIST);
        imageSelectedList = getIntent().getParcelableArrayListExtra(Constants.IMAGE_SELECTED_LIST);
        curPosition = getIntent().getIntExtra(Constants.POSITION, 0);
        // 获取 imageconfig 中的值
        int statusBarColor = imageConfig.getStatusBarColor();
        titleBarColor = imageConfig.getTitleBarColor();
        int titleBarTextColor = imageConfig.getTitleBarTextColor();
        // 设置状态栏颜色
        if (statusBarColor != 0) {
            StatusBarUtil.setColor(this, getResources().getColor(statusBarColor), 0);
        }
        // 设置标题栏背景颜色
        if (titleBarColor != 0) {
            titleLayout.setBackgroundColor(getResources().getColor(titleBarColor));
        }
        // 设置标题栏文字颜色
        if (titleBarTextColor != 0) {
            countTextView.setTextColor(getResources().getColor(titleBarTextColor));
//            confirmTextView.setTextColor(getResources().getColor(titleBarTextColor));
        }
        countTextView.setText((curPosition + 1) + " / " + imageList.size());
        if (imageSelectedList.size() > 0) {
            confirmTextView.setEnabled(true);
            confirmTextView.setText("确定(" + imageSelectedList.size() + ")");
        } else {
            confirmTextView.setEnabled(false);
            confirmTextView.setText("确定");
        }
        // 初始化预览 recyclerview
        initPreviewRecyclerView();
        // 初始化底部 menu
        initBottomMenu();

        titleLayout.post(new Runnable() {
            @Override
            public void run() {
                titleLayoutHeight = titleLayout.getMeasuredHeight();
                bottomLayoutHeight = bottomLayout.getMeasuredHeight();
            }
        });
    }

    /**
     * 初始化预览 recyclerview
     */
    private void initPreviewRecyclerView() {
        previewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        previewRecyclerView.setLayoutManager(previewLayoutManager);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(previewRecyclerView);
        imagePreviewAdapter = new ImagePreviewAdapter(this, imageConfig);
        previewRecyclerView.setAdapter(imagePreviewAdapter);
        imagePreviewAdapter.setImages(imageList);
        previewRecyclerView.scrollToPosition(curPosition);
    }

    /**
     * 初始化底部 recyclerview
     */
    private void initBottomMenu() {
        if (imageSelectedList == null || imageSelectedList.size() == 0) {
            bottomRecyclerView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        } else {
            bottomRecyclerView.setBackgroundColor(Color.WHITE);
        }
        LinearLayoutManager bottomLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        bottomRecyclerView.setLayoutManager(bottomLayoutManager);
        imageThumbnailAdapter = new ImageThumbnailAdapter(this, imageConfig);
        bottomRecyclerView.setAdapter(imageThumbnailAdapter);
        // 设置 checkbox 选中状态
        setCheckBoxState();
        imageThumbnailAdapter.setImagesThumbnail(imageSelectedList, getPositionInImageSelectedList());
    }

    private void setListener() {
        backView.setOnClickListener(this);
        confirmTextView.setOnClickListener(this);
        // 设置 previewRecyclerView 滚动监听，更新当前正在显示的位置
        previewRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    curPosition = previewLayoutManager.findLastVisibleItemPosition();
                    countTextView.setText((curPosition + 1) + " / " + imageList.size());
                    setCheckBoxState();
                    imageThumbnailAdapter.setImagesThumbnail(imageSelectedList, getPositionInImageSelectedList());
                }
            }
        });
        imagePreviewAdapter.setOnImageTapListener(new ImagePreviewAdapter.OnImageTapListener() {
            @Override
            public void onImageTap() {
                startTitleBottomLayoutAnimation();
            }
        });
        // 点击缩略图，预览图显示对应的位置上的图片
        imageThumbnailAdapter.setOnImageThumbnailClickListener(new ImageThumbnailAdapter.OnImageThumbnailClickListener() {
            @Override
            public void onImageThumbnailClick(View itemView, Image image, int position) {
                int inFolderPosition = imageList.indexOf(image);
                Log.e(TAG, "onImageThumbnailClick: " + inFolderPosition);
                if (inFolderPosition == curPosition) {
                    return;
                }
                // 设置底部缩略图选中的背景颜色
                imageThumbnailAdapter.setImagesThumbnail(imageSelectedList, position);
                curPosition = inFolderPosition;
                // 预览图显示缩略图对应的图片
                previewLayoutManager.scrollToPosition(curPosition);
                countTextView.setText((curPosition + 1) + " / " + imageList.size());
                // 切换底部缩略图时，更新 checkbox 状态，是否设置为已选
                setCheckBoxState();
            }
        });
        // 选择 或 取消
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // checkbox.setChecked() 方法也会触发 onCheckedChanged(),需要排除这种方式，只有在点击时才触发
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        imageSelectedList.add(imageList.get(curPosition));
                        imageThumbnailAdapter.setImagesThumbnail(imageSelectedList, getPositionInImageSelectedList());
                        if (imageSelectedList.size() > 0) {
                            bottomRecyclerView.setBackgroundColor(Color.WHITE);
                            confirmTextView.setText("确定(" + imageSelectedList.size() + ")");
                            confirmTextView.setEnabled(true);
                        }
                    } else {
                        imageSelectedList.remove(imageList.get(curPosition));
                        imageThumbnailAdapter.setImagesThumbnail(imageSelectedList, getPositionInImageSelectedList());
//                        setImageSelectedState();
                        if (imageSelectedList.size() == 0) {
                            bottomRecyclerView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                            confirmTextView.setText("确定");
                            confirmTextView.setEnabled(false);
                        } else {
                            confirmTextView.setText("确定(" + imageSelectedList.size() + ")");
                            confirmTextView.setEnabled(true);
                        }
                    }
                }
            }
        });
    }

    /**
     * 获取当前预览图显示的图片，在已选择的图片集合中的索引
     */
    private int getPositionInImageSelectedList() {
        Image image = imageList.get(curPosition);
        return imageSelectedList.indexOf(image);
    }

    private void setCheckBoxState() {
        Image image = imageList.get(curPosition);
        if (imageSelectedList.contains(image)) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
    }


    /**
     * 开启标题栏，底部操作栏动画，显示或隐藏
     */
    private void startTitleBottomLayoutAnimation() {
        // titleLayout 动画
        ObjectAnimator titleLayoutAnimator = ObjectAnimator.ofObject(titleLayout, "translationY", new FloatEvaluator(),
                isTitleBottomLayoutShowing ? 0 : -titleLayoutHeight, isTitleBottomLayoutShowing ? -titleLayoutHeight : 0);

        // bottomLayout 动画
        ObjectAnimator bottomLayoutAnimator = ObjectAnimator.ofObject(bottomLayout, "translationY", new FloatEvaluator(),
                isTitleBottomLayoutShowing ? 0 : bottomLayoutHeight, isTitleBottomLayoutShowing ? bottomLayoutHeight : 0);
        // 动画集合
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.playTogether(titleLayoutAnimator, bottomLayoutAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                // 显示titleLayout 时，先改变状态栏背景颜色，再开启显示动画
                if (!isTitleBottomLayoutShowing) {
                    StatusBarUtil.setColor(ImagePreviewActivity.this, getResources().getColor(imageConfig.getStatusBarColor()), 0);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isTitleBottomLayoutShowing = !isTitleBottomLayoutShowing;
                // 隐藏 titleLayout时，先开启隐藏动画，再改变状态栏背景颜色
                if (!isTitleBottomLayoutShowing) {
                    StatusBarUtil.setColor(ImagePreviewActivity.this, getResources().getColor(android.R.color.black), 0);
                }
            }
        });
        animatorSet.start();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_preview_back) {
            exit(false);
        } else if (v.getId() == R.id.tv_preview_confirm) {
            exit(true);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit(false);
        }
        return true;
    }

    /**
     * 退出 activity
     *
     * @param isConfirm
     */
    private void exit(boolean isConfirm) {
        Intent intent = new Intent();
        intent.putExtra(Constants.IS_CONFIRM, isConfirm);
        intent.putParcelableArrayListExtra(Constants.IMAGE_SELECTED_LIST, imageSelectedList);
        setResult(RESULT_OK, intent);
        finish();
    }


}
