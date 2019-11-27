package cn.finalteam.rxgalleryfinal.ui.adapter;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import cn.finalteam.rxgalleryfinal.Configuration;
import cn.finalteam.rxgalleryfinal.R;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.rxbus.RxBus;
import cn.finalteam.rxgalleryfinal.rxbus.event.MediaCheckChangeEvent;
import cn.finalteam.rxgalleryfinal.rxjob.Job;
import cn.finalteam.rxgalleryfinal.rxjob.RxJob;
import cn.finalteam.rxgalleryfinal.rxjob.job.ImageThmbnailJobCreate;
import cn.finalteam.rxgalleryfinal.ui.activity.MediaActivity;
import cn.finalteam.rxgalleryfinal.ui.base.IMultiImageCheckedListener;
import cn.finalteam.rxgalleryfinal.ui.widget.CheckView;
import cn.finalteam.rxgalleryfinal.ui.widget.FixImageView;
import cn.finalteam.rxgalleryfinal.utils.FormatUtils;
import cn.finalteam.rxgalleryfinal.utils.Logger;
import cn.finalteam.rxgalleryfinal.utils.OsCompat;
import cn.finalteam.rxgalleryfinal.utils.ThemeUtils;

import static cn.finalteam.rxgalleryfinal.ui.widget.CheckView.UNCHECKED;

/**
 * Desction:
 * Author:pengjianbo  Dujinyang
 * Date:16/5/18 下午7:48
 */
public class MediaGridAdapter extends RecyclerView.Adapter<MediaGridAdapter.GridViewHolder> {

    private static IMultiImageCheckedListener iMultiImageCheckedListener;
    private final MediaActivity mMediaActivity;
    private final List<MediaBean> mMediaBeanList;
    private final int mImageSize;
    private final Configuration mConfiguration;
    private final Drawable mDefaultImage;
    private final Drawable mImageViewBg;
    private final Drawable mCameraImage;
    private final int mCameraImageBgColor;
    private final int mCameraTextColor;
    private int imageLoaderType = 0;

    public MediaGridAdapter(
            MediaActivity mediaActivity,
            List<MediaBean> list,
            int screenWidth,
            Configuration configuration) {
        this.mMediaActivity = mediaActivity;
        this.mMediaBeanList = list;
        this.mImageSize = screenWidth / 3;
        int defaultResId = ThemeUtils.resolveDrawableRes(mediaActivity, R.attr.gallery_default_image, R.drawable.gallery_default_image);
        this.mDefaultImage = ContextCompat.getDrawable(mediaActivity, defaultResId);
        this.mConfiguration = configuration;
        this.imageLoaderType = configuration.getImageLoaderType();
        this.mImageViewBg = ThemeUtils.resolveDrawable(mMediaActivity, R.attr.gallery_imageview_bg, R.drawable.gallery_default_image);
        this.mCameraImage = ThemeUtils.resolveDrawable(mMediaActivity, R.attr.gallery_camera_image, R.drawable.gallery_ic_camera);
        this.mCameraImageBgColor = ThemeUtils.resolveColor(mMediaActivity, R.attr.gallery_camera_bg, R.color.gallery_default_camera_bg_color);
        this.mCameraTextColor = ThemeUtils.resolveColor(mMediaActivity, R.attr.gallery_take_image_text_color, R.color.gallery_default_take_image_text_color);
    }

