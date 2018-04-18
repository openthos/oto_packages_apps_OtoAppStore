package org.openthos.appstore.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class CustomRecyclerView extends RecyclerView{
    private int mDisplayWidth;
    private int mFirstVisibleChildPos;
    private int mLastVisiblePosition;

    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);

        Recycler recycler = new RecyclerView.Recycler();
        int itemCount = getLayoutManager().getItemCount();
        for (int i = 0; i < itemCount; i++) {
            View view = recycler.getViewForPosition(i);
            LayoutParams p = (LayoutParams) view.getLayoutParams();
            int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
                    getPaddingLeft() + getPaddingRight(), p.width);
            int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
                    getPaddingTop() + getPaddingBottom(), p.height);
            view.measure(childWidthSpec, childHeightSpec);
            recycler.recycleView(view);
        }

        View child = recycler.getViewForPosition(0);
        int childWidth = child.getMeasuredWidth();
        int childHeight = child.getMeasuredHeight();
        int measuredWidth = getMeasuredWidth();

        setMeasuredDimension(measuredWidth - measuredWidth % childWidth, childHeight);
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        ImageView imageView = (ImageView) getTag();

        if (layoutManager.findFirstVisibleItemPosition() == mFirstVisibleChildPos
                || lastVisiblePosition == mLastVisiblePosition) {
            imageView.setVisibility(lastVisiblePosition == layoutManager.getItemCount() - 1 ?
                    View.INVISIBLE : View.VISIBLE);
        }

        View firstVisibleChild = getChildAt(0);
        if (mDisplayWidth != getDisplay().getWidth()) {
            scrollBy(mFirstVisibleChildPos * firstVisibleChild.getMeasuredWidth(), 0);
        }
        mFirstVisibleChildPos = getChildPosition(firstVisibleChild);
        mLastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        mDisplayWidth = getDisplay().getWidth();
    }
}
