package com.openthos.appstore.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.openthos.appstore.R;

public class CustomRatingBar extends View {

    private static final int BLACK = 0;
    private static final int PURPLE = 1;

    private Bitmap mDrawable;
    private Bitmap mBackground;
    private Context mContext;
    private int mNumStars = 5;
    private float mRating = 0;
    private boolean mIndicator;
    private int mType;

    public interface OnRatingBarChangeListener {
        void onRatingChanged(CustomRatingBar customRatingBar, float rating, boolean fromUser);
    }

    private OnRatingBarChangeListener mOnRatingBarChangeListener;

    public CustomRatingBar(Context context) {
        this(context, null);
    }

    public CustomRatingBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.customRatingBarStyle);
    }

    public CustomRatingBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColoredRatingBar,
                defStyle, 0);
        mIndicator =  typedArray.getBoolean(R.styleable.ColoredRatingBar_indicator, false);
        mRating = typedArray.getFloat(R.styleable.ColoredRatingBar_rating, -1);
        mType = typedArray.getInt(R.styleable.ColoredRatingBar_type, 0);
        typedArray.recycle();
        init(context);
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    private void init(Context context) {
        mContext = context;
        Resources res = getResources();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        switch (mType){
            case BLACK:
                mDrawable = BitmapFactory.decodeResource(res,R.drawable.star_black_select,options);
                mBackground = BitmapFactory.decodeResource(res, R.drawable.star_black,options);
                break;
            case PURPLE:
                mDrawable = BitmapFactory.decodeResource(res,R.drawable.star_black_select,options);
                mBackground = BitmapFactory.decodeResource(res, R.drawable.star_black,options);
                break;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mNumStars; i++) {
            drawStar(canvas, i);
        }
    }

    private void drawStar(Canvas canvas, int position) {
        float fraction = mRating - (position);
        Bitmap ratedStar = getRatedStar();
        if ((position + 1) < mRating) {
            canvas.drawBitmap(ratedStar, (position * ratedStar.getWidth()), 0, null);
        } else {
            if (fraction > 0 && fraction <= 1) {
                int sourceWidth = ratedStar.getWidth();
                int sourceHeight = ratedStar.getHeight();

                int targetWidth = (int) (ratedStar.getWidth() * fraction);
                int bgWidth = sourceWidth - targetWidth;

                if (targetWidth > 0) {
                    Bitmap croppedBmp =
                            Bitmap.createBitmap(ratedStar, 0, 0, targetWidth, sourceHeight);
                    canvas.drawBitmap(croppedBmp, (position * sourceWidth), 0, null);
                }
                if (bgWidth > 0) {
                    Bitmap croppedBg =
                            Bitmap.createBitmap(mBackground, targetWidth, 0, bgWidth, sourceHeight);
                    canvas.drawBitmap(croppedBg, (position * sourceWidth) + targetWidth, 0, null);
                }
            } else {
                canvas.drawBitmap(mBackground, (position * mBackground.getWidth()), 0, null);
            }
        }
    }

    private Bitmap getRatedStar() {
        return mDrawable;
    }

    public int getNumStars() {
        return mNumStars;
    }

    public void setNumStars(int numStars) {
        this.mNumStars = numStars;
    }

    public float getRating() {
        return mRating;
    }

    public void setRating(float rating) {
        setRating(rating, false);
    }

    void setRating(float rating, boolean fromUser) {
        if (rating > mNumStars) {
            this.mRating = mNumStars;
        }
        this.mRating = rating;
        invalidate();
        dispatchRatingChange(fromUser);
    }

    public boolean isIndicator() {
        return mIndicator;
    }

    public void setIndicator(boolean indicator) {
        this.mIndicator = indicator;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mBackground != null) {

            final int width = mBackground.getWidth() * mNumStars;
            final int height = mBackground.getHeight();
            setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec, 0),
                    resolveSizeAndState(height, heightMeasureSpec, 0));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mIndicator) {
            return false;
        }

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                float newRating = getRelativePosition(event.getX());
                if (newRating != mRating) {
                    setRating(newRating, true);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return true;
    }

    private float getRelativePosition(float x) {
        float position = x / mBackground.getWidth();
        position = Math.max(position, 0);
        return Math.min(position, mNumStars);
    }

    public void setOnRatingBarChangeListener(OnRatingBarChangeListener listener) {
        mOnRatingBarChangeListener = listener;
    }

    public OnRatingBarChangeListener getOnRatingBarChangeListener() {
        return mOnRatingBarChangeListener;
    }

    void dispatchRatingChange(boolean fromUser) {
        if (mOnRatingBarChangeListener != null) {
            mOnRatingBarChangeListener.onRatingChanged(this, getRating(),
                    fromUser);
        }
    }
}
