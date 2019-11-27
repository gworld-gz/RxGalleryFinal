package cn.finalteam.rxgalleryfinal.ui.adapter;

import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.finalteam.rxgalleryfinal.Configuration;
import cn.finalteam.rxgalleryfinal.R;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.ui.activity.MediaActivity;
import cn.finalteam.rxgalleryfinal.ui.widget.FixImageView;
import cn.finalteam.rxgalleryfinal.ui.widget.SquareRelativeLayout;
import cn.finalteam.rxgalleryfinal.utils.ThemeUtils;

/**
 * Copyright (C), 2017-2019, GWorld(平潭)互联网有限公司
 *
 * @author :        Chee <z-code@foxmail.com>
 * @date :          2019/11/25 18:27
 * @desc :
 */
public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.ViewHoler> {

    MediaActivity mediaActivity;
    List<MediaBean> list;
    private final Configuration mConfiguration;
    private int imageLoaderType = 0;
    private int mImageSize;
    private MediaBean checkMediabean;

    public PreviewAdapter(
            MediaActivity mediaActivity,
            List<MediaBean> list,
            int screenWidth,
            Configuration configuration) {
        this.mImageSize = screenWidth / 3;
        this.mediaActivity = mediaActivity;
        this.list = list;
        this.mConfiguration = configuration;
        this.imageLoaderType = configuration.getImageLoaderType();
        if (list != null && list.size() > 0) {
            setCheck(list.get(0));
        }
    }

    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_preview_item, parent, false);
        return new ViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoler holder, int position) {
        MediaBean mediaBean = list.get(position);
        String path;
        if (mConfiguration.isPlayGif() && (imageLoaderType == 3 || imageLoaderType == 2)) {
            path = mediaBean.getOriginalPath();
        } else {
            path = mediaBean.getThumbnailSmallPath();
            if (TextUtils.isEmpty(path)) {
                path = mediaBean.getThumbnailBigPath();
            }
            if (TextUtils.isEmpty(path)) {
                path = mediaBean.getOriginalPath();
            }
        }
        mConfiguration.getImageLoader().displayImage(mediaActivity, path, (FixImageView) holder.imageView, null, mConfiguration.getImageConfig(), true, mConfiguration.isPlayGif(), mImageSize, mImageSize, mediaBean.getOrientation());


        int checkTint = ThemeUtils.resolveColor(holder.imageView.getContext(), R.attr.gallery_checkbox_button_tint_color, R.color.gallery_default_checkbox_button_tint_color);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke((int) (2 * holder.imageView.getContext().getResources().getDisplayMetrics().density), checkTint);

        holder.previewLayout.setBackgroundDrawable(mediaBean == checkMediabean ? drawable : null);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItem(position);
                }
                setCheck(mediaBean);
            }
        });
    }

    public void setCheck(MediaBean mediaBean) {
        checkMediabean = mediaBean;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHoler extends RecyclerView.ViewHolder {

        ImageView imageView;
        SquareRelativeLayout previewLayout;

        public ViewHoler(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgView);
            previewLayout = itemView.findViewById(R.id.previewLayout);
        }
    }

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItem(int position);
    }

}
