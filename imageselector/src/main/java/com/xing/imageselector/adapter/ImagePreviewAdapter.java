package com.xing.imageselector.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.xing.imageselector.R;
import com.xing.imageselector.config.ImageConfig;
import com.xing.imageselector.entity.Image;

import java.util.ArrayList;
import java.util.List;

public class ImagePreviewAdapter extends RecyclerView.Adapter<ImagePreviewAdapter.ImagePreviewViewHolder> {

    private Context mContext;
    private List<String> imagesPathList = new ArrayList<>();
    private ImageConfig imageConfig;
    private LayoutInflater inflater;

    public ImagePreviewAdapter(Context context, ImageConfig config) {
        this.mContext = context;
        if (config == null) {
            throw new IllegalArgumentException("config can't be null");
        }
        imageConfig = config;
        inflater = LayoutInflater.from(context);
    }

    public void setImages(ArrayList<Image> list) {
        if (list == null) {
            return;
        }
        imagesPathList = convertImagePath(list);
        notifyDataSetChanged();
    }

    public void setPaths(List<String> paths) {
        imagesPathList = paths;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImagePreviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.item_preview, viewGroup, false);
        return new ImagePreviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagePreviewViewHolder viewHolder, int position) {
        viewHolder.bindData(imagesPathList.get(position));
        viewHolder.previewImgView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                if (onImageTapListener != null) {
                    onImageTapListener.onImageTap();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagesPathList == null ? 0 : imagesPathList.size();
    }


    class ImagePreviewViewHolder extends RecyclerView.ViewHolder {

        PhotoView previewImgView;

        public ImagePreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            previewImgView = itemView.findViewById(R.id.pv_preview);
        }

        private void bindData(String path) {
            imageConfig.getImageEngine().displayImage(path, previewImgView);
        }
    }


    private List<String> convertImagePath(List<Image> images) {
        if (images == null) {
            return null;
        }
        List<String> pathList = new ArrayList<>();
        for (Image image : images) {
            pathList.add(image.getPath());
        }
        return pathList;
    }


    OnImageTapListener onImageTapListener;

    public interface OnImageTapListener {
        void onImageTap();
    }

    public void setOnImageTapListener(OnImageTapListener listener) {
        this.onImageTapListener = listener;
    }
}
