package com.openthos.appstore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import com.openthos.appstore.view.CustomRatingBar;

import java.util.ArrayList;
import java.util.List;

public class RecyclerItemAdapter extends RecyclerView.Adapter<RecyclerItemAdapter.HorViewHolder>
        implements View.OnClickListener {
    private List<AppItemInfo> mDatas;
    private Context mContext;

    public RecyclerItemAdapter(Context context) {
        mContext = context;
        mDatas = new ArrayList<>();
    }

    @Override
    public HorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.app_item, parent, false);
        return new HorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HorViewHolder holder, int position) {
        if (mDatas != null && mDatas.size() != 0) {
            AppItemInfo appItemInfo = mDatas.get(position);
            ImageCache.loadImage(holder.icon, appItemInfo.getIconUrl());
            holder.appName.setText(appItemInfo.getAppName());
            holder.type.setText(appItemInfo.getType());
            holder.ratingBar.setRating(appItemInfo.getStar());
            holder.layout.setOnClickListener(this);
            holder.layout.setTag(appItemInfo);
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
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

    class HorViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView appName;
        private TextView type;
        private CustomRatingBar ratingBar;
        private LinearLayout layout;

        public HorViewHolder(View view) {
            super(view);
            layout = (LinearLayout) view.findViewById(R.id.app_item_layout);
            icon = (ImageView) view.findViewById(R.id.app_item_img);
            appName = (TextView) view.findViewById(R.id.app_item_name);
            type = (TextView) view.findViewById(R.id.app_item_type);
            ratingBar = (CustomRatingBar) view.findViewById(R.id.app_item_ratingbar);
        }
    }
}
