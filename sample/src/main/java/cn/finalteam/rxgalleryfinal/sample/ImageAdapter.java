package cn.finalteam.rxgalleryfinal.sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cn.finalteam.rxgalleryfinal.bean.MediaBean;

/**
 * Copyright (C), 2017-2019, GWorld(平潭)互联网有限公司
 *
 * @author :        Chee <z-code@foxmail.com>
 * @date :          2019/11/27 16:11
 * @desc :
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    ArrayList<MediaBean> mediaBeans = new ArrayList<>();
    Context context;

    public ImageAdapter(Context context, ArrayList<MediaBean> mediaBeans) {
        this.context = context;
        this.mediaBeans = mediaBeans;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_img, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bitmap bitmap = BitmapFactory.decodeFile(mediaBeans.get(position).getOriginalPath());
        holder.imageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return mediaBeans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

}
