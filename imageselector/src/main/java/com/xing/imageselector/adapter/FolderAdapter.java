package com.xing.imageselector.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xing.imageselector.R;
import com.xing.imageselector.config.ImageConfig;
import com.xing.imageselector.entity.Folder;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件夹列表 adapter
 */
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    private Context mContext;
    private ImageConfig imageConfig;
    private List<Folder> folderList = new ArrayList<>();
    private int curSelectedPosition;

    public FolderAdapter(Context context, ImageConfig config, int selectedPosition) {
        this.mContext = context;
        imageConfig = config;
        curSelectedPosition = selectedPosition;
    }

    public void setData(List<Folder> folders) {
        this.folderList = folders;
        notifyDataSetChanged();
    }

    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_folder, viewGroup, false);
        return new FolderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FolderViewHolder folderViewHolder, final int position) {
        final Folder folder = folderList.get(position);
        folderViewHolder.bindData(folder, position);

        // 设置点击监听
        folderViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFolderClickListener != null) {
                    onFolderClickListener.onFolderClick(folder, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return folderList == null ? 0 : folderList.size();
    }


    class FolderViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImageView;
        TextView folderNameTextView;
        TextView folderSizeTextView;
        ImageView selectedImgView;

        public FolderViewHolder(View itemView) {
            super(itemView);
            coverImageView = itemView.findViewById(R.id.iv_folder_cover);
            folderNameTextView = itemView.findViewById(R.id.tv_folder_name);
            folderSizeTextView = itemView.findViewById(R.id.tv_folder_size);
            selectedImgView = itemView.findViewById(R.id.iv_selected);
        }

        public void bindData(Folder folder, int position) {
            // 加载封面
            imageConfig.getImageEngine().displayImage(folder.getImageList().get(0).getPath(), coverImageView);
            folderNameTextView.setText(folder.getName());
            folderSizeTextView.setText(folder.getImageList().size() + "张");
            if (curSelectedPosition == position) {
                selectedImgView.setVisibility(View.VISIBLE);
            } else {
                selectedImgView.setVisibility(View.INVISIBLE);
            }

        }
    }

    public interface OnFolderClickListener {
        void onFolderClick(Folder foler, int position);
    }

    private OnFolderClickListener onFolderClickListener;

    public void setOnFolderClickListener(OnFolderClickListener listener) {
        this.onFolderClickListener = listener;
    }
}
