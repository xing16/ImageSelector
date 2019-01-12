package com.xing.imageselector.widget;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.xing.imageselector.R;
import com.xing.imageselector.adapter.FolderAdapter;
import com.xing.imageselector.config.ImageConfig;
import com.xing.imageselector.entity.Folder;
import com.xing.imageselector.utils.DensityUtil;

import java.util.ArrayList;

public class BottomDialogFragment extends DialogFragment {

    private static final String CONFIG = "config";
    private static final String FOLDER_LIST = "folder_list";
    private static final String POSITION = "position";
    private RecyclerView recyclerView;
    private ImageConfig imageConfig;
    private ArrayList<Folder> folderList;
    private FolderAdapter adapter;
    private int currentSelectedPosition;

    public static BottomDialogFragment newsInstance(ImageConfig config, ArrayList<Folder> folders, int selectedPosition) {
        BottomDialogFragment bottomDialogFragment = new BottomDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(CONFIG, config);
        bundle.putParcelableArrayList(FOLDER_LIST, folders);
        bundle.putInt(POSITION, selectedPosition);
        bottomDialogFragment.setArguments(bundle);
        return bottomDialogFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.BottomDialogStyle);
        Bundle bundle = getArguments();
        if (bundle != null) {
            imageConfig = (ImageConfig) bundle.getSerializable(CONFIG);
            folderList = bundle.getParcelableArrayList(FOLDER_LIST);
            currentSelectedPosition = bundle.getInt(POSITION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_bottom, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.rv_folder_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new FolderAdapter(getContext(), imageConfig, currentSelectedPosition);
        recyclerView.setAdapter(adapter);
        adapter.setData(folderList);
        adapter.setOnFolderClickListener(new FolderAdapter.OnFolderClickListener() {
            @Override
            public void onFolderClick(Folder foler, int position) {
                if (position == currentSelectedPosition) {
                    dismiss();
                    return;
                }
                // 通过接口回调，fragment 向 activity 传值
                if (getActivity() instanceof OnFolderSelectedListener) {
                    ((OnFolderSelectedListener) getActivity()).onFolderSelected(position);
                }
                dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            Window window = dialog.getWindow();
            if (window != null) {
                window.setWindowAnimations(R.style.BottomDialogAnimationStyle);
                WindowManager.LayoutParams params = window.getAttributes();
                params.gravity = Gravity.BOTTOM;
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.height = (int) DensityUtil.dp2Px(getContext(), 400); // 底部弹出的DialogFragment的高度，如果是MATCH_PARENT则铺满整个窗口
                window.setAttributes(params);
            }
        }
    }

    /**
     * 定义 Fragment 向宿主 Activity 传递数据的接口,文件夹切换的回调
     */
    public interface OnFolderSelectedListener {
        void onFolderSelected(int position);
    }

}
