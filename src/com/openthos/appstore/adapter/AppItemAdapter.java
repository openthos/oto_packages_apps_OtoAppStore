package com.openthos.appstore.adapter;

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
import com.openthos.appstore.app.StoreApplication;
import com.openthos.appstore.bean.AppInstallInfo;
import com.openthos.appstore.bean.AppItemInfo;
import com.openthos.appstore.bean.DownloadInfo;
import com.openthos.appstore.bean.TaskInfo;
import com.openthos.appstore.download.DownloadListener;
import com.openthos.appstore.download.DownloadManager;
import com.openthos.appstore.download.DownloadService;
import com.openthos.appstore.utils.AppUtils;
import com.openthos.appstore.utils.FileHelper;
import com.openthos.appstore.utils.ImageCache;
import com.openthos.appstore.utils.NetUtils;
import com.openthos.appstore.utils.SQLOperator;
import com.openthos.appstore.utils.Tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppItemAdapter extends BasicAdapter implements View.OnClickListener {
    private HashMap<String, AppInstallInfo> mAppInstallMap;
    private DownloadManager mManager;

    public AppItemAdapter(Context context,
                          HashMap<String, AppInstallInfo> appInstallMap, List<AppItemInfo> datas) {
        super(context);
        mDatas = datas;
        mAppInstallMap = appInstallMap;
        mManager = DownloadService.getDownloadManager();
        mManager.setAllTaskListener(new ItemDownloadListener());
    }

    @Override
    public long getItemId(int position) {
        return Tools.transformLong(((AppItemInfo) mDatas.get(position)).getTaskId());
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.app_item, viewGroup, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();

        if (mDatas != null && mDatas.size() != 0) {
            AppItemInfo appItemInfo = (AppItemInfo) mDatas.get(position);
            ImageCache.loadImage(holder.icon, appItemInfo.getIconUrl());
            holder.appName.setText(appItemInfo.getAppName());
            holder.type.setText(appItemInfo.getType());
            holder.starNum.setText(String.valueOf(appItemInfo.getStar()));
            holder.layout.setOnClickListener(this);
            holder.install.setOnClickListener(this);
            holder.layout.setTag(appItemInfo);
            holder.install.setTag(appItemInfo);
            switch (appItemInfo.getState()) {
                case Constants.APP_NOT_INSTALL:
                    holder.install.setText(mContext.getString(R.string.download));
                    break;
                case Constants.APP_HAVE_INSTALLED:
                    holder.install.setText(mContext.getString(R.string.open));
                    break;
                case Constants.APP_DOWNLOAD_CONTINUE:
                    holder.install.setText(mContext.getString(R.string.downloading));
                    break;
                case Constants.APP_DOWNLOAD_PAUSE:
                    holder.install.setText(mContext.getString(R.string.pause));
                    break;
                case Constants.APP_NEED_UPDATE:
                    holder.install.setText(mContext.getString(R.string.update));
                    break;
                case Constants.APP_DOWNLOAD_FINISHED:
                    holder.install.setText(mContext.getString(R.string.install));
                    break;
                default:
                    break;
            }
        }
        return convertView;
    }

    @Override
    public void refreshLayout() {
        for (int i = 0; i < mDatas.size(); i++) {
            initStateAndProgress((AppItemInfo) mDatas.get(i));
        }
        notifyDataSetChanged();
    }

    private void initStateAndProgress(AppItemInfo appItemInfo) {
        if (appItemInfo != null) {
            AppInstallInfo appInstallInfo = mAppInstallMap.get(appItemInfo.getPackageName());
            if (appInstallInfo != null) {
                if (appInstallInfo.getVersionCode() < appItemInfo.getVersionCode()) {
                    appItemInfo.setState(Constants.APP_NEED_UPDATE);
                } else {
                    appItemInfo.setState(Constants.APP_HAVE_INSTALLED);
                }
            } else {
                appItemInfo.setState(Constants.APP_NOT_INSTALL);
            }

            DownloadInfo downloadInfo = new SQLOperator(mContext).
                    getDownloadInfoByPkgName(appItemInfo.getPackageName());
            if (downloadInfo != null) {
                long downloadSize = downloadInfo.getDownloadSize();
                long fileSize = downloadInfo.getFileSize();
                if (fileSize == 0) {
                    appItemInfo.setProgress(0);
                } else if (downloadSize < fileSize) {
                    appItemInfo.setProgress(downloadInfo.getProgress());
                    appItemInfo.setState(Constants.APP_DOWNLOAD_PAUSE);
                } else if (downloadSize == fileSize) {
                    switch (appItemInfo.getState()) {
                        case Constants.APP_HAVE_INSTALLED:
                            break;
                        default:
                            appItemInfo.setProgress(100);
                            appItemInfo.setState(Constants.APP_DOWNLOAD_FINISHED);
                            break;
                    }
                }
            }

            ArrayList<TaskInfo> allTask = mManager.getAllTask();
            for (int i = 0; i < allTask.size(); i++) {
                TaskInfo taskInfo = allTask.get(i);
                if (appItemInfo.getTaskId().equals(taskInfo.getTaskID())) {
                    if (taskInfo.isOnDownloading()) {
                        appItemInfo.setState(Constants.APP_DOWNLOAD_CONTINUE);
                        appItemInfo.setProgress(taskInfo.getProgress());
                    }
                }
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.app_item_layout:
                MainActivity.mHandler.sendMessage(MainActivity.mHandler.
                        obtainMessage(Constants.DETAIL_FRAGMENT, view.getTag()));
                break;
            case R.id.app_item_install:
                installClick((Button) view, (AppItemInfo) view.getTag());
                break;
            default:
                break;
        }
    }

    private void installClick(Button installBtn, AppItemInfo appItemInfo) {
        if (appItemInfo != null) {
            int state = appItemInfo.getState();
            if (state == Constants.APP_DOWNLOAD_FINISHED) {
                File file = FileHelper.getDownloadUrlFile(appItemInfo.getDownloadUrl());
                MainActivity.mHandler.sendMessage(MainActivity.mHandler.
                        obtainMessage(Constants.INSTALL_APK, file.getAbsolutePath()));
                if (!file.exists() || file.length() == 0) {
                    installBtn.setText(mContext.getString(R.string.download));
                }
            } else if (state == Constants.APP_HAVE_INSTALLED) {
                AppUtils.openApp(mContext, appItemInfo.getPackageName());
            } else if (NetUtils.isConnected(mContext)) {
                switch (state) {
                    case Constants.APP_NOT_INSTALL:
                        installBtn.setText(mContext.getString(R.string.downloading));
                        MainActivity.mDownloadService.addTask(appItemInfo.getTaskId() + "",
                                StoreApplication.mBaseUrl + "/" + appItemInfo.getDownloadUrl(),
                                appItemInfo.getAppName(),
                                appItemInfo.getPackageName(),
                                appItemInfo.getIconUrl());
                        break;
                    case Constants.APP_DOWNLOAD_CONTINUE:
                        installBtn.setText(mContext.getString(R.string.pause));
                        MainActivity.mDownloadService.stopTask(appItemInfo.getTaskId() + "");
                        break;
                    case Constants.APP_DOWNLOAD_PAUSE:
                        installBtn.setText(mContext.getString(R.string.downloading));
                        MainActivity.mDownloadService.startTask(appItemInfo.getTaskId() + "");
                        break;
                    case Constants.APP_NEED_UPDATE:
                        installBtn.setText(mContext.getString(R.string.downloading));
                        MainActivity.mDownloadService.addTask(appItemInfo.getTaskId() + "",
                                StoreApplication.mBaseUrl + "/" + appItemInfo.getDownloadUrl(),
                                appItemInfo.getAppName(),
                                appItemInfo.getPackageName(),
                                appItemInfo.getIconUrl());
                        break;
                }
            } else {
                Tools.toast(mContext, mContext.getString(R.string.check_net_state));
            }
        }
    }

    private class ViewHolder {
        private ImageView icon;
        private TextView appName;
        private TextView type;
        private TextView starNum;
        private Button install;
        private LinearLayout layout;

        public ViewHolder(View view) {
            layout = (LinearLayout) view.findViewById(R.id.app_item_layout);
            icon = (ImageView) view.findViewById(R.id.app_item_img);
            appName = (TextView) view.findViewById(R.id.app_item_name);
            type = (TextView) view.findViewById(R.id.app_item_type);
            starNum = (TextView) view.findViewById(R.id.app_item_star_num);
            install = (Button) view.findViewById(R.id.app_item_install);
        }
    }

    private class ItemDownloadListener implements DownloadListener {
        @Override
        public void onStart(DownloadInfo downloadInfo) {
            for (int i = 0; i < mDatas.size(); i++) {
                AppItemInfo appItemInfo = ((List<AppItemInfo>) mDatas).get(i);
                if (appItemInfo.getPackageName().equals(downloadInfo.getPackageName())) {
                    appItemInfo.setState(Constants.APP_DOWNLOAD_CONTINUE);
                    break;
                }
            }
        }

        @Override
        public void onProgress(DownloadInfo downloadInfo, boolean isSupportFTP) {
            for (int i = 0; i < mDatas.size(); i++) {
                AppItemInfo appItemInfo = ((List<AppItemInfo>) mDatas).get(i);
                if (appItemInfo.getPackageName().equals(downloadInfo.getPackageName())) {
                    appItemInfo.setState(Constants.APP_DOWNLOAD_CONTINUE);
                    break;
                }
            }
        }

        @Override
        public void onStop(DownloadInfo downloadInfo, boolean isSupportFTP) {
            for (int i = 0; i < mDatas.size(); i++) {
                AppItemInfo appItemInfo = ((List<AppItemInfo>) mDatas).get(i);
                if (appItemInfo.getPackageName().equals(downloadInfo.getPackageName())) {
                    appItemInfo.setState(Constants.APP_DOWNLOAD_PAUSE);
                    break;
                }
            }
        }

        @Override
        public void onError(DownloadInfo downloadInfo, String error) {
            for (int i = 0; i < mDatas.size(); i++) {
                AppItemInfo appItemInfo = ((List<AppItemInfo>) mDatas).get(i);
                if (appItemInfo.getPackageName().equals(downloadInfo.getPackageName())) {
                    appItemInfo.setState(Constants.APP_DOWNLOAD_PAUSE);
                    break;
                }
            }
        }

        @Override
        public void onSuccess(DownloadInfo downloadInfo) {
            for (int i = 0; i < mDatas.size(); i++) {
                AppItemInfo appItemInfo = ((List<AppItemInfo>) mDatas).get(i);
                if (appItemInfo.getPackageName().equals(downloadInfo.getPackageName())) {
                    appItemInfo.setState(Constants.APP_DOWNLOAD_FINISHED);
                    break;
                }
            }
        }
    }
}