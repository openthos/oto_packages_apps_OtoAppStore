package com.openthos.appstore.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.provider.SyncStateContract;
import android.util.AttributeSet;
import android.widget.ListView;

import com.openthos.appstore.app.Constants;

/**
 * Created by luojunhuan on 16-10-31.
 */
public class CustomListView extends ListView {
    public CustomListView(Context context) {
        super(context);
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE & Constants.HEIGHT_MASK,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, height);
    }
}
