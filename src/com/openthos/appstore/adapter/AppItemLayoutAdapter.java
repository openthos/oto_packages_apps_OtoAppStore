package com.openthos.appstore.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppItemLayoutInfo;

import java.util.List;

public class AppItemLayoutAdapter extends BasicAdapter implements View.OnClickListener {

    public AppItemLayoutAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.app_item_layout, viewGroup, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        if (mDatas != null && mDatas.size() != 0) {
            AppItemLayoutInfo appItemLayoutInfo = (AppItemLayoutInfo) mDatas.get(position);
            holder.type.setText(appItemLayoutInfo.getType());
            holder.whole.setText(appItemLayoutInfo.getWhole());
            RecyclerItemAdapter recyclerItemAdapter = new RecyclerItemAdapter(mContext);
            LinearLayoutManager layout = new LinearLayoutManager(mContext);
            layout.setOrientation(LinearLayoutManager.HORIZONTAL);
            holder.recyclerView.setLayoutManager(layout);
            holder.recyclerView.setHasFixedSize(true);
            holder.recyclerView.setAdapter(recyclerItemAdapter);
            recyclerItemAdapter.addDatas(appItemLayoutInfo.getAppItemInfoList());
            holder.whole.setOnClickListener(this);
            holder.whole.setTag(appItemLayoutInfo.getAppItemInfoList());
        }
        return convertView;
    }

    public void addDatas(List<AppItemLayoutInfo> datas, boolean isAll) {
        if (datas != null) {
            mDatas.clear();
            mDatas.addAll(datas);
            notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        MainActivity.mHandler.sendMessage(
                MainActivity.mHandler.obtainMessage(Constants.MORE_FRAGMENT, view.getTag()));
    }

    private class ViewHolder {
        private TextView type;
        private TextView whole;
        private RecyclerView recyclerView;

        public ViewHolder(View view) {
            type = (TextView) view.findViewById(R.id.app_item_layout_type);
            whole = (TextView) view.findViewById(R.id.app_item_layout_whole);
            recyclerView = (RecyclerView) view.findViewById(R.id.app_item_layout_recycler);
        }
    }
}
