package com.openthos.appstore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.app.StoreApplication;
import com.openthos.appstore.bean.AppInstallInfo;
import com.openthos.appstore.bean.AppItemInfo;
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
            holder.install.setAnimation(null);
            switch (appItemInfo.getState()) {
                case Constants.APP_NOT_INSTALL:
                    holder.install.setBackground(mContext.getDrawable(R.drawable.download));
                    break;
                case Constants.APP_HAVE_INSTALLED:
                    holder.install.setBackground(mContext.getDrawable(R.drawable.open));
                    break;
                case Constants.APP_DOWNLOAD_CONTINUE:
                    holder.install.setBackground(mContext.getDrawable(R.drawable.downloading));
                    Tools.setDowningAnimation(holder.install);
                    break;
                case Constants.APP_DOWNLOAD_PAUSE:
                    holder.install.setBackground(mContext.getDrawable(R.drawable.pause));
                    break;
                case Constants.APP_NEED_UPDATE:
                    holder.install.setBackground(mContext.getDrawable(R.drawable.upgrade));
                    break;
                case Constants.APP_DOWNLOAD_FINISHED:
                    holder.install.setBackground(mContext.getDrawable(R.drawable.install));
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

            AppItemInfo downloadInfo = new SQLOperator(mContext).
                    getDownloadInfoByPkgName(appItemInfo.getPackageName());
            if (downloadInfo != null) {
                long downloadSize = downloadInfo.getDownFileSize();
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

            ArrayList<AppItemInfo> allTask = mManager.getAllInfo();
            for (int i = 0; i < allTask.size(); i++) {
                AppItemInfo appInfo = allTask.get(i);
                if (appItemInfo.getTaskId().equals(appInfo.getTaskId())) {
                    if (appInfo.isOnDownloading()) {
                        appItemInfo.setState(Constants.APP_DOWNLOAD_CONTINUE);
                        appItemInfo.setProgress(appInfo.getProgress());
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
                installClick((ImageButton) view, (AppItemInfo) view.getTag());
                break;
            default:
                break;
        }
    }

    private void installClick(ImageButton installBtn, AppItemInfo appItemInfo) {
        if (appItemInfo != null) {
            int state = appItemInfo.getState();
            if (state == Constants.APP_DOWNLOAD_FINISHED) {
                appItemInfo.setFilePath(FileHelper.
                    getDownloadUrlFile(appItemInfo.getDownloadUrl()).getAbsolutePath());
                MainActivity.mHandler.sendMessage(MainActivity.mHandler.
                        obtainMessage(Constants.INSTALL_APK, appItemInfo));
            } else if (state == Constants.APP_HAVE_INSTALLED) {
                AppUtils.openApp(mContext, appItemInfo.getPackageName());
            } else if (NetUtils.isConnected(mContext)) {
                installBtn.setAnimation(null);
                switch (state) {
                    case Constants.APP_NOT_INSTALL:
                        installBtn.setBackground(mContext.getDrawable(R.drawable.downloading));
                        Tools.setDowningAnimation(installBtn);
                        MainActivity.mDownloadService.addTask(appItemInfo.getTaskId() + "",
                                StoreApplication.mBaseUrl + "/" + appItemInfo.getDownloadUrl(),
                                appItemInfo.getAppName(),
                                appItemInfo.getPackageName(),
                                appItemInfo.getIconUrl());
                        break;
                    case Constants.APP_DOWNLOAD_CONTINUE:
                        installBtn.setBackground(mContext.getDrawable(R.drawable.pause));
                        MainActivity.mDownloadService.stopTask(appItemInfo.getTaskId() + "");
                        break;
                    case Constants.APP_DOWNLOAD_PAUSE:
                        installBtn.setBackground(mContext.getDrawable(R.drawable.downloading));
                        Tools.setDowningAnimation(installBtn);
                        MainActivity.mDownloadService.startTask(appItemInfo.getTaskId() + "");
                        break;
                    case Constants.APP_NEED_UPDATE:
                        installBtn.setBackground(mContext.getDrawable(R.drawable.downloading));
                        Tools.setDowningAnimation(installBtn);
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
        private ImageButton install;
        private LinearLayout layout;

        public ViewHolder(View view) {
            layout = (LinearLayout) view.findViewById(R.id.app_item_layout);
            icon = (ImageView) view.findViewById(R.id.app_item_img);
            appName = (TextView) view.findViewById(R.id.app_item_name);
            type = (TextView) view.findViewById(R.id.app_item_type);
            starNum = (TextView) view.findViewById(R.id.app_item_star_num);
            install = (ImageButton) view.findViewById(R.id.app_item_install);
        }
    }

    private class ItemDownloadListener implements DownloadListener {
        @Override
        public void onStart(AppItemInfo downloadInfo) {
            for (int i = 0; i < mDatas.size(); i++) {
                AppItemInfo appItemInfo = ((List<AppItemInfo>) mDatas).get(i);
                if (appItemInfo.getPackageName().equals(downloadInfo.getPackageName())) {
                    appItemInfo.setState(Constants.APP_DOWNLOAD_CONTINUE);
                    break;
                }
            }
        }

        @Override
        public void onProgress(AppItemInfo downloadInfo, boolean isSupportFTP) {
            for (int i = 0; i < mDatas.size(); i++) {
                AppItemInfo appItemInfo = ((List<AppItemInfo>) mDatas).get(i);
                if (appItemInfo.getPackageName().equals(downloadInfo.getPackageName())) {
                    appItemInfo.setState(Constants.APP_DOWNLOAD_CONTINUE);
                    break;
                }
            }
        }

        @Override
        public void onStop(AppItemInfo downloadInfo, boolean isSupportFTP) {
            for (int i = 0; i < mDatas.size(); i++) {
                AppItemInfo appItemInfo = ((List<AppItemInfo>) mDatas).get(i);
                if (appItemInfo.getPackageName().equals(downloadInfo.getPackageName())) {
                    appItemInfo.setState(Constants.APP_DOWNLOAD_PAUSE);
                    break;
                }
            }
        }

        @Override
        public void onError(AppItemInfo downloadInfo, String error) {
            for (int i = 0; i < mDatas.size(); i++) {
                AppItemInfo appItemInfo = ((List<AppItemInfo>) mDatas).get(i);
                if (appItemInfo.getPackageName().equals(downloadInfo.getPackageName())) {
                    appItemInfo.setState(Constants.APP_DOWNLOAD_PAUSE);
                    break;
                }
            }
        }

        @Override
        public void onSuccess(AppItemInfo downloadInfo) {
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
