package com.openthos.appstore.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
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
import com.openthos.appstore.download.DownloadListener;
import com.openthos.appstore.download.DownloadManager;
import com.openthos.appstore.utils.FileHelper;
import com.openthos.appstore.utils.ImageCache;
import com.openthos.appstore.utils.NetUtils;
import com.openthos.appstore.utils.SPUtils;
import com.openthos.appstore.utils.SQLOperator;
import com.openthos.appstore.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ManagerUpdateAdapter extends BasicAdapter implements View.OnClickListener {
    private DownloadManager mDownloadManager;
    private List<AppInstallInfo> mAppInstallInfos;

    public ManagerUpdateAdapter(Context context, DownloadManager downloadManager,
                                List<AppInstallInfo> appInstallInfos, List<AppItemInfo> datas) {
        super(context);
        mDatas = datas;
        mAppInstallInfos = appInstallInfos;
        mDownloadManager = downloadManager;
        mDownloadManager.setAllTaskListener(new UpdateManagerListener());
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
            AppItemInfo appItemInfo = (AppItemInfo) mDatas.get(position);
            ImageCache.loadImage(holder.appIcon, appItemInfo.getIconUrl());
            holder.appName.setText(appItemInfo.getAppName());
            holder.category.setText("");
            holder.appVersion.setText(
                    appItemInfo.getVersionName() + "(" + appItemInfo.getVersionCode() + ")");
            holder.appContent.setText(appItemInfo.getPackageName());
            holder.update.setOnClickListener(this);
            holder.update.setTag(appItemInfo);
            switch (appItemInfo.getState()) {
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
        mDatas.clear();
        AppItemInfo appItemInfo = null;
        for (int i = 0; i < mAppInstallInfos.size(); i++) {
            AppInstallInfo appInstallInfo = mAppInstallInfos.get(i);
            List<String> searchData = SPUtils.getSearchData(mContext, appInstallInfo.getName());
            if (searchData.size() < 1) {
                continue;
            } else {
                for (int j = 0; j < searchData.size(); j++) {
                    try {
                        appItemInfo = Tools.getAppItemInfo(new JSONObject(searchData.get(j)),
                                mMainActivity.mAllAppItemInfos);
                        if (appItemInfo != null && appItemInfo.getPackageName()
                                .equals(appInstallInfo.getPackageName())) {
                            if (appInstallInfo.getVersionCode() < appItemInfo.getVersionCode()) {
                                appItemInfo.setState(Constants.APP_NEED_UPDATE);
                            }
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (appItemInfo != null && appItemInfo.getState() != Constants.APP_NEED_UPDATE) {
                    continue;
                }
            }

            AppItemInfo downloadInfo = new SQLOperator(mContext).
                    getDownloadInfoByPkgName(appItemInfo.getPackageName());
            if (downloadInfo != null) {
                long downloadSize = downloadInfo.getDownFileSize();
                long fileSize = downloadInfo.getFileSize();
                if (downloadSize < fileSize) {
                    appItemInfo.setState(Constants.APP_DOWNLOAD_PAUSE);
                } else if (fileSize != 0 && downloadSize == fileSize
                        && getVersionCodeByApk(
                                downloadInfo.getFilePath()) > appInstallInfo.getVersionCode()) {
                    appItemInfo.setState(Constants.APP_DOWNLOAD_FINISHED);
                }
            }

            ArrayList<AppItemInfo> allTask = mDownloadManager.getAllInfo();
            for (int j = 0; j < allTask.size(); j++) {
                AppItemInfo appInfo = allTask.get(j);
                if (appItemInfo.getTaskId().equals(appInfo.getTaskId())) {
                    if (appInfo.isOnDownloading()) {
                        appItemInfo.setState(Constants.APP_DOWNLOAD_CONTINUE);
                    }
                }
            }

            if (appItemInfo != null) {
                mDatas.add(appItemInfo);
            }
        }
    }

    @Override
    public void onClick(View view) {
        AppItemInfo appItemInfo = (AppItemInfo) view.getTag();
        Button btn = (Button) view;
        if (appItemInfo.getState() == Constants.APP_DOWNLOAD_FINISHED) {
            appItemInfo.setFilePath(FileHelper.
                getDownloadUrlFile(appItemInfo.getDownloadUrl()).getAbsolutePath());
            MainActivity.mHandler.sendMessage(
                    MainActivity.mHandler.obtainMessage(Constants.INSTALL_APK, appItemInfo));
        } else if (NetUtils.isConnected(mContext)) {
            switch (appItemInfo.getState()) {
                case Constants.APP_NEED_UPDATE:
                    btn.setText(mContext.getString(R.string.updating));
                    MainActivity.mDownloadService.addTask(appItemInfo.getTaskId(),
                            StoreApplication.mBaseUrl + "/" + appItemInfo.getDownloadUrl(),
                            appItemInfo.getAppName(),
                            appItemInfo.getPackageName(),
                            appItemInfo.getIconUrl());
                    break;
                case Constants.APP_DOWNLOAD_PAUSE:
                    btn.setText(mContext.getString(R.string.updating));
                    MainActivity.mDownloadService.startTask(appItemInfo.getTaskId());
                    break;
                case Constants.APP_DOWNLOAD_CONTINUE:
                    btn.setText(mContext.getString(R.string.pause));
                    MainActivity.mDownloadService.stopTask(appItemInfo.getTaskId());
                    break;
            }
        } else {
            Tools.toast(mContext, mContext.getString(R.string.check_net_state));
        }
        MainActivity.mHandler.sendEmptyMessage(Constants.REFRESH);
    }

    @Override
    public void refreshLayout() {
        initState();
        notifyDataSetChanged();
    }

    private int getVersionCodeByApk(String apkPath) {
        if (new File(apkPath).exists()) {
            return mContext.getPackageManager().getPackageArchiveInfo(
                    apkPath, PackageManager.GET_ACTIVITIES).versionCode;
        } else {
            return 0;
        }
    }

    private class ViewHolder {
        private ImageView appIcon;
        private TextView appName;
        private TextView appVersion;
        private TextView appContent;
        private TextView category;
        private Button update;

        public ViewHolder(View view) {
            appIcon = ((ImageView) view.findViewById(R.id.item_update_appIcon));
            appName = (TextView) view.findViewById(R.id.item_update_appName);
            category = (TextView) view.findViewById(R.id.item_update_category);
            appVersion = (TextView) view.findViewById(R.id.item_update_version);
            appContent = (TextView) view.findViewById(R.id.item_update_newFeature);
            update = ((Button) view.findViewById(R.id.item_update_update));
            update.setVisibility(View.VISIBLE);
        }
    }

    private class UpdateManagerListener implements DownloadListener {
        @Override
        public void onStart(AppItemInfo downloadInfo) {
            for (int i = 0; i < mDatas.size(); i++) {
                AppItemInfo updateInfo = (AppItemInfo) mDatas.get(i);
                if (updateInfo.getPackageName().equals(downloadInfo.getPackageName())) {
                    updateInfo.setState(Constants.APP_DOWNLOAD_CONTINUE);
                    notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void onProgress(AppItemInfo downloadInfo, boolean isSupportF) {

        }

        @Override
        public void onStop(AppItemInfo downloadInfo, boolean isSupportFTP) {
            for (int i = 0; i < mDatas.size(); i++) {
                AppItemInfo updateInfo = (AppItemInfo) mDatas.get(i);
                if (updateInfo.getPackageName().equals(downloadInfo.getPackageName())) {
                    updateInfo.setState(Constants.APP_DOWNLOAD_PAUSE);
                    notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void onError(AppItemInfo downloadInfo, String error) {
            for (int i = 0; i < mDatas.size(); i++) {
                AppItemInfo updateInfo = (AppItemInfo) mDatas.get(i);
                if (updateInfo.getPackageName().equals(downloadInfo.getPackageName())) {
                    updateInfo.setState(Constants.APP_DOWNLOAD_PAUSE);
                    notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void onSuccess(AppItemInfo downloadInfo) {
            for (int i = 0; i < mDatas.size(); i++) {
                AppItemInfo updateInfo = (AppItemInfo) mDatas.get(i);
                if (updateInfo.getPackageName().equals(downloadInfo.getPackageName())) {
                    updateInfo.setState(Constants.APP_DOWNLOAD_FINISHED);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }
}
