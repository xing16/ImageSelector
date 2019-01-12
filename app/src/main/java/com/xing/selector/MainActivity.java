package com.xing.selector;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.xing.imageselector.ImageSelector;
import com.xing.imageselector.config.ImageConfig;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SINGLE = 11;
    private static final int REQUEST_SINGLE_CROP = 12;
    private static final int REQUEST_MULTI = 13;
    private RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.rv_result);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);
    }

    public void click(View view) {
        switch (view.getId()) {
            case R.id.btn_single:
                ImageConfig imageConfig = new ImageConfig.Builder(new GlideImageEngine())
                        .showCamera(true)
                        .statusBarColor(R.color.colorPrimary)
                        .titleBarColor(R.color.colorPrimary)
                        .titleBarTextColor(android.R.color.white)
                        .selectMode(ImageConfig.MODE_SINGLE)
                        .needCrop(false)
                        .build();
                ImageSelector.open(this, imageConfig, REQUEST_SINGLE);

                break;
            case R.id.btn_single_crop:
                ImageConfig imageConfig2 = new ImageConfig.Builder(new GlideImageEngine())
                        .showCamera(true)
                        .statusBarColor(R.color.colorPrimary)
                        .titleBarColor(R.color.colorPrimary)
                        .titleBarTextColor(android.R.color.white)
                        .selectMode(ImageConfig.MODE_SINGLE)
                        .needCrop(true)
                        .build();
                ImageSelector.open(this, imageConfig2, REQUEST_SINGLE_CROP);

                break;
            case R.id.btn_multi:
                ImageConfig imageConfig1 = new ImageConfig.Builder(new GlideImageEngine())
                        .showCamera(true)
                        .statusBarColor(R.color.colorPrimary)
                        .titleBarColor(R.color.colorPrimary)
                        .titleBarTextColor(android.R.color.white)
                        .selectMode(ImageConfig.MODE_MULTI)
                        .needCrop(true)
                        .build();
                ImageSelector.open(this, imageConfig1, REQUEST_MULTI);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<String> list = new ArrayList<>();
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SINGLE:
                    String path = ImageSelector.getSingleSelectPath(data);
                    if (!TextUtils.isEmpty(path)) {
                        list.clear();
                        list.add(path);
                        adapter.setData(list);
                    }
                    break;
                case REQUEST_SINGLE_CROP:
                    String cpath = ImageSelector.getCropPath(data);
                    if (!TextUtils.isEmpty(cpath)) {
                        list.clear();
                        list.add(cpath);
                        adapter.setData(list);
                    }
                    break;
                case REQUEST_MULTI:
                    ArrayList<String> selectImgList = ImageSelector.getMultiSelectPath(data);
                    if (selectImgList != null) {
                        adapter.setData(selectImgList);
                    }
                    break;
                default:
                    break;
            }

        }

    }
}
