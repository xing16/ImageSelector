package com.xing.selector;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> paths = new ArrayList<>();

    public void setData(List<String> paths) {
        this.paths = paths;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_img, viewGroup, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        String path = paths.get(position);
        ((ItemViewHolder) viewHolder).bindData(path);
    }

    @Override
    public int getItemCount() {
        return paths == null ? 0 : paths.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_img);
        }

        public void bindData(String path) {
            Glide.with(imageView.getContext())
                    .load(path)
                    .into(imageView);
        }
    }
}