    public static void setCheckedListener(IMultiImageCheckedListener checkedListener) {
        iMultiImageCheckedListener = checkedListener;
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery_media_grid, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {
        MediaBean mediaBean = mMediaBeanList.get(position);
        if (mediaBean.getId() == Integer.MIN_VALUE) {
            holder.mCbCheck.setVisibility(View.GONE);
            holder.tvDuration.setVisibility(View.GONE);
            holder.mIvMediaImage.setVisibility(View.GONE);
            holder.checkView.setVisibility(View.GONE);
            holder.mLlCamera.setVisibility(View.VISIBLE);
            holder.mIvCameraImage.setImageDrawable(mCameraImage);
            holder.mTvCameraTxt.setTextColor(mCameraTextColor);
            holder.mTvCameraTxt.setText(mConfiguration.isImage() ? mMediaActivity.getString(R.string.gallery_take_image) : mMediaActivity.getString(R.string.gallery_video));
            holder.mIvCameraImage.setBackgroundColor(mCameraImageBgColor);
        } else {
            holder.checkView.setCountable(!mConfiguration.isRadio());
            if (mConfiguration.isRadio()) {
                holder.mCbCheck.setVisibility(View.GONE);
                holder.checkView.setVisibility(View.GONE);
            } else {
                holder.checkView.setVisibility(View.VISIBLE);
                holder.mCbCheck.setVisibility(View.GONE);
//                holder.mCbCheck.setOnClickListener(new OnCheckBoxClickListener(mediaBean));
//                holder.mCbCheck.setOnCheckedChangeListener(new OnCheckBoxCheckListener(mediaBean));
            }
            holder.checkView.bindMedia(mediaBean);

            holder.tvDuration.setVisibility(mConfiguration.isImage() ? View.GONE : View.VISIBLE);
            holder.tvDuration.setText(FormatUtils.formatDurationTime(mediaBean.getDuration()));
            holder.ivGif.setVisibility(mConfiguration.isImage() && mediaBean.getOriginalPath().endsWith(".gif") ? View.VISIBLE : View.GONE);

            holder.mIvMediaImage.setVisibility(View.VISIBLE);
            holder.mLlCamera.setVisibility(View.GONE);
            holder.mCbCheck.setChecked(mMediaActivity.getCheckedList() != null && mMediaActivity.getCheckedList().contains(mediaBean));
//            holder.checkView.setEnabled();
            String bitPath = mediaBean.getThumbnailBigPath();
            String smallPath = mediaBean.getThumbnailSmallPath();

            if (mMediaActivity.getCheckedList() != null && mMediaActivity.getCheckedList().contains(mediaBean)) {
                if (mConfiguration.isRadio()) {
                    holder.checkView.setChecked(true);
                } else {
                    int num = mMediaActivity.getCheckedList().indexOf(mediaBean);
                    holder.checkView.setCheckedNum(num + 1);
                }
            } else {
                if (mConfiguration.isRadio()) {
                    holder.checkView.setChecked(false);
                } else {
                    holder.checkView.setCheckedNum(UNCHECKED);
                }
            }

            holder.checkView.setEnabled(mConfiguration.getMaxSize() > mMediaActivity.getCheckedList().size());

            holder.checkView.bindMedia(mediaBean);
            holder.checkView.setOnCheckClickListener(new CheckView.OnCheckClickListener() {
                @Override
                public void onCheckViewClicked(CheckView checkView, MediaBean item) {
                    if (mConfiguration.getMaxSize() == mMediaActivity.getCheckedList().size() &&
                            !mMediaActivity.getCheckedList().contains(mediaBean)) {
                        Logger.i("=>" + mMediaActivity.getResources().getString(R.string.gallery_image_max_size_tip, mConfiguration.getMaxSize()));
                        if (iMultiImageCheckedListener != null) {
                            iMultiImageCheckedListener.selectedImgMax(checkView, false, mConfiguration.getMaxSize());
                        }
                    } else {
                        RxBus.getDefault().post(new MediaCheckChangeEvent(mediaBean));
                        if (iMultiImageCheckedListener != null)
                            iMultiImageCheckedListener.selectedImg(checkView, !checkView.isChecked());
                    }
                    notifyDataSetChanged();
                }
            });

            if (!new File(bitPath).exists() || !new File(smallPath).exists()) {
                Job job = new ImageThmbnailJobCreate(mMediaActivity, mediaBean).create();
                RxJob.getDefault().addJob(job);
            }
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
            Logger.w("提示path：" + path);

            OsCompat.setBackgroundDrawableCompat(holder.mIvMediaImage, mImageViewBg);
            mConfiguration.getImageLoader().displayImage(mMediaActivity, path, (FixImageView) holder.mIvMediaImage, mDefaultImage, mConfiguration.getImageConfig(), true, mConfiguration.isPlayGif(), mImageSize, mImageSize, mediaBean.getOrientation());
        }
    }

    @Override
    public int getItemCount() {
        return mMediaBeanList.size();
    }


    static class GridViewHolder extends RecyclerView.ViewHolder {

        final AppCompatCheckBox mCbCheck;
        final LinearLayout mLlCamera;
        final TextView mTvCameraTxt;
        final ImageView mIvCameraImage;
        ImageView mIvMediaImage, ivGif;
        CheckView checkView;
        TextView tvDuration;


        GridViewHolder(View itemView) {
            super(itemView);
            mIvMediaImage = itemView.findViewById(R.id.iv_media_image);
            mCbCheck = (AppCompatCheckBox) itemView.findViewById(R.id.cb_check);
            mLlCamera = (LinearLayout) itemView.findViewById(R.id.ll_camera);
            mTvCameraTxt = (TextView) itemView.findViewById(R.id.tv_camera_txt);
            mIvCameraImage = (ImageView) itemView.findViewById(R.id.iv_camera_image);
            checkView = itemView.findViewById(R.id.check_view);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            ivGif = itemView.findViewById(R.id.iv_gif);

            int checkTint = ThemeUtils.resolveColor(itemView.getContext(), R.attr.gallery_checkbox_button_tint_color, R.color.gallery_default_checkbox_button_tint_color);
            CompoundButtonCompat.setButtonTintList(mCbCheck, ColorStateList.valueOf(checkTint));
        }
    }

    private class OnCheckBoxClickListener implements View.OnClickListener {

        private final MediaBean mediaBean;

        OnCheckBoxClickListener(MediaBean bean) {
            this.mediaBean = bean;
        }

        @Override
        public void onClick(View view) {
            if (mConfiguration.getMaxSize() == mMediaActivity.getCheckedList().size() &&
                    !mMediaActivity.getCheckedList().contains(mediaBean)) {
                AppCompatCheckBox checkBox = (AppCompatCheckBox) view;
                checkBox.setChecked(false);
                Logger.i("=>" + mMediaActivity.getResources().getString(R.string.gallery_image_max_size_tip, mConfiguration.getMaxSize()));
            } else {
                RxBus.getDefault().post(new MediaCheckChangeEvent(mediaBean));
            }
        }
    }

    /**
     * @author KARL-dujinyang
     */
    private class OnCheckBoxCheckListener implements CompoundButton.OnCheckedChangeListener {
        private final MediaBean mediaBean;

        OnCheckBoxCheckListener(MediaBean bean) {
            this.mediaBean = bean;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mConfiguration.getMaxSize() == mMediaActivity.getCheckedList().size() &&
                    !mMediaActivity.getCheckedList().contains(mediaBean)) {
                AppCompatCheckBox checkBox = (AppCompatCheckBox) buttonView;
                checkBox.setChecked(false);
                Logger.i("选中：" + mMediaActivity.getResources().getString(R.string.gallery_image_max_size_tip, mConfiguration.getMaxSize()));
                if (iMultiImageCheckedListener != null) {
                    iMultiImageCheckedListener.selectedImgMax(buttonView, isChecked, mConfiguration.getMaxSize());
                }
            } else {
                if (iMultiImageCheckedListener != null)
                    iMultiImageCheckedListener.selectedImg(buttonView, isChecked);
            }
        }
    }


}
