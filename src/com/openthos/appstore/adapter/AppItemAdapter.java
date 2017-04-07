package com.openthos.appstore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppItemInfo;
import com.openthos.appstore.utils.ImageCache;
import com.openthos.appstore.utils.Tools;
import com.openthos.appstore.view.CustomRatingBar;

import java.util.List;

public class AppItemAdapter extends BasicAdapter implements View.OnClickListener {

    public AppItemAdapter(Context context) {
        super(context);
    }

    @Override
    public long getItemId(int position) {
        return Tools.tranLong(((AppItemInfo) mDatas.get(position)).getTaskId());
    }

    @Override
    public Object getItem(int position) {
        return (AppItemInfo) mDatas.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.app_item, viewGroup, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();

        if (mDatas != null && mDatas.size() != 0) {
            AppItemInfo appItemInfo = (AppItemInfo) mDatas.get(position);
            ImageCache.loadImage(holder.icon, appItemInfo.getIconUrl());
            holder.appName.setText(appItemInfo.getAppName());
            holder.type.setText(appItemInfo.getType());
            holder.ratingBar.setRating(appItemInfo.getStar());
            holder.layout.setOnClickListener(this);
            holder.layout.setTag(appItemInfo);
        }
        return convertView;
    }

    public void addDatas(List<AppItemInfo> datas) {
        if (datas != null) {
            mDatas.clear();
            mDatas.addAll(datas);
            notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        MainActivity.mHandler.sendMessage(
                MainActivity.mHandler.obtainMessage(Constants.DETAIL_FRAGMENT, view.getTag()));
    }

    private class ViewHolder {
        private ImageView icon;
        private TextView appName;
        private TextView type;
        private CustomRatingBar ratingBar;
        private LinearLayout layout;

        public ViewHolder(View view) {
            layout = (LinearLayout) view.findViewById(R.id.app_item_layout);
            icon = (ImageView) view.findViewById(R.id.app_item_img);
            appName = (TextView) view.findViewById(R.id.app_item_name);
            type = (TextView) view.findViewById(R.id.app_item_type);
            ratingBar = (CustomRatingBar) view.findViewById(R.id.app_item_ratingbar);
        }
    }
}
