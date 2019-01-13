package com.xing.imageselector.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xing.imageselector.R;
import com.xing.imageselector.config.ImageConfig;
import com.xing.imageselector.entity.Image;

import java.util.ArrayList;

/**
 * 图片网格列表 adapter
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_IMAGE = 1;
    private Context context;
    private ImageConfig imageConfig;
    private ArrayList<Image> imageList = new ArrayList<>();
    private LayoutInflater inflater;
    /**
     * 是否显示拍照，只有在 "全部图片" 文件夹时才显示 拍照
     */
    private boolean isShowCamera;
    /**
     * 已经选中的图片集合
     */
    private ArrayList<Image> imageSelectedList = new ArrayList<>();

    public ImageAdapter(Context context, ImageConfig config) {
        this.context = context;
        this.imageConfig = config;
        inflater = LayoutInflater.from(context);
    }

    /**
     * 是否显示拍照 item, 只有在 "全部图片" 文件夹时才显示 拍照
     *
     * @param images
     * @param isShowCamera
     */
    public void setData(ArrayList<Image> images, boolean isShowCamera) {
        this.imageList = images;
        this.isShowCamera = isShowCamera;
        notifyDataSetChanged();
    }

    /**
     * 返回数据源
     *
     * @return
     */
    public ArrayList<Image> getImageList() {
        return imageList;
    }

    /**
     * 设置选中的集合
     *
     * @param selectedImages
     */
    public void setImageSelectedList(ArrayList<Image> selectedImages) {
        if (selectedImages == null) {
            return;
        }
        this.imageSelectedList = selectedImages;
        notifyDataSetChanged();
    }


    /**
     * 返回选中的图片
     *
     * @return
     */
    public ArrayList<Image> getImageSelectedList() {
        return imageSelectedList;
    }

    @NonNull
    @Override
    public ImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_CAMERA) {
            View cameraView = inflater.inflate(R.layout.item_camera, viewGroup, false);
            return new ImageViewHolder(cameraView);
        } else {
            View view = inflater.inflate(R.layout.item_image, viewGroup, false);
            return new ImageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageAdapter.ImageViewHolder viewHolder, final int position) {
        int itemType = getItemViewType(position);
        if (itemType == TYPE_CAMERA) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCameraClickListener != null) {
                        onCameraClickListener.onCameraClick();
                    }
                }
            });
        } else {
            final Image image;
            if (imageConfig.isShowCamera() && isShowCamera) {
                image = imageList.get(position - 1);
            } else {
                image = imageList.get(position);
            }
            viewHolder.bindData(image);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onImageClickListener != null) {
                        onImageClickListener.onImageClick(image, imageConfig.isShowCamera() && isShowCamera ? position - 1 : position);
                    }
                }
            });
            viewHolder.checkImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = false;
                    if (imageSelectedList.contains(image)) {
                        imageSelectedList.remove(image);
                        isChecked = false;
                        setItemSelect(viewHolder, false);
                    } else {
                        imageSelectedList.add(image);
                        isChecked = true;
                        setItemSelect(viewHolder, true);
                    }
                    // 图片选中的回调
                    if (onImageSelectedListener != null) {
                        onImageSelectedListener.onImageSelected(image, imageSelectedList.size(), isChecked);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return imageConfig.isShowCamera() && isShowCamera ? imageList.size() + 1 : imageList.size();
    }

    /**
     * 只有在 "全部图片" 文件夹，并且配置 imageconfig.showCamera(true) 时，才显示出拍照 item
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0 && imageConfig.isShowCamera() && isShowCamera) {
            return TYPE_CAMERA;
        } else {
            return TYPE_IMAGE;
        }
    }


    public void setItemSelect(ImageViewHolder viewHolder, boolean isSelected) {
        if (isSelected) {
            viewHolder.checkImgView.setImageResource(R.drawable.is_cb_white_checked);
            viewHolder.imageView.setColorFilter(context.getResources().getColor(R.color.is_overlay_selected), PorterDuff.Mode.SRC_ATOP);
        } else {
            viewHolder.checkImgView.setImageResource(R.drawable.is_cb_white_uncheck);
            viewHolder.imageView.setColorFilter(context.getResources().getColor(R.color.is_overlay_unselect), PorterDuff.Mode.SRC_ATOP);
        }
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView checkImgView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_image);
            checkImgView = itemView.findViewById(R.id.iv_select);
        }

        public void bindData(Image image) {
            imageConfig.getImageEngine().displayImage(image.getPath(), imageView);
            setItemSelect(this, imageSelectedList.contains(image));
            int selectMode = imageConfig.getSelectMode();
            if (selectMode == ImageConfig.MODE_MULTI) {
                checkImgView.setVisibility(View.VISIBLE);
            } else {
                checkImgView.setVisibility(View.INVISIBLE);
            }
        }
    }

    public interface OnImageClickListener {
        void onImageClick(Image image, int position);
    }

    public interface OnCameraClickListener {
        void onCameraClick();
    }

    public interface OnImageSelectedListener {
        void onImageSelected(Image image, int selectCount, boolean isSelected);
    }

    private OnImageClickListener onImageClickListener;
    private OnCameraClickListener onCameraClickListener;
    private OnImageSelectedListener onImageSelectedListener;

    public void setOnImageClickListener(OnImageClickListener listener) {
        onImageClickListener = listener;
    }

    public void setOnCameraClickListener(OnCameraClickListener listener) {
        this.onCameraClickListener = listener;
    }

    public void setOnImageSelectedListener(OnImageSelectedListener listener) {
        this.onImageSelectedListener = listener;
    }
}
