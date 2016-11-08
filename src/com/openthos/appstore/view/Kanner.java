package com.openthos.appstore.view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Kanner extends FrameLayout {
    private int mCount;
    //    private List<View> mImageViews;
    private List<ImageView> mImageViews;
    private Context mContext;
    private ViewPager mPager;
    private boolean mIsAutoPlay;
    private int mCurrentItem;
    private int mDelayTime;
    private LinearLayout mLlDot;
    private List<ImageView> mIvDots;
    private Handler mHandler = new Handler();
    private List<String> mImagesUrl = new ArrayList<>();

    public Kanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initData();
    }

    public Kanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Kanner(Context context) {
        this(context, null);
    }

    private void initData() {
        mImageViews = new ArrayList<>();
        mIvDots = new ArrayList<ImageView>();
        mDelayTime = Constants.DELAY_TIME_2;
    }

    public void setCurrentItem(int currentItem) {
        mIsAutoPlay = false;
        mCurrentItem = currentItem;
        mPager.setCurrentItem(currentItem, false);
        mIsAutoPlay = true;
    }

    public int getCurrentItem() {
        return mCurrentItem;
    }

    public int getImageViewsSize() {
        return mImageViews.size();
    }

    public void setImagesUrl(String[] imagesUrl) {
        initLayout();
        initImgFromNet(imagesUrl);
        showTime();
    }

    public void setImagesUrl(List<String> imagesUrl) {
        mImagesUrl = imagesUrl;
        initLayout();
        initImgFromNet(imagesUrl);
        showTime();
    }

    public void setImagesRes(int[] imagesRes) {
        initLayout();
        initImgFromRes(imagesRes);
        showTime();
    }

    private void initLayout() {
        mImageViews.clear();
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.kanner_layout, this, true);
        mPager = (ViewPager) view.findViewById(R.id.vp);
        mLlDot = (LinearLayout) view.findViewById(R.id.ll_dot);
        mLlDot.removeAllViews();
    }

    private void initImgFromRes(int[] imagesRes) {
        mCount = imagesRes.length;
        for (int i = 0; i < mCount; i++) {
            ImageView iv_dot = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 5;
            params.rightMargin = 5;
            iv_dot.setImageResource(R.drawable.dot_blur);
            mLlDot.addView(iv_dot, params);
            mIvDots.add(iv_dot);
        }
        mIvDots.get(0).setImageResource(R.drawable.dot_focus);

        for (int i = 0; i <= mCount + 1; i++) {
            ImageView iv = new ImageView(mContext);
            iv.setScaleType(ScaleType.FIT_XY);
//            iv.setBackgroundResource(R.drawable.loading);
            if (i == 0) {
                iv.setImageResource(imagesRes[mCount - 1]);
            } else if (i == mCount + 1) {
                iv.setImageResource(imagesRes[0]);
            } else {
                iv.setImageResource(imagesRes[i - 1]);
            }
            mImageViews.add(iv);
        }
    }

    private void initImgFromNet(String[] imagesUrl) {
        mCount = imagesUrl.length;
        for (int i = 0; i < mCount; i++) {
            ImageView iv_dot = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 5;
            params.rightMargin = 5;
            iv_dot.setImageResource(R.drawable.dot_blur);
            mLlDot.addView(iv_dot, params);
            mIvDots.add(iv_dot);
        }
        mIvDots.get(0).setImageResource(R.drawable.dot_focus);

        for (int i = 0; i <= mCount + 1; i++) {
            ImageView iv = new ImageView(mContext);
            iv.setScaleType(ScaleType.FIT_XY);
//            iv.setBackgroundResource(R.mipmap.loading);
            if (i == 0) {
                Picasso.with(mContext).load(imagesUrl[mCount - 1]).into(iv);
            } else if (i == mCount + 1) {
                Picasso.with(mContext).load(imagesUrl[0]).into(iv);
            } else {
                Picasso.with(mContext).load(imagesUrl[i - 1]).into(iv);

            }
            mImageViews.add(iv);
        }
    }

    private void initImgFromNet(List<String> imagesUrl) {
        mCount = imagesUrl.size();
        for (int i = 0; i < mCount; i++) {
            ImageView iv_dot = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 5;
            params.rightMargin = 5;
            iv_dot.setImageResource(R.drawable.dot_blur);
            mLlDot.addView(iv_dot, params);
            mIvDots.add(iv_dot);
        }
        mIvDots.get(0).setImageResource(R.drawable.dot_focus);
    }

    private void showTime() {
        mPager.setAdapter(new KannerPagerAdapter());
        mPager.setFocusable(true);
        mPager.setCurrentItem(1);
        mCurrentItem = 1;
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
        startPlay();
    }

    private void startPlay() {
        mIsAutoPlay = true;
        mHandler.postDelayed(task, Constants.DELAY_TIME_2);
    }

    private final Runnable task = new Runnable() {

        @Override
        public void run() {
            if (mIsAutoPlay) {
                mCurrentItem = mCurrentItem % (mCount + 1) + 1;
                if (mCurrentItem == 1) {
                    mPager.setCurrentItem(mCurrentItem, false);
                    mHandler.post(task);
                } else {
                    mPager.setCurrentItem(mCurrentItem);
                    mHandler.postDelayed(task, Constants.DELAY_TIME_3);
                }
            } else {
                mHandler.postDelayed(task, Constants.DELAY_TIME_5);
            }
        }
    };

    class KannerPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImagesUrl.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int j) {
//            View view = mImageViews.get(position);
//            view.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onItemClickListener.onItemClick(v,position - 1);
//                }
//            });
            View ret = LayoutInflater.from(mContext).
                    inflate(R.layout.home_view, container, false);
            ImageView img1 = (ImageView) ret.findViewById(R.id.home_view_img1);
            ImageView img2 = (ImageView) ret.findViewById(R.id.home_view_img2);
            ImageView img3 = (ImageView) ret.findViewById(R.id.home_view_img3);
            ImageView[] imgs = new ImageView[]{img1, img2, img3};

            for (int i = 0; i < imgs.length; i++) {
                final int finalI = i;
                imgs[i].setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       mOnItemClickListener.onItemClick(v, j - 1 + finalI);
                    }
                });
            }

            int size = mImagesUrl.size();
            if (size == 1) {
                imgs[1].setVisibility(View.GONE);
                imgs[2].setVisibility(View.GONE);
                Picasso.with(mContext).load(mImagesUrl.get(0)).into(imgs[0]);
            } else if (size == 2) {
                imgs[2].setVisibility(View.GONE);
                Picasso.with(mContext).load(mImagesUrl.get(0)).into(imgs[0]);
                Picasso.with(mContext).load(mImagesUrl.get(1)).into(imgs[1]);
            } else if (size == 3) {
                Picasso.with(mContext).load(mImagesUrl.get(0)).into(imgs[0]);
                Picasso.with(mContext).load(mImagesUrl.get(1)).into(imgs[1]);
                Picasso.with(mContext).load(mImagesUrl.get(2)).into(imgs[2]);
            } else {
                if (j == 0 || j == size) {
                    //mImageLoader.displayImage(mImagesUrl.get(mCount - 1), iv, options);
                    Picasso.with(mContext).load(mImagesUrl.get(size - 1)).into(imgs[0]);
                    Picasso.with(mContext).load(mImagesUrl.get(0)).into(imgs[1]);
                    Picasso.with(mContext).load(mImagesUrl.get(1)).into(imgs[2]);
                } else if (j == size - 1) {
                    Picasso.with(mContext).load(mImagesUrl.get(size - 2)).into(imgs[0]);
                    Picasso.with(mContext).load(mImagesUrl.get(size - 1)).into(imgs[1]);
                    Picasso.with(mContext).load(mImagesUrl.get(0)).into(imgs[2]);
                } else if (j == size + 1) {
                    Picasso.with(mContext).load(mImagesUrl.get(0)).into(imgs[0]);
                    Picasso.with(mContext).load(mImagesUrl.get(1)).into(imgs[1]);
                    Picasso.with(mContext).load(mImagesUrl.get(2)).into(imgs[2]);
                } else {
                    Picasso.with(mContext).load(mImagesUrl.get(j - 1)).into(imgs[0]);
                    Picasso.with(mContext).load(mImagesUrl.get(j)).into(imgs[1]);
                    Picasso.with(mContext).load(mImagesUrl.get(j + 1)).into(imgs[2]);
                }
            }
            container.addView(ret);
            return ret;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
//            container.removeView(mImageViews.get(position));
        }
    }

    class MyOnPageChangeListener implements OnPageChangeListener {

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
                    if (mPager.getCurrentItem() == 1) {
                        mPager.setCurrentItem(mCount, false);
                    } else if (mPager.getCurrentItem() == mCount + 1) {
                        mPager.setCurrentItem(1, false);
                    }
                    mCurrentItem = mPager.getCurrentItem();
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
            for (int i = 0; i < mIvDots.size(); i++) {
                if (i == position - 1) {
                    mIvDots.get(i).setImageResource(R.drawable.dot_focus);
                } else {
                    mIvDots.get(i).setImageResource(R.drawable.dot_blur);
                }
            }
        }
    }

    public void removeCallbacksAndMessages() {
        mHandler.removeCallbacksAndMessages(null);
        mContext = null;
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
