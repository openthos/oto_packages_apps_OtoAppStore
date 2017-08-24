package com.openthos.appstore.adapter;

import com.openthos.appstore.MainActivity;
import android.content.Context;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class BasicAdapter extends BaseAdapter {
    protected Context mContext;
    protected List mDatas;
    protected MainActivity mMainActivity;

    public BasicAdapter(Context context) {
        mDatas = new ArrayList();
        mContext = context;
        mMainActivity = (MainActivity) mContext;
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas == null ? null : mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public abstract void refreshLayout();
}
