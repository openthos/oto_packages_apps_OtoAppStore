package com.openthos.appstore.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.SQLDownLoadInfo;
import com.openthos.appstore.bean.TaskInfo;
import com.openthos.appstore.utils.AppUtils;
import com.openthos.appstore.utils.SPUtils;
import com.openthos.appstore.utils.Tools;
import com.openthos.appstore.utils.download.DownLoadListener;
import com.openthos.appstore.utils.download.DownLoadManager;
import com.openthos.appstore.utils.FileHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ManagerDownloadAdapter extends BasicAdapter {
    private DownLoadManager mDownLoadManager;

    public ManagerDownloadAdapter(Context context, DownLoadManager downLoadManager) {
        super(context);
        if (downLoadManager != null) {
            mDownLoadManager = downLoadManager;
            mDownLoadManager.setAllTaskListener(new DownloadManagerListener());
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.item_manager_download, null);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        TaskInfo taskInfo = (TaskInfo) mDatas.get(position);
        holder.fileName.setText(taskInfo.getFileName());

        if (taskInfo.getProgress() < Constants.MAX_PROGRESS) {
            holder.fileProgress.setProgress(taskInfo.getProgress());
            holder.textProgress.setText(taskInfo.getProgress() + "%");
            holder.speech.setText(Tools.transformFileSize(taskInfo.getSpeech() * 1024) + "/s");
            holder.fileProgress.setVisibility(View.VISIBLE);
            holder.controlDownload.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(taskInfo.getIconUrl()).into(holder.appIcon);
        } else {
            holder.fileProgress.setVisibility(View.INVISIBLE);
            holder.textProgress.setText(Tools.transformFileSize(taskInfo.getFileSize()));
            holder.speech.setText("");
            Drawable appIcon = AppUtils.getAPKIcon(mContext, taskInfo.getFilePath());
            if (appIcon != null) {
                holder.appIcon.setImageDrawable(appIcon);
            } else {
                holder.appIcon.setImageResource(R.mipmap.ic_launcher);
            }
        }
        holder.controlDownload.setOnClickListener(new BtnStateOnclickListener(position));
        if (taskInfo.isOnDownloading()) {
            holder.controlDownload.setText(mContext.getResources().getString(R.string.pause));
        } else if (taskInfo.getProgress() == Constants.MAX_PROGRESS) {
//            holder.controlDownload.setText(mContext.getResources().getString(R.string.install));
            holder.controlDownload.setVisibility(View.INVISIBLE);
//            SPUtils.saveDownloadState(mContext, taskInfo.getPackageName(),
//                                                               Constants.APP_DOWNLOAD_FINISHED);
        } else {
            holder.controlDownload.setText(mContext.getResources().getString(R.string.continues));
        }

        return convertView;
    }

    public void setAll(boolean isAll) {
        mIsAll = isAll;
    }

    class ViewHolder {
        private TextView fileName;
        private TextView textProgress;
        private TextView speech;
        private ProgressBar fileProgress;
        private Button controlDownload;
        private ImageView appIcon;

        public ViewHolder(View view) {
            appIcon = (ImageView) view.findViewById(R.id.app_icon);
            fileName = (TextView) view.findViewById(R.id.file_name);
            textProgress = (TextView) view.findViewById(R.id.file_size);
            speech = (TextView) view.findViewById(R.id.file_speech);
            fileProgress = (ProgressBar) view.findViewById(R.id.progressbar);
            controlDownload = (Button) view.findViewById(R.id.control_download);
        }
    }

    public void addData(List<TaskInfo> listdata) {
        mDatas.clear();
        if (mIsAll) {
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

    class BtnStateOnclickListener implements View.OnClickListener {

        int position = 0;
        TaskInfo mTaskInfo;

        public BtnStateOnclickListener(int position) {
            this.position = position;
            mTaskInfo = (TaskInfo) mDatas.get(position);
        }

        @Override
        public void onClick(View v) {
            Button button_state = (Button)v;
            String currentState = (button_state).getText().toString().trim();
            final String stateContinue = mContext.getResources().getString(R.string.continues);
            final String statePause = mContext.getResources().getString(R.string.pause);
//            final String stateInstall = mContext.getResources().getString(R.string.install);

            if(stateContinue.equals(currentState)) {
                button_state.setText(statePause);
                File file = new File(FileHelper.getDefaultFile(mTaskInfo.getFileName()));

                if (mTaskInfo.getProgress() == Constants.MAX_PROGRESS && file.exists()
                        && file.length() != 0) {
                    Message message = MainActivity.mHandler.obtainMessage();
                    message.what = Constants.TOAST;
                    message.obj = mContext.getString(R.string.this_task_have_been_download);
                    MainActivity.mHandler.sendMessage(message);
                } else {
                    mTaskInfo.setOnDownloading(true);
                    MainActivity.mBinder.startTask(mTaskInfo.getTaskID());
                }

            } else if (statePause.equals(currentState)) {
                button_state.setText(stateContinue);
                mTaskInfo.setOnDownloading(false);
                MainActivity.mBinder.stopTask(mTaskInfo.getTaskID());
            }
//            else if(stateInstall.equals(currentState)) {
//                String result = AppUtils.installApk(mContext,
//                        FileHelper.getDefaultFile(mTaskInfo.getFileName()));
//                if (result != null) {
//                    Tools.toast(mContext, result);
//                }
//            }

            ManagerDownloadAdapter.this.notifyDataSetChanged();
        }
    }

    private class DownloadManagerListener implements DownLoadListener {

        @Override
        public void onStart(SQLDownLoadInfo sqlDownLoadInfo) {
            Tools.toast(mContext, mContext.getString(R.string.start_download));
        }

        @Override
        public void onProgress(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
            for (TaskInfo taskInfo : (ArrayList<TaskInfo>) mDatas) {
                if (taskInfo.getTaskID().equals(sqlDownLoadInfo.getTaskID())) {
                    taskInfo.setDownFileSize(sqlDownLoadInfo.getDownloadSize());
                    taskInfo.setFileSize(sqlDownLoadInfo.getFileSize());
                    taskInfo.setSpeech(sqlDownLoadInfo.getSpeech());
                    ManagerDownloadAdapter.this.notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void onStop(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
            for (TaskInfo taskInfo : (ArrayList<TaskInfo>) mDatas) {
                if (taskInfo.getTaskID().equals(sqlDownLoadInfo.getTaskID())) {
                    taskInfo.setSpeech(0);
                    taskInfo.setOnDownloading(false);
                    ManagerDownloadAdapter.this.notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void onSuccess(SQLDownLoadInfo sqlDownLoadInfo) {
            for (TaskInfo taskInfo : (ArrayList<TaskInfo>) mDatas) {
                if (taskInfo.getTaskID().equals(sqlDownLoadInfo.getTaskID())) {
                    taskInfo.setSpeech(0);
                    taskInfo.setOnDownloading(false);
                    taskInfo.setDownFileSize(sqlDownLoadInfo.getDownloadSize());
                    taskInfo.setFileSize(sqlDownLoadInfo.getFileSize());
                    ManagerDownloadAdapter.this.notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void onError(SQLDownLoadInfo sqlDownLoadInfo, String error) {
            for (TaskInfo taskInfo : (ArrayList<TaskInfo>) mDatas) {
                if (taskInfo.getTaskID().equals(sqlDownLoadInfo.getTaskID())) {
                    taskInfo.setOnDownloading(false);
                    FileHelper.deleteFile(sqlDownLoadInfo.getFileName());
                    taskInfo.setDownFileSize(sqlDownLoadInfo.getDownloadSize());
                    taskInfo.setFileSize(sqlDownLoadInfo.getFileSize());
                    ManagerDownloadAdapter.this.notifyDataSetChanged();
                    break;
                }
            }
            Tools.toast(mContext, error + "");
        }
    }
}
