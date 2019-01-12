package com.xing.imageselector.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xing.imageselector.R;
import com.xing.imageselector.config.ImageConfig;
import com.xing.imageselector.entity.Image;
import com.xing.imageselector.utils.DensityUtil;
import com.xing.imageselector.view.SquareImageView;

import java.util.ArrayList;

/**
 * 预览界面底部缩略图 adapter
 */
public class ImageThumbnailAdapter extends RecyclerView.Adapter<ImageThumbnailAdapter.ImageThumbnailViewHolder> {

    private Context mContext;
    private ArrayList<Image> imagesThumbnailList;
    private ImageConfig imageConfig;
    private final LayoutInflater inflater;
    /**
     * 当前缩略图选中的位置
     */
    private int curPosition = -1;

    public ImageThumbnailAdapter(Context context, ImageConfig config) {
        this.mContext = context;
        this.imageConfig = config;
        inflater = LayoutInflater.from(mContext);
    }

    /**
     * 设置缩略图数据集合
     *
     * @param images
     */
    public void setImagesThumbnail(ArrayList<Image> images, int position) {
        this.imagesThumbnailList = images;
        curPosition = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageThumbnailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = inflater.inflate(R.layout.item_thumbnail, viewGroup, false);
        return new ImageThumbnailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageThumbnailViewHolder viewHolder, final int position) {
        final Image image = imagesThumbnailList.get(position);
        final View itemView = viewHolder.itemView;
        // 最后一张设置右边距
        if (position == imagesThumbnailList.size() - 1) {
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            layoutParams.rightMargin = (int) DensityUtil.dp2Px(mContext, 10);
            itemView.setLayoutParams(layoutParams);
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onImageThumbnailClickListener != null) {
                    onImageThumbnailClickListener.onImageThumbnailClick(itemView, image, position);
                }
            }
        });
        viewHolder.bindData(image, position);
    }

    @Override
    public int getItemCount() {
        return imagesThumbnailList == null ? 0 : imagesThumbnailList.size();
    }

    class ImageThumbnailViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout thumbnailLayout;
        SquareImageView imageView;

        public ImageThumbnailViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailLayout = itemView.findViewById(R.id.rl_thumbnail_layout);
            imageView = itemView.findViewById(R.id.siv_thumbnail);
        }

        public void bindData(Image image, int position) {
            imageConfig.getImageEngine().displayImage(image.getPath(), imageView);
            if (position == curPosition) {
                thumbnailLayout.setBackgroundResource(R.drawable.is_shape_selected_stroke);
//                thumbnailLayout.setBackgroundColor(mContext.getResources().getColor(imageConfig.getTitleBarTextColor()));
            } else {
                thumbnailLayout.setBackground(null);
            }
        }
    }

    public interface OnImageThumbnailClickListener {
        void onImageThumbnailClick(View itemView, Image image, int position);
    }

    OnImageThumbnailClickListener onImageThumbnailClickListener;

    public void setOnImageThumbnailClickListener(OnImageThumbnailClickListener listener) {
        this.onImageThumbnailClickListener = listener;
    }
}
