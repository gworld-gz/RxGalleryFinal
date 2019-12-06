package cn.finalteam.rxgalleryfinal.ui.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import cn.finalteam.rxgalleryfinal.Configuration;
import cn.finalteam.rxgalleryfinal.R;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import pl.droidsonroids.gif.GifDrawable;
import uk.co.senab.photoview.PhotoView;

/**
 * Desction:
 * Author:pengjianbo  Dujinyang
 * Date:16/7/21 下午10:12
 */
public class MediaPreviewAdapter extends RecyclingPagerAdapter {

    private final List<MediaBean> mMediaList;
    private final Configuration mConfiguration;
    private final Drawable mDefaultImage;
    private final int mScreenWidth;
    private final int mScreenHeight;
    private final int mPageColor;
    private boolean original;

    public MediaPreviewAdapter(List<MediaBean> list,
                               int screenWidth,
                               int screenHeight,
                               Configuration configuration,
                               int pageColor,
                               Drawable drawable) {
        this.mMediaList = list;
        this.mScreenWidth = screenWidth;
        this.mScreenHeight = screenHeight;
        this.mConfiguration = configuration;
        this.mPageColor = pageColor;
        this.mDefaultImage = drawable;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        MediaBean mediaBean = mMediaList.get(position);
        if (convertView == null) {
            convertView = View.inflate(container.getContext(), R.layout.gallery_media_image_preview_item, null);
        }
        ImageView ivPlay = convertView.findViewById(R.id.iv_play);
        PhotoView ivImage = (PhotoView) convertView.findViewById(R.id.iv_media_image);
        ImageView ivGif = convertView.findViewById(R.id.iv_gif);
        TextView tvGifSize = convertView.findViewById(R.id.tv_gif_size);

        String path = null;
        if (mediaBean.getWidth() > 1200 || mediaBean.getHeight() > 1200) {
            path = mediaBean.getThumbnailBigPath();
        }
        if (TextUtils.isEmpty(path)) {
            path = mediaBean.getOriginalPath();
        }

        ivImage.setBackgroundColor(mPageColor);
        ivPlay.setVisibility(mConfiguration.isImage() ? View.GONE : View.VISIBLE);
        tvGifSize.setVisibility(mediaBean.isGif() ? View.VISIBLE : View.GONE);

        if (mConfiguration.isImage()) {
            if (mediaBean.isGif()) {
                tvGifSize.setText("GIF大小:" + Formatter.formatFileSize(container.getContext(), mediaBean.getLength()));
                try {
                    GifDrawable drawable = new GifDrawable(path);
                    ivImage.setImageDrawable(drawable);
                    drawable.start();
                    ivImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (drawable.isRunning()) {
                                drawable.stop();
                            } else {
                                drawable.start();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                mConfiguration.getImageLoader().displayImage(container.getContext(), path, ivImage, mDefaultImage, mConfiguration.getImageConfig(),
                        false, mConfiguration.isPlayGif(), mScreenWidth, mScreenHeight, mediaBean.getOrientation());
            }
        } else {
            mConfiguration.getImageLoader().displayImage(container.getContext(), path, ivImage, mDefaultImage, mConfiguration.getImageConfig(),
                    false, mConfiguration.isPlayGif(), mScreenWidth, mScreenHeight, mediaBean.getOrientation());
            ivPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    //SDCard卡根目录下/DCIM/Camera/test.mp4文件
                    Uri uri = Uri.parse(mediaBean.getOriginalPath());
                    intent.setDataAndType(uri, "video/*");
                    container.getContext().startActivity(intent);
                }
            });
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return mMediaList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
