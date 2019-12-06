package cn.finalteam.rxgalleryfinal.ui.fragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.rxgalleryfinal.Configuration;
import cn.finalteam.rxgalleryfinal.R;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.rxbus.RxBus;
import cn.finalteam.rxgalleryfinal.rxbus.event.CloseMediaViewPageFragmentEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.MediaCheckChangeEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.MediaViewPagerChangedEvent;
import cn.finalteam.rxgalleryfinal.ui.activity.MediaActivity;
import cn.finalteam.rxgalleryfinal.ui.adapter.MediaPreviewAdapter;
import cn.finalteam.rxgalleryfinal.ui.adapter.PreviewAdapter;
import cn.finalteam.rxgalleryfinal.utils.DeviceUtils;
import cn.finalteam.rxgalleryfinal.utils.FileSizeUtil;
import cn.finalteam.rxgalleryfinal.utils.ThemeUtils;

/**
 * Desction:图片预览
 * Author:pengjianbo  Dujinyang
 * Date:16/6/9 上午1:35
 */
public class MediaPreviewFragment extends BaseFragment implements ViewPager.OnPageChangeListener,
        View.OnClickListener {

    private static final String EXTRA_PAGE_INDEX = EXTRA_PREFIX + ".PageIndex";

    DisplayMetrics mScreenSize;

    private AppCompatCheckBox mCbCheck, boxOriginal;
    private ViewPager mViewPager;
    private List<MediaBean> mMediaBeanList;
    private RelativeLayout mRlRootView;
    private TextView tvSize;
    private RecyclerView previewRecycler;

    private MediaActivity mMediaActivity;
    private int mPagerPosition;
    private PreviewAdapter previewAdapter;
    private boolean original;//是否为原图

    public static MediaPreviewFragment newInstance(Configuration configuration, int position) {
        MediaPreviewFragment fragment = new MediaPreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_CONFIGURATION, configuration);
        bundle.putInt(EXTRA_PAGE_INDEX, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MediaActivity) {
            mMediaActivity = (MediaActivity) context;
        }
    }

    @Override
    public int getContentView() {
        return R.layout.gallery_fragment_media_preview;
    }


    @Override
    public void onViewCreatedOk(View view, @Nullable Bundle savedInstanceState) {
        mCbCheck = (AppCompatCheckBox) view.findViewById(R.id.cb_check);
        boxOriginal = view.findViewById(R.id.original_check);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mRlRootView = (RelativeLayout) view.findViewById(R.id.rl_root_view);
        previewRecycler = view.findViewById(R.id.recyclerView);
        tvSize = view.findViewById(R.id.tv_size);

        boxOriginal.setVisibility(mConfiguration.isImage() ? View.VISIBLE : View.GONE);
        tvSize.setVisibility(mConfiguration.isImage() ? View.VISIBLE : View.GONE);

        mScreenSize = DeviceUtils.getScreenSize(getContext());
        mMediaBeanList = new ArrayList<>();
        if (mMediaActivity.getCheckedList() != null) {
            mMediaBeanList.addAll(mMediaActivity.getCheckedList());
        }

        MediaPreviewAdapter mMediaPreviewAdapter = new MediaPreviewAdapter(mMediaBeanList,
                mScreenSize.widthPixels, mScreenSize.heightPixels, mConfiguration,
                ThemeUtils.resolveColor(getActivity(), R.attr.gallery_page_bg, R.color.gallery_default_page_bg),
                ContextCompat.getDrawable(getActivity(), ThemeUtils.resolveDrawableRes(getActivity(), R.attr.gallery_default_image, R.drawable.gallery_default_image)));
        mViewPager.setAdapter(mMediaPreviewAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                previewAdapter.setCheck(mMediaBeanList.get(position));
                tvSize.setText("(" + Formatter.formatFileSize(getContext(), getImageSize(mMediaBeanList.get(position))) + ")");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mCbCheck.setOnClickListener(this);
        boxOriginal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                original = isChecked;
            }
        });

        previewAdapter = new PreviewAdapter(mMediaActivity, mMediaBeanList, mScreenSize.widthPixels, mConfiguration);
        previewRecycler.setAdapter(previewAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        previewRecycler.setLayoutManager(linearLayoutManager);
        previewAdapter.setOnItemClickListener(new PreviewAdapter.OnItemClickListener() {
            @Override
            public void onItem(int position) {
                mViewPager.setCurrentItem(position);
            }
        });
        if (mMediaBeanList != null && mMediaBeanList.size() > 0) {
            previewAdapter.setCheck(mMediaBeanList.get(0));
            tvSize.setText("(" + Formatter.formatFileSize(getContext(), getImageSize(mMediaBeanList.get(0))) + ")");
        }
        if (savedInstanceState != null) {
            mPagerPosition = savedInstanceState.getInt(EXTRA_PAGE_INDEX);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewPager.setCurrentItem(mPagerPosition, false);
        mViewPager.addOnPageChangeListener(this);
        //#ADD UI预览数量的BUG
        RxBus.getDefault().post(new MediaViewPagerChangedEvent(mPagerPosition, mMediaBeanList.size(), true));
    }

    @Override
    public void setTheme() {
        super.setTheme();
        int checkTint = ThemeUtils.resolveColor(getContext(), R.attr.gallery_checkbox_button_tint_color, R.color.gallery_default_checkbox_button_tint_color);
        CompoundButtonCompat.setButtonTintList(mCbCheck, ColorStateList.valueOf(checkTint));
        CompoundButtonCompat.setButtonTintList(boxOriginal, ColorStateList.valueOf(checkTint));
        int cbTextColor = ThemeUtils.resolveColor(getContext(), R.attr.gallery_checkbox_text_color, R.color.gallery_default_checkbox_text_color);
        mCbCheck.setTextColor(cbTextColor);
        boxOriginal.setTextColor(cbTextColor);
        tvSize.setTextColor(cbTextColor);

        int pageColor = ThemeUtils.resolveColor(getContext(), R.attr.gallery_page_bg, R.color.gallery_default_page_bg);
        mRlRootView.setBackgroundColor(pageColor);
    }

    @Override
    protected void onFirstTimeLaunched() {

    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mPagerPosition = savedInstanceState.getInt(EXTRA_PAGE_INDEX);
        }
    }

    @Override
    protected void onSaveState(Bundle outState) {
        if (outState != null) {
            outState.putInt(EXTRA_PAGE_INDEX, mPagerPosition);
        }
    }

    private long getImageSize(MediaBean mediaBean) {
        if (mediaBean.getLength() > 0) {
            return mediaBean.getLength();
        }
        return FileSizeUtil.getAutoFileOrFilesSize(mediaBean.getOriginalPath());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mPagerPosition = position;
        MediaBean mediaBean = mMediaBeanList.get(position);
        mCbCheck.setChecked(false);
        //判断是否选择
        if (mMediaActivity != null && mMediaActivity.getCheckedList() != null) {
            mCbCheck.setChecked(mMediaActivity.getCheckedList().contains(mediaBean));
        }

        RxBus.getDefault().post(new MediaViewPagerChangedEvent(position, mMediaBeanList.size(), true));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * 改变选择
     */
    @Override
    public void onClick(View view) {
        int position = mViewPager.getCurrentItem();
        MediaBean mediaBean = mMediaBeanList.get(position);
        if (mConfiguration.getMaxSize() == mMediaActivity.getCheckedList().size()
                && !mMediaActivity.getCheckedList().contains(mediaBean)) {
            Toast.makeText(getContext(), getResources()
                    .getString(R.string.gallery_image_max_size_tip, mConfiguration.getMaxSize()), Toast.LENGTH_SHORT).show();
            mCbCheck.setChecked(false);
        } else {
            RxBus.getDefault().post(new MediaCheckChangeEvent(mediaBean));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPagerPosition = 0;
        RxBus.getDefault().post(new CloseMediaViewPageFragmentEvent());
    }

    public boolean isOriginal() {
        return original;
    }

    public void setOriginal(boolean original) {
        this.original = original;
    }
}
