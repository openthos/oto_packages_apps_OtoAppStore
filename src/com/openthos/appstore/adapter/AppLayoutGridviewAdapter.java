package com.openthos.appstore.adapter;

import android.content.Context;
import android.os.Message;
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
import com.openthos.appstore.bean.AppLayoutGridviewInfo;
import com.openthos.appstore.bean.SQLDownLoadInfo;
import com.openthos.appstore.utils.AppUtils;
import com.openthos.appstore.utils.FileHelper;
import com.openthos.appstore.utils.SPUtils;
import com.openthos.appstore.utils.Tools;
import com.openthos.appstore.utils.download.DownLoadListener;
import com.openthos.appstore.utils.download.DownLoadManager;
import com.openthos.appstore.utils.download.DownLoadService;
import com.openthos.appstore.view.CustomProgressBar;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by luojunhuan on 16-10-26.
 */
public class AppLayoutGridviewAdapter extends BasicAdapter implements View.OnClickListener {
    private DownLoadManager mDownLoadManager;
    private UpdateProgress mUpdateProgress;

    public AppLayoutGridviewAdapter(Context context, boolean isAll) {
        super(context, isAll);
        mDatas = new ArrayList<>();
        mDownLoadManager = DownLoadService.getDownLoadManager();
        mDownLoadManager.setAllTaskListener(new AppDownLoadListener());
    }

