package com.openthos.appstore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import com.openthos.appstore.bean.AppItemInfo;
import com.openthos.appstore.utils.AppUtils;
import com.openthos.appstore.utils.FileHelper;
import com.openthos.appstore.utils.ImageCache;
import com.openthos.appstore.utils.NetUtils;
import com.openthos.appstore.utils.Tools;

import java.io.File;
import java.util.List;

public class RecyclerItemAdapter extends RecyclerView.Adapter<RecyclerItemAdapter.HorViewHolder>
        implements View.OnClickListener {
    private List<AppItemInfo> mDatas;
    private Context mContext;

    public RecyclerItemAdapter(Context context, List<AppItemInfo> datas) {
        mContext = context;
        mDatas = datas;
    }

    @Override
    public HorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.app_item, parent, false);
        return new HorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HorViewHolder holder, int position) {
        if (mDatas != null && mDatas.size() != 0) {
            AppItemInfo appItemInfo = mDatas.get(position);
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
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void refreshLayout() {
        notifyDataSetChanged();
    }

    @Override
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
                appItemInfo.setFilePath(file.getAbsolutePath());
                MainActivity.mHandler.sendMessage(MainActivity.mHandler.
                        obtainMessage(Constants.INSTALL_APK, appItemInfo));
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

    class HorViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView appName;
        private TextView type;
        private TextView starNum;
        private Button install;
        private LinearLayout layout;

        public HorViewHolder(View view) {
            super(view);
            layout = (LinearLayout) view.findViewById(R.id.app_item_layout);
            icon = (ImageView) view.findViewById(R.id.app_item_img);
            appName = (TextView) view.findViewById(R.id.app_item_name);
            type = (TextView) view.findViewById(R.id.app_item_type);
            starNum = (TextView) view.findViewById(R.id.app_item_star_num);
            install = (Button) view.findViewById(R.id.app_item_install);
        }
    }
}
