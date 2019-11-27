package cn.finalteam.rxgalleryfinal.ui.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.finalteam.rxgalleryfinal.Configuration;
import cn.finalteam.rxgalleryfinal.R;
import cn.finalteam.rxgalleryfinal.bean.BucketBean;
import cn.finalteam.rxgalleryfinal.ui.widget.SquareImageView;
import cn.finalteam.rxgalleryfinal.utils.ThemeUtils;

/**
 * Desction:
 * Author:pengjianbo  Dujinyang
 * Date:16/7/4 下午5:40
 */
public class BucketAdapter extends RecyclerView.Adapter<BucketAdapter.BucketViewHolder> {

    private final List<BucketBean> mBucketList;
    private final Drawable mDefaultImage;
    private final Configuration mConfiguration;
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;
    private BucketBean mSelectedBucket;

    public BucketAdapter(
            List<BucketBean> bucketList,
            Configuration configuration,
            @ColorInt int color) {
        this.mBucketList = bucketList;
        this.mConfiguration = configuration;
        this.mDefaultImage = new ColorDrawable(color);
    }

    @Override
    public BucketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_adapter_bucket_item, parent, false);
        return new BucketViewHolder(parent, view);
    }

    @Override
    public void onBindViewHolder(BucketViewHolder holder, int position) {
        BucketBean bucketBean = mBucketList.get(position);
        String bucketName = bucketBean.getBucketName();
        if (position != 0) {
            SpannableString nameSpannable = new SpannableString(bucketName + "（" + bucketBean.getImageCount() + "）");
            nameSpannable.setSpan(new ForegroundColorSpan(Color.GRAY), bucketName.length(), nameSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            nameSpannable.setSpan(new RelativeSizeSpan(0.8f), bucketName.length(), nameSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.mTvBucketName.setText(nameSpannable);
        } else {
            holder.mTvBucketName.setText(bucketName);
        }
        if (mSelectedBucket != null && TextUtils.equals(mSelectedBucket.getBucketId(), bucketBean.getBucketId())) {
            holder.mRbSelected.setVisibility(View.VISIBLE);
        } else {
            holder.mRbSelected.setVisibility(View.GONE);
        }

        String path = bucketBean.getCover();
        mConfiguration.getImageLoader()
                .displayImage(holder.itemView.getContext(), path, holder.mIvBucketCover, mDefaultImage, mConfiguration.getImageConfig(),
                        true, mConfiguration.isPlayGif(), 100, 100, bucketBean.getOrientation());
    }

    public void setSelectedBucket(BucketBean bucketBean) {
        this.mSelectedBucket = bucketBean;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mBucketList.size();
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnRecyclerViewItemClickListener = listener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    class BucketViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView mTvBucketName;
        final SquareImageView mIvBucketCover;
        final ImageView mRbSelected;

        private final ViewGroup mParentView;

        BucketViewHolder(ViewGroup parent, View itemView) {
            super(itemView);
            this.mParentView = parent;
            mTvBucketName = (TextView) itemView.findViewById(R.id.tv_bucket_name);
            mIvBucketCover = (SquareImageView) itemView.findViewById(R.id.iv_bucket_cover);
            mRbSelected = (ImageView) itemView.findViewById(R.id.rb_selected);

            itemView.setOnClickListener(this);

            int checkTint = ThemeUtils.resolveColor(itemView.getContext(), R.attr.gallery_checkbox_button_tint_color, R.color.gallery_default_checkbox_button_tint_color);
            Drawable drawable = ContextCompat.getDrawable(parent.getContext(), R.drawable.gallery_ic_select);
            Drawable.ConstantState state = drawable.getConstantState();
            Drawable drawable1 = DrawableCompat.wrap(state == null ? drawable : state.newDrawable()).mutate();
            drawable1.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            DrawableCompat.setTint(drawable, checkTint);
            mRbSelected.setImageDrawable(drawable);
        }

        @Override
        public void onClick(View v) {
            if (mOnRecyclerViewItemClickListener != null) {
                mOnRecyclerViewItemClickListener.onItemClick(v, getLayoutPosition());
            }

            setRadioDisChecked(mParentView);
            mRbSelected.setVisibility(View.VISIBLE);
        }

        /**
         * 设置未所有Item为未选中
         */
        private void setRadioDisChecked(ViewGroup parentView) {
            if (parentView == null || parentView.getChildCount() < 1) {
                return;
            }

            for (int i = 0; i < parentView.getChildCount(); i++) {
                View itemView = parentView.getChildAt(i);
                ImageView rbSelect = (ImageView) itemView.findViewById(R.id.rb_selected);
                if (rbSelect != null) {
                    rbSelect.setVisibility(View.GONE);
                }
            }
        }
    }
}
