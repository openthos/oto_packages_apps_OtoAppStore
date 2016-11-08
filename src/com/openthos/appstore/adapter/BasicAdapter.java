package com.openthos.appstore.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luojunhuan on 16-11-7.
 */
public class BasicAdapter extends BaseAdapter {
    protected Context mContext;
    protected boolean mIsAll;
    protected List mDatas;
    protected int mFromFragment;

    public BasicAdapter(Context context) {
        mDatas = new ArrayList();
        mContext = context;
    }

    public BasicAdapter(Context context, boolean isAll) {
        this(context);
        mIsAll = isAll;
    }

    public BasicAdapter(Context context, int fromFragment) {
        this(context);
        mFromFragment = fromFragment;
    }

    public BasicAdapter(Context context, boolean isAll, int fromFragment) {
        this(context, isAll);
        mFromFragment = fromFragment;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
