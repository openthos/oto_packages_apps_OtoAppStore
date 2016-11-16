package com.openthos.appstore.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.SQLAppInstallInfo;
import com.openthos.appstore.bean.ManagerInfo;
import com.openthos.appstore.utils.Tools;
import com.openthos.appstore.utils.AppUtils;
import com.openthos.appstore.utils.DialogUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luojunhuan on 16-11-1.
 */
public class ManagerUpdateAdapter extends BasicAdapter
        implements View.OnClickListener {

    public ManagerUpdateAdapter(Context context, boolean isAll, int fragment) {
        super(context, isAll, fragment);
    }

    public void setAppInfo(List<SQLAppInstallInfo> datas) {
        if (mDatas == null){
            mDatas = new ArrayList<SQLAppInstallInfo>();
        }
        mDatas.clear();
        if (mIsAll) {
            mDatas.addAll(datas);
        } else {
            int len = datas == null ? 0 : (datas.size() > Constants.MANAGER_NUM_FALSE ?
                    Constants.MANAGER_NUM_FALSE : datas.size());
            for (int i = 0; i < len; i++) {
                mDatas.add(datas.get(i));
            }
        }
        notifyDataSetChanged();
    }

    public void setAll(boolean all) {
        mIsAll = all;
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public long getItemId(int position) {
        return mDatas == null ? -1 : ((SQLAppInstallInfo) mDatas.get(position)).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.item_manager, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        if (mDatas != null && mDatas.size() != 0) {
            SQLAppInstallInfo SQLAppInstallInfo = (SQLAppInstallInfo) mDatas.get(position);
            holder.appIcon.setImageDrawable(SQLAppInstallInfo.getIcon());
            holder.appName.setText(SQLAppInstallInfo.getName());
            holder.appVersion.setText(SQLAppInstallInfo.getVersionCode() + "");
            holder.appContent.setText(SQLAppInstallInfo.getPackageName());
            Tools.printLog("IMA", SQLAppInstallInfo.toString());
            holder.appTask.setOnClickListener(this);
            holder.appTask.setTag(position);
            holder.layout.setOnClickListener(this);
            holder.layout.setTag(position);
        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        final int position = (int)v.getTag();
        switch(v.getId()){
            case R.id.item_manager_apptask:
                Tools.toast(mContext,"gengxin");
                break;
            default:
                new DialogUtils().dialogUpdate(mContext, new DialogUtils.UpdateManager(){
                    @Override
                    public void uninstall(AlertDialog dialog){
                        AppUtils.uninstallApk(mContext,
                                 ((SQLAppInstallInfo) mDatas.get(position)).getPackageName());
                        dialog.cancel();
                    }
                });
                break;
        }
    }

    class ViewHolder {
        private ImageView appIcon;
        private TextView appName;
        private TextView appVersion;
        private TextView appContent;
        private Button appTask;
        private LinearLayout layout;

        public ViewHolder(View view) {
            appIcon = ((ImageView) view.findViewById(R.id.item_manager_appIcon));
            appName = (TextView) view.findViewById(R.id.item_manager_appName);
            appVersion = (TextView) view.findViewById(R.id.item_manager_appVersion);
            appContent = (TextView) view.findViewById(R.id.item_manager_appcontent);
            appTask = ((Button) view.findViewById(R.id.item_manager_apptask));
            layout = ((LinearLayout) view.findViewById(R.id.item_manager_layout));
        }
    }

    public void addDatas(List<ManagerInfo> datas) {
        mDatas.clear();
        if (mIsAll) {
            mDatas.addAll(datas);
        } else {
            int len = datas == null ? 0 : (datas.size() > Constants.MANAGER_NUM_FALSE ?
                    Constants.MANAGER_NUM_FALSE : datas.size());
            for (int i = 0; i < len; i++) {
                mDatas.add(datas.get(i));
            }
        }
        notifyDataSetChanged();
    }
}
