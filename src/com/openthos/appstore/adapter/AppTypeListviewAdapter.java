package com.openthos.appstore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.openthos.appstore.R;
import com.openthos.appstore.bean.AppTypeListviewInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luojunhuan on 16-10-27.
 */
public class AppTypeListviewAdapter extends BasicAdapter {

    public AppTypeListviewAdapter(Context context,int fromFragment) {
        super(context,fromFragment);
        mDatas = new ArrayList<>();
    }

    @Override
    public long getItemId(int position) {
        return mDatas == null ? -1 :
                ((AppTypeListviewInfo) mDatas.get(position)).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.app_type_listview, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        if (mDatas != null && mDatas.size() != 0) {
            AppTypeListviewInfo appTypeListviewInfo = (AppTypeListviewInfo) mDatas.get(position);
            holder.content.setText(appTypeListviewInfo.getContent());
        }

        return convertView;
    }

    class ViewHolder {
        private TextView content;

        public ViewHolder(View view) {
            content = (TextView) view.findViewById(R.id.app_type_listview_content);
        }
    }

    public void addDatas(List<AppTypeListviewInfo> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }
}