package com.openthos.appstore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.app.StoreApplication;
import com.openthos.appstore.bean.AppInstallInfo;
import com.openthos.appstore.bean.AppItemInfo;
import com.openthos.appstore.bean.DownloadInfo;
import com.openthos.appstore.bean.TaskInfo;
import com.openthos.appstore.download.DownloadManager;
import com.openthos.appstore.download.DownloadService;
import com.openthos.appstore.utils.AppUtils;
import com.openthos.appstore.utils.FileHelper;
import com.openthos.appstore.utils.NetUtils;
import com.openthos.appstore.utils.SPUtils;
import com.openthos.appstore.utils.SQLOperator;
import com.openthos.appstore.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManagerUpdateAdapter extends BasicAdapter implements View.OnClickListener {
    private DownloadManager mManager;
    private SQLOperator mSQLOperator;

    public ManagerUpdateAdapter(Context context, List<AppInstallInfo> datas) {
        super(context);
        mSQLOperator = new SQLOperator(mContext);
        mManager = DownloadService.getDownloadManager();
        mDatas = datas;
        initState();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.item_update, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        if (mDatas != null && mDatas.size() != 0) {
            AppInstallInfo appInstallInfo = (AppInstallInfo) mDatas.get(position);
            holder.appIcon.setImageDrawable(appInstallInfo.getIcon());
            holder.appName.setText(appInstallInfo.getName());
            holder.category.setText("");
            holder.appVersion.setText(appInstallInfo.getVersionName());
            holder.appContent.setText(appInstallInfo.getPackageName());
            holder.uninstall.setOnClickListener(this);
            holder.open.setOnClickListener(this);
            holder.update.setOnClickListener(this);
            holder.update.setTag(appInstallInfo);
            holder.uninstall.setTag(appInstallInfo);
            holder.open.setTag(appInstallInfo);
            switch (appInstallInfo.getState()) {
                case Constants.APP_NOT_EXIST:
                case Constants.APP_HAVE_INSTALLED:
                    holder.update.setVisibility(View.GONE);
                    break;
                case Constants.APP_NEED_UPDATE:
                    holder.update.setVisibility(View.VISIBLE);
                    holder.update.setText(mContext.getString(R.string.update));
                    break;
                case Constants.APP_DOWNLOAD_FINISHED:
                    holder.update.setVisibility(View.VISIBLE);
                    holder.update.setText(mContext.getString(R.string.install));
                    break;
                case Constants.APP_DOWNLOAD_CONTINUE:
                    holder.update.setVisibility(View.VISIBLE);
                    holder.update.setText(mContext.getString(R.string.updating));
                    break;
                case Constants.APP_DOWNLOAD_PAUSE:
                    holder.update.setVisibility(View.VISIBLE);
                    holder.update.setText(mContext.getString(R.string.pause));
                    break;
            }
        }
        return convertView;
    }

    public void initState() {
        for (int i = 0; i < mDatas.size(); i++) {
            AppInstallInfo appInstallInfo = ((List<AppInstallInfo>) mDatas).get(i);
            List<String> searchData = SPUtils.getSearchData(mContext, appInstallInfo.getName());
            if (searchData.size() < 1) {
                appInstallInfo.setState(Constants.APP_NOT_EXIST);
                continue;
            } else {
                for (int j = 0; j < searchData.size(); j++) {
                    try {
                        AppItemInfo appItemInfo = new AppItemInfo(new JSONObject(searchData.get(j)));
                        if (appItemInfo.getPackageName().equals(appInstallInfo.getPackageName())) {
                            appInstallInfo.setDownloadUrl(appItemInfo.getDownloadUrl());
                            appInstallInfo.setIconUrl(appItemInfo.getIconUrl());
                            appInstallInfo.setTaskId(appItemInfo.getTaskId());
                            if (appInstallInfo.getVersionCode() < appItemInfo.getVersionCode()) {
                                appInstallInfo.setState(Constants.APP_NEED_UPDATE);
                            } else {
                                appInstallInfo.setState(Constants.APP_HAVE_INSTALLED);
                            }
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (appInstallInfo.getState() != Constants.APP_HAVE_INSTALLED
                        && appInstallInfo.getState() != Constants.APP_HAVE_INSTALLED) {
                    appInstallInfo.setState(Constants.APP_NOT_EXIST);
                    continue;
                }
            }

            DownloadInfo downloadInfo = new SQLOperator(mContext).
                    getDownloadInfoByPkgName(appInstallInfo.getPackageName());
            if (downloadInfo != null) {
                long downloadSize = downloadInfo.getDownloadSize();
                long fileSize = downloadInfo.getFileSize();
                if (downloadSize < fileSize) {
                    appInstallInfo.setState(Constants.APP_DOWNLOAD_PAUSE);
                } else if (fileSize != 0 && downloadSize == fileSize) {
                    switch (appInstallInfo.getState()) {
                        case Constants.APP_HAVE_INSTALLED:
                            break;
                        default:
                            appInstallInfo.setState(Constants.APP_DOWNLOAD_FINISHED);
                            break;
                    }
                }
            }

            ArrayList<TaskInfo> allTask = mManager.getAllTask();
            for (int j = 0; j < allTask.size(); j++) {
                TaskInfo taskInfo = allTask.get(j);
                if (appInstallInfo.getTaskId().equals(taskInfo.getTaskID())) {
                    if (taskInfo.isOnDownloading()) {
                        appInstallInfo.setState(Constants.APP_DOWNLOAD_CONTINUE);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        AppInstallInfo appInstallInfo = (AppInstallInfo) view.getTag();
        switch (view.getId()) {
            case R.id.item_update_uninstall:
                AppUtils.uninstallApk(mContext, appInstallInfo.getPackageName());
                break;
            case R.id.item_update_open:
                AppUtils.openApp(mContext, appInstallInfo.getPackageName());
                break;
            case R.id.item_update_update:
                updateOperate((Button) view, appInstallInfo);
                MainActivity.mHandler.sendEmptyMessage(Constants.REFRESH);
                break;
            default:
                break;
        }
    }

    private void updateOperate(Button btn, AppInstallInfo appInstallInfo) {
        if (appInstallInfo.getState() == Constants.APP_DOWNLOAD_FINISHED) {
            MainActivity.mHandler.sendMessage(
                    MainActivity.mHandler.obtainMessage(
                            Constants.INSTALL_APK,
                            FileHelper.getDownloadUrlPath(appInstallInfo.getDownloadUrl())));
        } else if (NetUtils.isConnected(mContext)) {
            switch (appInstallInfo.getState()) {
                case Constants.APP_NEED_UPDATE:
                    btn.setText(mContext.getString(R.string.updating));
                    MainActivity.mDownloadService.addTask(appInstallInfo.getTaskId(),
                            StoreApplication.mBaseUrl + "/" + appInstallInfo.getDownloadUrl(),
                            appInstallInfo.getName(),
                            appInstallInfo.getPackageName(),
                            appInstallInfo.getIconUrl());
                    break;
                case Constants.APP_DOWNLOAD_PAUSE:
                    btn.setText(mContext.getString(R.string.updating));
                    MainActivity.mDownloadService.startTask(appInstallInfo.getTaskId());
                    break;
                case Constants.APP_DOWNLOAD_CONTINUE:
                    btn.setText(mContext.getString(R.string.pause));
                    MainActivity.mDownloadService.stopTask(appInstallInfo.getTaskId());
                    break;
            }
        } else {
            Tools.toast(mContext, mContext.getString(R.string.check_net_state));
        }
    }

    @Override
    public void refreshLayout() {
        initState();
        notifyDataSetChanged();
    }

    private class ViewHolder {
        private ImageView appIcon;
        private TextView appName;
        private TextView appVersion;
        private TextView appContent;
        private TextView category;
        private Button update;
        private Button open;
        private Button uninstall;

        public ViewHolder(View view) {
            appIcon = ((ImageView) view.findViewById(R.id.item_update_appIcon));
            appName = (TextView) view.findViewById(R.id.item_update_appName);
            category = (TextView) view.findViewById(R.id.item_update_category);
            appVersion = (TextView) view.findViewById(R.id.item_update_version);
            appContent = (TextView) view.findViewById(R.id.item_update_newFeature);
            update = ((Button) view.findViewById(R.id.item_update_update));
            open = ((Button) view.findViewById(R.id.item_update_open));
            uninstall = ((Button) view.findViewById(R.id.item_update_uninstall));
        }
    }
}
