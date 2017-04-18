package com.openthos.appstore.view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.BannerUrl;
import com.openthos.appstore.utils.ImageCache;

import java.util.ArrayList;
import java.util.List;

public class BannerView extends FrameLayout {
    private static final int MARGIN_LENGTH = 5;
    private Context mContext;
    private ViewPager mPager;
    private LinearLayout mDotLayout;
    private boolean mIsAutoPlay;
    private int mCurrentItem;
    private Handler mHandler = new Handler();
    private List<ImageView> mImageDots;
    private List<BannerUrl> mImageUrls;
    private int mCount;

    public BannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        mImageDots = new ArrayList<>();
        initView();
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context) {
        this(context, null);
    }

    public void setImageUrls(List<BannerUrl> imgUrls) {
        if (imgUrls != null) {
            mImageUrls = imgUrls;
            mCount = imgUrls.size();
            mDotLayout.removeAllViews();
            mImageDots.clear();
            initImageDots();
            initData();
            initListener();
            startPlay();
        }

    }

    private void initData() {
        mPager.setAdapter(new BannerPagerAdapter());
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.banner, this, true);
        mPager = (ViewPager) view.findViewById(R.id.banner_viewpager);
        mDotLayout = (LinearLayout) view.findViewById(R.id.banner_dotlayout);
    }

    private void initListener() {
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
        mPager.setCurrentItem(mCount);
    }

    private void initImageDots() {
        for (int i = 0; i < mCount; i++) {
            ImageView imgDot = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = MARGIN_LENGTH;
            params.rightMargin = MARGIN_LENGTH;
            imgDot.setImageResource(R.drawable.dot_blur);
            mDotLayout.addView(imgDot);
            mImageDots.add(imgDot);
        }
        mImageDots.get(0).setImageResource(R.drawable.dot_focus);
    }

    public void startPlay() {
        removeCallbacksAndMessages();
        mIsAutoPlay = true;
        mHandler.postDelayed(task, Constants.TIME_TWO_SECONDS);
    }

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (mIsAutoPlay) {
                mCurrentItem = mCurrentItem + 1;
                mPager.setCurrentItem(mCurrentItem);
                mHandler.postDelayed(task, Constants.TIME_THREE_SECONDS);
            } else {
                mHandler.postDelayed(task, Constants.TIME_FIVE_SECONDS);
            }
        }
    };

    class BannerPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.
                    from(mContext).inflate(R.layout.banner_item, container, false);
            ViewHolder holder = new ViewHolder(view);
            if (mCount >= 3) {
                ImageCache.loadImage(holder.bannerImg1,
                        mImageUrls.get(position % mCount).getImgUrl());
                ImageCache.loadImage(holder.bannerImg2,
                        mImageUrls.get((position + 1) % mCount).getImgUrl());
                ImageCache.loadImage(holder.bannerImg3,
                        mImageUrls.get((position + 2) % mCount).getImgUrl());
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    class ViewHolder {
        private ImageView bannerImg1;
        private ImageView bannerImg2;
        private ImageView bannerImg3;

        public ViewHolder(View view) {
            bannerImg1 = (ImageView) view.findViewById(R.id.banner_item_img1);
            bannerImg2 = (ImageView) view.findViewById(R.id.banner_item_img2);
            bannerImg3 = (ImageView) view.findViewById(R.id.banner_item_img3);
        }
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    mIsAutoPlay = false;
                    break;
                case ViewPager.SCROLL_STATE_SETTLING:
                    mIsAutoPlay = true;
                    break;
                case ViewPager.SCROLL_STATE_IDLE:
                    mIsAutoPlay = true;
                    break;
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mCurrentItem = position;
            setSelectDot(position % mCount);
        }

        public void setSelectDot(int position) {
            for (int i = 0; i < mCount; i++) {
                if (i == position) {
                    mImageDots.get(i).setImageResource(R.drawable.dot_focus);
                } else {
                    mImageDots.get(i).setImageResource(R.drawable.dot_blur);
                }
            }
        }
    }

    public void removeCallbacksAndMessages() {
        mHandler.removeCallbacksAndMessages(null);
    }
}
