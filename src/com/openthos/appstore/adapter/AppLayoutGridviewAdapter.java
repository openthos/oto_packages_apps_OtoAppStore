package com.openthos.appstore.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.openthos.appstore.R;
import com.openthos.appstore.activity.DetailActivity;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppLayoutGridviewInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luojunhuan on 16-10-26.
 */
public class AppLayoutGridviewAdapter extends BasicAdapter
        implements View.OnClickListener {

    public AppLayoutGridviewAdapter(Context context, int fromFragment, boolean isAll) {
        super(context, isAll);
        mFromFragment = fromFragment;
        mDatas = new ArrayList<>();
    }

    @Override
    public long getItemId(int position) {
        return mDatas == null ? -1 : ((AppLayoutGridviewInfo)mDatas.get(position)).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.app_layout_gridview, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        if (mDatas != null && mDatas.size() != 0) {
            AppLayoutGridviewInfo appLayoutGridviewInfo =
                                      (AppLayoutGridviewInfo) mDatas.get(position);
//            LogUtils.printLog("info1",itemListGridviewInfo.getIconUrl());
            Picasso.with(mContext).load(appLayoutGridviewInfo.getIconUrl()).into(holder.icon);
            holder.name.setText(appLayoutGridviewInfo.getName());
            holder.type.setText(appLayoutGridviewInfo.getType());
            switch (appLayoutGridviewInfo.getState()) {
                case Constants.INSTALL_BUTTON_NOT_INSTALL:
                    holder.install.setText(R.string.not_install);
                    holder.install.setBackgroundResource(R.drawable.shape_button_white_cyan);
                    holder.install.setTextColor(mContext.getResources().getColor(
                            R.color.button_cyan));
                    break;
                case Constants.INSTALL_BUTTON_HAVE_INSTALLED:
                    holder.install.setText(R.string.have_installed);
                    holder.icon.setBackgroundResource(R.drawable.shape_button_gray);
                    holder.install.setTextColor(mContext.getResources().getColor(
                            R.color.button_gray));
                    break;
                case Constants.INSTALL_BUTTON_CONTINUE:
                    holder.install.setText(R.string.continues);
                    holder.install.setBackgroundResource(R.drawable.shape_button_white_cyan);
                    holder.install.setTextColor(mContext.getResources().getColor(
                            R.color.button_cyan));
                    break;
                case Constants.INSTALL_BUTTON_PAUSE:
                    holder.install.setText(R.string.pause);
                    holder.install.setBackgroundResource(R.drawable.shape_button_white_cyan);
                    holder.install.setTextColor(mContext.getResources().getColor(
                            R.color.button_cyan));
                    break;
                default:
                    break;
            }

            holder.install.setOnClickListener(this);
            holder.icon.setOnClickListener(this);
            holder.name.setOnClickListener(this);
            holder.type.setOnClickListener(this);
            holder.install.setTag(position);
            holder.icon.setTag(position);
            holder.name.setTag(position);
            holder.type.setTag(position);
        }

        return convertView;
    }

    @Override
    public void onClick(View v) {
        int tag = (int) v.getTag();
        switch (v.getId()) {
            case R.id.app_layout_gridview_install:
                Button install = (Button) v;
                String btnStr = install.getText().toString();

                if (btnStr.equals(mContext.getResources().getString(R.string.continues))) {
                    install.setText(mContext.getResources().getString(R.string.pause));
                } else if (btnStr.equals(mContext.getResources().getString(R.string.pause))) {
                    install.setText(mContext.getResources().getString(R.string.continues));
                } else if (btnStr.equals(mContext.getResources().getString(R.string.install))) {
                    install.setText(mContext.getResources().getString(R.string.continues));
                } else {

                }

                break;

            default:
                Toast.makeText(mContext, tag + "", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(Constants.FROM_FRAGMENT, mFromFragment);
                Bundle bundle = new Bundle();
                //TODO
                mContext.startActivity(intent);
                break;
        }
    }

    class ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView type;
        private Button install;

        public ViewHolder(View view) {
            icon = (ImageView) view.findViewById(R.id.app_layout_gridview_icon);
            name = (TextView) view.findViewById(R.id.app_layout_gridview_name);
            type = (TextView) view.findViewById(R.id.app_layout_gridview_type);
            install = (Button) view.findViewById(R.id.app_layout_gridview_install);
        }
    }

    public void addDatas(List<AppLayoutGridviewInfo> datas) {
        mDatas.clear();
        if (mIsAll) {
            mDatas.addAll(datas);
        } else {
            int len = datas == null ? 0 : (datas.size() > Constants.APP_NUM_FALSE
                                               ? Constants.APP_NUM_FALSE : datas.size());
            for (int i = 0; i < len; i++) {
                mDatas.add(datas.get(i));
            }
        }
        notifyDataSetChanged();
    }
}
