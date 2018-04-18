package org.openthos.appstore.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class CustomProgressBar extends ProgressBar{
    private String mText;
    private Paint mPaint;

    public CustomProgressBar(Context context) {
        super(context);
        initText();
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initText();
    }


    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initText();
    }

    @Override
    public synchronized void setProgress(int progress) {
        setText(progress);
        super.setProgress(progress);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        mPaint.getTextBounds(mText, 0, mText.length(), rect);
        int x = (getWidth() / 2) - rect.centerX();
        int y = (getHeight() / 2) - rect.centerY();
        canvas.drawText(mText, x, y, mPaint);
    }

    private void initText() {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);

    }

    private void setText() {
        setText(getProgress());
    }

    private void setText(int progress) {
        int i = (progress * 100) / getMax();
        mText = String.valueOf(i) + "%";
    }
}