    @Override
    public long getItemId(int position) {
        return mDatas == null ? -1 : ((AppLayoutGridviewInfo) mDatas.get(position)).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        boolean isFirstInit = false;
        if (convertView == null) {
            isFirstInit = true;
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.app_layout_gridview, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        if (mDatas != null && mDatas.size() != 0) {
            AppLayoutGridviewInfo appInfo = (AppLayoutGridviewInfo) mDatas.get(position);
            Picasso.with(mContext).load(Constants.BASEURL + "/" +
                    appInfo.getIconUrl()).into(holder.icon);
            holder.name.setText(appInfo.getName());
            holder.type.setText(appInfo.getType());
            if (!isFirstInit) {
                appInfo.setState(SPUtils.getDownloadState(mContext, appInfo.getAppPackageName()));
            } else {
                SPUtils.saveDownloadState(mContext, appInfo.getAppPackageName(), appInfo.getState());
            }
            switch (appInfo.getState()) {
                case Constants.APP_NOT_INSTALL:
                    setContent(holder.install, R.string.install,
                            R.drawable.shape_button_white_cyan, R.color.button_cyan);
                    break;
                case Constants.APP_HAVE_INSTALLED:
                    setContent(holder.install, R.string.have_installed,
                            R.drawable.shape_button_white_gray, R.color.button_gray);
                    break;
                case Constants.APP_DOWNLOAD_CONTINUE:
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.progressBar.setProgress(appInfo.getProgress());
                    setContent(holder.install, R.string.pause, 0, R.color.button_cyan);
                    break;
                case Constants.APP_DOWNLOAD_PAUSE:
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.progressBar.setProgress(appInfo.getProgress());
                    setContent(holder.install, R.string.continues, 0, R.color.button_cyan);
                    break;
                case Constants.APP_NEED_UPDATE:
                    setContent(holder.install, R.string.update, R.drawable.shape_button_white_cyan,
                            R.color.button_cyan);
                    break;
                case Constants.APP_DOWNLOAD_FINISHED:
                    setContent(holder.install, R.string.finished,
                               R.drawable.shape_button_white_cyan, R.color.button_cyan);
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

    private void setContent(Button btn, int text, int background, int color) {
        btn.setText(text);
        if (background != 0) {
            btn.setBackgroundResource(background);
        } else {
            btn.setBackground(null);
        }
        btn.setTextColor(mContext.getResources().getColor(color));
    }

    @Override
    public void onClick(View v) {
        int possition = (int) v.getTag();
        AppLayoutGridviewInfo appInfo = (AppLayoutGridviewInfo) mDatas.get(possition);
        String appId = appInfo.getId() + "";
        switch (v.getId()) {
            case R.id.app_layout_gridview_install:
                Button install = (Button) v;
                String btnStr = install.getText().toString();

                String continues = mContext.getResources().getString(R.string.continues);
                String pause = mContext.getResources().getString(R.string.pause);
                String installs = mContext.getResources().getString(R.string.install);
                String update = mContext.getResources().getString(R.string.update);
                String finished = mContext.getResources().getString(R.string.finished);

                if (btnStr.equals(continues)) {
                    install.setText(pause);
                    SPUtils.saveDownloadState(
                            mContext, appInfo.getAppPackageName(), Constants.APP_DOWNLOAD_CONTINUE);
                    MainActivity.mBinder.startTask(appId);
                } else if (btnStr.equals(pause)) {
                    install.setText(continues);
                    SPUtils.saveDownloadState(
                            mContext, appInfo.getAppPackageName(), Constants.APP_DOWNLOAD_PAUSE);
                    MainActivity.mBinder.stopTask(appId);
                } else if (btnStr.equals(installs)) {
                    install.setText(continues);
                    SPUtils.saveDownloadState(
                            mContext, appInfo.getAppPackageName(), Constants.APP_DOWNLOAD_CONTINUE);
                    MainActivity.mBinder.addTask(appId, Constants.BASEURL + "/" +
                            appInfo.getDownloadUrl(),
                            FileHelper.getNameFromUrl(appInfo.getDownloadUrl()),
                            appInfo.getAppPackageName(),
                            Constants.BASEURL + "/" + appInfo.getIconUrl());
                } else if (btnStr.equals(update)) {
                    install.setText(continues);
                    SPUtils.saveDownloadState(
                            mContext, appInfo.getAppPackageName(), Constants.APP_DOWNLOAD_CONTINUE);
                    MainActivity.mBinder.addTask(appId, Constants.BASEURL + "/" +
                            appInfo.getDownloadUrl(),
                            FileHelper.getNameFromUrl(appInfo.getDownloadUrl()),
                            appInfo.getAppPackageName(),
                            Constants.BASEURL + "/" + appInfo.getIconUrl());
                } else if (btnStr.equals(finished)) {
                    File file =
                            new File(FileHelper.getDefaultFileFromUrl(appInfo.getDownloadUrl()));
                    if (file.exists() && file.length() != 0) {
                        AppUtils.installApk(mContext, file.getAbsolutePath());
                    } else {
                        SPUtils.saveDownloadState(mContext, appInfo.getAppPackageName(),
                                                  Constants.APP_NOT_INSTALL);
                        notifyDataSetChanged();
                        Message message = MainActivity.mHandler.obtainMessage();
                        message.what = Constants.TOAST;
                        message.obj = mContext.getString(R.string.this_file_is_not_exist);
                        MainActivity.mHandler.sendMessage(message);
                    }
                }
                break;

            default:
                Message message = MainActivity.mHandler.obtainMessage();
                message.what = Constants.DETAIL_FRAGMENT;
                message.obj = appId + " " + appInfo.getState();
                MainActivity.mHandler.sendMessage(message);
                break;
        }
    }

    class ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView type;
        private Button install;
        private ProgressBar progressBar;

        public ViewHolder(View view) {
            icon = (ImageView) view.findViewById(R.id.app_layout_gridview_icon);
            name = (TextView) view.findViewById(R.id.app_layout_gridview_name);
            type = (TextView) view.findViewById(R.id.app_layout_gridview_type);
            install = (Button) view.findViewById(R.id.app_layout_gridview_install);
            progressBar = (ProgressBar) view.findViewById(
                                                      R.id.app_layout_gridview_progressbar);
            progressBar.setVisibility(View.GONE);
        }
    }

    public void addDatas(List<AppLayoutGridviewInfo> datas) {
        this.mDatas.clear();
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

    private class AppDownLoadListener implements DownLoadListener {
        @Override
        public void onStart(SQLDownLoadInfo sqlDownLoadInfo) {
            notifyDataSetChanged();
        }

        @Override
        public void onProgress(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
            for (AppLayoutGridviewInfo appInfo : (List<AppLayoutGridviewInfo>) mDatas) {
                if ((appInfo.getId() + "").equals(sqlDownLoadInfo.getTaskID())) {
                    appInfo.setDownFileSize(sqlDownLoadInfo.getDownloadSize());
                    appInfo.setFileSize(sqlDownLoadInfo.getFileSize());
                    mUpdateProgress.onProgress(sqlDownLoadInfo);
                    break;
                }
            }
        }

        @Override
        public void onStop(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
            notifyDataSetChanged();
        }

        @Override
        public void onError(SQLDownLoadInfo sqlDownLoadInfo, String error) {

        }

        @Override
        public void onSuccess(SQLDownLoadInfo sqlDownLoadInfo) {
            for (AppLayoutGridviewInfo appInfo : (List<AppLayoutGridviewInfo>) mDatas) {
                if ((appInfo.getId() + "").equals(sqlDownLoadInfo.getTaskID())) {
                    appInfo.setDownFileSize(sqlDownLoadInfo.getDownloadSize());
                    appInfo.setFileSize(sqlDownLoadInfo.getFileSize());
                    mUpdateProgress.onProgress(sqlDownLoadInfo);
                    break;
                }
            }
        }
    }

    public void setUpdateProcessListener(UpdateProgress updateProgress) {
        mUpdateProgress = updateProgress;
    }

    public interface UpdateProgress {
        void onProgress(SQLDownLoadInfo sqlDownLoadInfo);
    }
}
