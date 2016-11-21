package com.openthos.appstore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.openthos.appstore.R;
import com.openthos.appstore.bean.AppTypeInfo;
import com.openthos.appstore.view.CustomListView;

import java.util.List;

/**
 * Created by luojunhuan on 16-10-27.
 */
public class AppTypeAdapter extends BasicAdapter
        implements View.OnClickListener, AdapterView.OnItemClickListener {

    public AppTypeAdapter(Context context) {
        super(context);
    }

    @Override
    public long getItemId(int position) {
        return mDatas == null ? -1 : ((AppTypeInfo) mDatas.get(position)).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.app_type, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        if (mDatas != null && mDatas.size() != 0) {
            AppTypeInfo appTypeInfo = (AppTypeInfo) mDatas.get(position);
            holder.name.setText(appTypeInfo.getName());
            holder.more.setOnClickListener(this);
            holder.more.setTag(position);
            AppTypeListviewAdapter adapter = new AppTypeListviewAdapter(mContext);
            holder.listview.setAdapter(adapter);
            adapter.addDatas(appTypeInfo.getList());
            holder.listview.setOnItemClickListener(this);
        }

        return convertView;
    }

    @Override
    public void onClick(View v) {
        int tag = (int) v.getTag();
        Toast.makeText(mContext, tag + "wei zuo tiao zhuan",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(mContext, position + "", Toast.LENGTH_SHORT).show();
        //TODO
    }

    class ViewHolder {
        private TextView name;
        private TextView more;
        private CustomListView listview;

        public ViewHolder(View view) {
            name = ((TextView) view.findViewById(R.id.app_type_name));
            more = (TextView) view.findViewById(R.id.app_type_more);
            listview = ((CustomListView) view.findViewById(R.id.app_type_listview));
        }
    }

    public void addDatas(List<AppTypeInfo> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }
}