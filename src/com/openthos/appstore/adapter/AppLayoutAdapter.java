package com.openthos.appstore.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppLayoutInfo;
import com.openthos.appstore.bean.SQLDownLoadInfo;
import com.openthos.appstore.view.CustomGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luojunhuan on 16-10-26.
 */
public class AppLayoutAdapter extends BasicAdapter
        implements View.OnClickListener, AppLayoutGridviewAdapter.UpdateProgress {
    private int mNumColumns;

    public AppLayoutAdapter(Context context, int numColumns, boolean isAll) {
        super(context, isAll);
        mDatas = new ArrayList<AppLayoutInfo>();
        if (numColumns != 0) {
            mNumColumns = numColumns;
        } else {
            mNumColumns = Constants.GRIDVIEW_NUM_COLUMS;
        }
    }

    @Override
    public long getItemId(int position) {
        return mDatas == null ? -1 : ((AppLayoutInfo) mDatas.get(position)).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.app_layout, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        if (mDatas != null && mDatas.size() != 0) {
            AppLayoutInfo appLayoutInfo = (AppLayoutInfo) mDatas.get(position);
            holder.name.setText(getType(appLayoutInfo.getType()));
            AppLayoutGridviewAdapter appLayoutGridviewAdapter =
                    new AppLayoutGridviewAdapter(mContext, mIsAll);
            holder.gridView.setAdapter(appLayoutGridviewAdapter);
            appLayoutGridviewAdapter.addDatas(appLayoutInfo.getAppLayoutGridviewList());
            appLayoutGridviewAdapter.setUpdateProcessListener(this);

            if (mIsAll) {
                holder.more.setVisibility(View.GONE);
            } else {
                holder.more.setVisibility(View.VISIBLE);
                holder.more.setOnClickListener(this);
                holder.more.setTag(position);
            }
            holder.gridView.setNumColumns(mNumColumns);
        }

        return convertView;
    }

    @Override
    public void onClick(View v) {
        int tag = (int) v.getTag();
        AppLayoutInfo appLayoutInfo = (AppLayoutInfo) mDatas.get(tag);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.APP_LAYOUT_INFO, appLayoutInfo);
        Message message = MainActivity.mHandler.obtainMessage();
        message.what = Constants.MORE_FRAGMENT;
        message.setData(bundle);
        MainActivity.mHandler.sendMessage(message);
    }

    @Override
    public void onProgress(SQLDownLoadInfo sqlDownLoadInfo) {
        notifyDataSetChanged();
    }

    class ViewHolder {
        private TextView name;
        private TextView more;
        private CustomGridView gridView;

        public ViewHolder(View view) {
            name = ((TextView) view.findViewById(R.id.app_layout_name));
            more = (TextView) view.findViewById(R.id.app_layout_more);
            gridView = ((CustomGridView) view.findViewById(R.id.app_layout_gridview));
        }
    }

    public void addDatas(List<AppLayoutInfo> datas) {
        if (datas != null) {
            mDatas.clear();
            mDatas.addAll(datas);
            notifyDataSetChanged();
        }
    }

    public void addItem(AppLayoutInfo datas) {
        if (datas != null) {
            mDatas.clear();
            mDatas.add(datas);
            notifyDataSetChanged();
        }
    }
}
