package com.openthos.appstore.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.SQLAppInstallInfo;
import com.openthos.appstore.bean.ManagerInfo;
import com.openthos.appstore.utils.FileHelper;
import com.openthos.appstore.utils.Tools;
import com.openthos.appstore.utils.AppUtils;
import com.openthos.appstore.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.provider.Settings;
import android.content.Intent;

/**
 * Created by luojunhuan on 16-11-1.
 */
public class ManagerUpdateAdapter extends BasicAdapter
        implements View.OnClickListener {

    public ManagerUpdateAdapter(Context context, boolean isAll) {
        super(context, isAll);
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
            SQLAppInstallInfo sqlAppInstallInfo = (SQLAppInstallInfo) mDatas.get(position);
            holder.appIcon.setImageDrawable(sqlAppInstallInfo.getIcon());
            holder.appName.setText(sqlAppInstallInfo.getName());
            holder.appVersion.setText(sqlAppInstallInfo.getVersionName());
            holder.appContent.setText(getType(sqlAppInstallInfo.getComment()));
            switch (sqlAppInstallInfo.getState()) {
                case Constants.APP_NEED_UPDATE:
                    holder.appTask.setText(mContext.getString(R.string.update));
                    break;
                case Constants.APP_DOWNLOAD_FINISHED:
                    holder.appTask.setText(mContext.getString(R.string.finished));
                    break;
                default:
                    holder.appTask.setText(mContext.getString(R.string.not_need_update));
                    break;
            }
            holder.appTask.setOnClickListener(this);
            holder.appTask.setTag(position);
            holder.layout.setOnClickListener(this);
            holder.layout.setTag(position);
        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        final int position = (int) v.getTag();
        SQLAppInstallInfo sqlAppInstallInfo = (SQLAppInstallInfo) mDatas.get(position);
        switch (v.getId()) {
            case R.id.item_manager_apptask:
                Button btn = (Button) v;
                String noUpdate = mContext.getString(R.string.not_need_update);
                String update = mContext.getString(R.string.update);
                String finished = mContext.getString(R.string.finished);
                String downloading = mContext.getString(R.string.continues);
                if (update.equals(btn.getText())) {
                    btn.setText(downloading);
                    MainActivity.mBinder.addTask(sqlAppInstallInfo.getId() + "",
                            sqlAppInstallInfo.getDownloadUrl(),
                            FileHelper.getNameFromUrl(sqlAppInstallInfo.getDownloadUrl()),
                            sqlAppInstallInfo.getPackageName());
                } else if (finished.equals(btn.getText())) {
                    AppUtils.installApk(mContext,
                            FileHelper.getDefaultFileFromUrl(sqlAppInstallInfo.getDownloadUrl()));
                } else {
                    Tools.toast(mContext, noUpdate);
                }
                break;
            default:
                String pkgName = (sqlAppInstallInfo).getPackageName();
                Uri uri = Uri.parse("package:" + pkgName);
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
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

    public void addDatas(List<SQLAppInstallInfo> datas) {
        if (datas != null) {
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
        }
        notifyDataSetChanged();
    }
}
