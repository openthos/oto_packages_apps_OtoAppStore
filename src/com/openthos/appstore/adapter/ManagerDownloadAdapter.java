package com.openthos.appstore.adapter;

import android.content.Context;
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
import com.openthos.appstore.bean.DownloadInfo;
import com.openthos.appstore.bean.TaskInfo;
import com.openthos.appstore.download.DownloadListener;
import com.openthos.appstore.download.DownloadManager;
import com.openthos.appstore.utils.AppUtils;
import com.openthos.appstore.utils.FileHelper;
import com.openthos.appstore.utils.ImageCache;
import com.openthos.appstore.utils.Tools;

import java.util.List;

public class ManagerDownloadAdapter extends BasicAdapter implements View.OnClickListener {
    private DownloadManager mDownloadManager;

    public ManagerDownloadAdapter(Context context, DownloadManager downloadManager) {
        super(context);
        if (downloadManager != null) {
            mDownloadManager = downloadManager;
            mDownloadManager.setAllTaskListener(new DownloadManagerListener());
        }
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
            TaskInfo taskInfo = (TaskInfo) mDatas.get(position);
            ImageCache.loadImage(holder.appIcon, taskInfo.getIconUrl());
            holder.appName.setText(taskInfo.getFileName());
            holder.fileProgress.setProgress(taskInfo.getProgress());
            switch (taskInfo.getDownloadState()) {
                case Constants.APP_HAVE_INSTALLED:
                    holder.install.setText(R.string.open);
                    holder.downloadState.setText(R.string.finished);
                    break;
                case Constants.APP_DOWNLOAD_PAUSE:
                    holder.install.setText(R.string.continues);
                    holder.downloadState.setText(taskInfo.getProgress() + "%     " +
                            Tools.transformFileSize(taskInfo.getSpeed() * 1024) + "/s");
                    break;
                case Constants.APP_DOWNLOAD_CONTINUE:
                    holder.install.setText(R.string.pause);
                    holder.downloadState.setText(taskInfo.getProgress() + "%     " +
                            Tools.transformFileSize(taskInfo.getSpeed() * 1024) + "/s");
                    break;
                case Constants.APP_DOWNLOAD_FINISHED:
                    holder.install.setText(R.string.install);
                    holder.downloadState.setText(R.string.finished);
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

    public void addData(List<TaskInfo> listdata, boolean isAll) {
        mDatas.clear();
        if (isAll) {
            mDatas = listdata;
        } else {
            int len = listdata == null ? null : (listdata.size() > Constants.MANAGER_NUM_FALSE ?
                    Constants.MANAGER_NUM_FALSE : listdata.size());
            for (int i = 0; i < len; i++) {
                mDatas.add(listdata.get(i));
            }
        }
        notifyDataSetInvalidated();
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        TaskInfo taskInfo = (TaskInfo) mDatas.get(position);
        switch (view.getId()) {
            case R.id.item_download_install:
                String s = ((Button) view).getText().toString();
                if (s.equals(mContext.getResources().getString(R.string.open))) {
                    AppUtils.startApk(mContext, taskInfo.getPackageName());
                } else if (s.equals(mContext.getResources().getString(R.string.continues))) {
                    MainActivity.mDownloadService.startTask(taskInfo.getTaskID());
                    taskInfo.setOnDownloading(true);
                } else if (s.equals(mContext.getResources().getString(R.string.pause))) {
                    MainActivity.mDownloadService.stopTask(taskInfo.getTaskID());
                    taskInfo.setOnDownloading(false);
                } else if (s.equals(mContext.getResources().getString(R.string.install))) {
                    MainActivity.mHandler.sendMessage(MainActivity.mHandler.
                            obtainMessage(Constants.INSTALL_APK, taskInfo.getFilePath()));
                }
                break;
            case R.id.item_download_remove:
                MainActivity.mDownloadService.stopTask(taskInfo.getTaskID());
                mDownloadManager.deleteTask(taskInfo.getTaskID());
                mDatas.remove(taskInfo.getTaskID());
                break;
            default:
                break;
        }
        MainActivity.mHandler.sendEmptyMessage(Constants.REFRESH);
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
        public void onStart(DownloadInfo downloadInfo) {
            Tools.toast(mContext, mContext.getResources().getString(R.string.start_download));

        }

        @Override
        public void onProgress(DownloadInfo downloadInfo, boolean isSupportFTP) {
            for (TaskInfo info : (List<TaskInfo>) mDatas) {
                if (downloadInfo.getTaskID().equals(info.getTaskID())) {
                    info.setDownFileSize(downloadInfo.getDownloadSize());
                    info.setFileSize(downloadInfo.getFileSize());
                    info.setSpeed(downloadInfo.getSpeed());
                    notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void onStop(DownloadInfo downloadInfo, boolean isSupportFTP) {
            for (TaskInfo info : (List<TaskInfo>) mDatas) {
                if (downloadInfo.getTaskID().equals(info.getTaskID())) {
                    info.setSpeed(0);
                    info.setOnDownloading(false);
                    notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void onSuccess(DownloadInfo downloadInfo) {
            for (TaskInfo info : (List<TaskInfo>) mDatas) {
                if (downloadInfo.getTaskID().equals(info.getTaskID())) {
                    info.setSpeed(0);
                    info.setOnDownloading(false);
                    info.setDownFileSize(downloadInfo.getDownloadSize());
                    info.setFileSize(downloadInfo.getFileSize());
                    MainActivity.mHandler.sendEmptyMessage(Constants.REFRESH);
                    break;
                }
            }
        }

        @Override
        public void onError(DownloadInfo downloadInfo, String error) {
            for (TaskInfo info : (List<TaskInfo>) mDatas) {
                if (info.getTaskID().equals(downloadInfo.getTaskID())) {
                    info.setOnDownloading(false);
                    FileHelper.deleteFile(downloadInfo.getFilePath());
                    info.setDownFileSize(downloadInfo.getDownloadSize());
                    info.setFileSize(downloadInfo.getFileSize());
                    MainActivity.mHandler.sendEmptyMessage(Constants.REFRESH);
                    break;
                }
            }
            Tools.toast(mContext, error);
        }
    }
}
