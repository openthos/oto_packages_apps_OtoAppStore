package com.openthos.appstore.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppInstallInfo;
import com.openthos.appstore.bean.AppItemInfo;
import com.openthos.appstore.download.DownloadListener;
import com.openthos.appstore.download.DownloadManager;
import com.openthos.appstore.utils.AppUtils;
import com.openthos.appstore.utils.FileHelper;
import com.openthos.appstore.utils.ImageCache;
import com.openthos.appstore.utils.NetUtils;
import com.openthos.appstore.utils.Tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ManagerDownloadAdapter extends BasicAdapter implements View.OnClickListener {
    private DownloadManager mDownloadManager;
    private List<AppInstallInfo> mAppInstallInfos;

    public ManagerDownloadAdapter(Context context, DownloadManager downloadManager,
                                  List<AppInstallInfo> appInstallInfos, List<AppItemInfo> datas) {
        super(context);
        mDatas = datas;
        mAppInstallInfos = appInstallInfos;
        mDownloadManager = downloadManager;
        mDownloadManager.setAllTaskListener(new DownloadManagerListener());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.item_download, null);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        if (mDatas.size() != 0) {
            AppItemInfo appInfo = (AppItemInfo) mDatas.get(position);
            ImageCache.loadImage(holder.appIcon, appInfo.getIconUrl());
            holder.appName.setText(appInfo.getFileName());
            holder.fileProgress.setProgress(appInfo.getProgress());
            switch (appInfo.getDownloadState()) {
                case Constants.APP_DOWNLOAD_PAUSE:
                    holder.fileProgress.setVisibility(View.VISIBLE);
                    holder.install.setText(R.string.continues);
                    holder.downloadState.setText(appInfo.getProgress() + "%");
                    break;
                case Constants.APP_DOWNLOAD_CONTINUE:
                    holder.fileProgress.setVisibility(View.VISIBLE);
                    holder.install.setText(R.string.pause);
                    holder.downloadState.setText(appInfo.getProgress() + "%     " +
                            Tools.transformFileSize(appInfo.getSpeed() * 1024) + "/s");
                    break;
                case Constants.APP_DOWNLOAD_FINISHED:
                    holder.install.setText(R.string.install);
                    holder.downloadState.setText(R.string.finished);
                    holder.fileProgress.setVisibility(View.GONE);
                    break;
                case Constants.APP_HAVE_INSTALLED:
                    holder.install.setText(R.string.open);
                    holder.downloadState.setText(R.string.finished);
                    holder.fileProgress.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
            holder.install.setOnClickListener(this);
            holder.remove.setOnClickListener(this);
            holder.install.setTag(position);
            holder.remove.setTag(position);
        }
        return convertView;
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        AppItemInfo appInfo = (AppItemInfo) mDatas.get(position);
        switch (view.getId()) {
            case R.id.item_download_install:
                installClick(appInfo);
                break;
            case R.id.item_download_remove:
                MainActivity.mDownloadService.stopTask(appInfo.getTaskId());
                mDownloadManager.deleteTask(appInfo.getTaskId());
                mDatas.remove(appInfo);
                break;
            default:
                break;
        }
        MainActivity.mHandler.sendEmptyMessage(Constants.REFRESH);
    }

    private void installClick(AppItemInfo appItemInfo) {
        if (appItemInfo.getDownloadState() == Constants.APP_DOWNLOAD_FINISHED) {
            appItemInfo.setFilePath(FileHelper.
                getDownloadUrlFile(appItemInfo.getDownloadUrl()).getAbsolutePath());
            MainActivity.mHandler.sendMessage(MainActivity.mHandler.
                        obtainMessage(Constants.INSTALL_APK, appItemInfo));
        } else if (appItemInfo.getDownloadState() == Constants.APP_HAVE_INSTALLED) {
            AppUtils.openApp(mContext, appItemInfo.getPackageName());
        } else if (NetUtils.isConnected(mContext)) {
            switch (appItemInfo.getDownloadState()) {
                case Constants.APP_DOWNLOAD_PAUSE:
                    MainActivity.mDownloadService.startTask(appItemInfo.getTaskId());
                    appItemInfo.setOnDownloading(true);
                    break;
                case Constants.APP_DOWNLOAD_CONTINUE:
                    MainActivity.mDownloadService.stopTask(appItemInfo.getTaskId());
                    appItemInfo.setOnDownloading(false);
                    break;
            }
        } else {
            Tools.toast(mContext, mContext.getString(R.string.check_net_state));
        }
    }

    @Override
    public void refreshLayout() {
        ArrayList<AppItemInfo> allTask = mDownloadManager.getAllInfo();
        mDatas.clear();
        for (int i = 0; i < allTask.size(); i++) {
            AppItemInfo appInfo = allTask.get(i);
            if (appInfo.isOnDownloading()) {
                appInfo.setDownloadState(Constants.APP_DOWNLOAD_CONTINUE);
            } else if (appInfo.getDownFileSize() != 0
                    && appInfo.getFileSize() == appInfo.getDownFileSize()) {
                boolean isBreak = false;
                for (int j = 0; j < mAppInstallInfos.size(); j++) {
                    AppInstallInfo appInstallInfo = mAppInstallInfos.get(j);
                    if (appInfo.getPackageName().equals(appInstallInfo.getPackageName())) {
                        if (new File(appInfo.getFilePath()).exists()) {
                            if (getVersionCodeByApk(appInfo.getFilePath())
                                    > appInstallInfo.getVersionCode()) {
                                appInfo.setDownloadState(Constants.APP_DOWNLOAD_FINISHED);
                            } else {
                                appInfo.setDownloadState(Constants.APP_HAVE_INSTALLED);
                            }
                        } else {
                            appInfo.setDownloadState(Constants.APP_NOT_EXIST);
                        }
                        isBreak = true;
                        break;
                    }
                }
                if (!isBreak) {
                    appInfo.setDownloadState(Constants.APP_DOWNLOAD_FINISHED);
                }
            } else {
                appInfo.setDownloadState(Constants.APP_DOWNLOAD_PAUSE);
            }
            if (appInfo.getDownloadState() != Constants.APP_HAVE_INSTALLED) {
                mDatas.add(appInfo);
            }
        }
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
        private TextView appName;
        private TextView downloadState;
        private ProgressBar fileProgress;
        private Button install;
        private Button remove;
        private ImageView appIcon;

        public ViewHolder(View view) {
            appIcon = (ImageView) view.findViewById(R.id.item_download_appIcon);
            appName = (TextView) view.findViewById(R.id.item_download_appName);
            downloadState = (TextView) view.findViewById(R.id.item_download_downloadState);
            fileProgress = (ProgressBar) view.findViewById(R.id.item_download_progressBar);
            install = (Button) view.findViewById(R.id.item_download_install);
            remove = (Button) view.findViewById(R.id.item_download_remove);
        }
    }

    private class DownloadManagerListener implements DownloadListener {

        @Override
        public void onStart(AppItemInfo downloadInfo) {
            Tools.toast(mContext, mContext.getResources().getString(R.string.start_download));
            for (AppItemInfo info : (List<AppItemInfo>) mDatas) {
                if (downloadInfo.getTaskId().equals(info.getTaskId())) {
                    info.setOnDownloading(true);
                    MainActivity.mHandler.sendEmptyMessage(Constants.REFRESH);
                    break;
                }
            }
        }

        @Override
        public void onProgress(AppItemInfo downloadInfo, boolean isSupportFTP) {
            for (AppItemInfo info : (List<AppItemInfo>) mDatas) {
                if (downloadInfo.getTaskId().equals(info.getTaskId())) {
                    info.setDownFileSize(downloadInfo.getDownFileSize());
                    info.setFileSize(downloadInfo.getFileSize());
                    info.setSpeed(downloadInfo.getSpeed());
                    notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void onStop(AppItemInfo downloadInfo, boolean isSupportFTP) {
            for (AppItemInfo info : (List<AppItemInfo>) mDatas) {
                if (downloadInfo.getTaskId().equals(info.getTaskId())) {
                    info.setSpeed(0);
                    info.setOnDownloading(false);
                    MainActivity.mHandler.sendEmptyMessage(Constants.REFRESH);
                    break;
                }
            }
        }

        @Override
        public void onSuccess(AppItemInfo downloadInfo) {
            for (AppItemInfo info : (List<AppItemInfo>) mDatas) {
                if (downloadInfo.getTaskId().equals(info.getTaskId())) {
                    info.setSpeed(0);
                    info.setOnDownloading(false);
                    info.setDownFileSize(downloadInfo.getDownFileSize());
                    info.setFileSize(downloadInfo.getFileSize());
                    MainActivity.mHandler.sendEmptyMessage(Constants.REFRESH);
                    break;
                }
            }
        }

        @Override
        public void onError(AppItemInfo downloadInfo, String error) {
            for (AppItemInfo info : (List<AppItemInfo>) mDatas) {
                if (info.getTaskId().equals(downloadInfo.getTaskId())) {
                    info.setOnDownloading(false);
                    FileHelper.deleteFile(downloadInfo.getFilePath());
                    info.setDownFileSize(downloadInfo.getDownFileSize());
                    info.setFileSize(downloadInfo.getFileSize());
                    MainActivity.mHandler.sendEmptyMessage(Constants.REFRESH);
                    break;
                }
            }
            Tools.toast(mContext, error);
        }
    }
}
