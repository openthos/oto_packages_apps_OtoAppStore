package com.openthos.appstore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.SQLDownLoadInfo;
import com.openthos.appstore.bean.TaskInfo;
import com.openthos.appstore.utils.AppUtils;
import com.openthos.appstore.utils.Tools;
import com.openthos.appstore.utils.download.DownLoadListener;
import com.openthos.appstore.utils.download.DownLoadManager;
import com.openthos.appstore.utils.FileHelper;

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
        holder.fileProgress.setProgress(taskInfo.getProgress());
        holder.textProgress.setText(taskInfo.getProgress() + "%");
        holder.downloadIcon.setOnCheckedChangeListener(new CheckedChangeListener(position));
        if (taskInfo.isOnDownloading()) {
            holder.downloadIcon.setChecked(true);
        } else {
            holder.downloadIcon.setChecked(false);
        }

        return convertView;
    }

    public void setAll(boolean isAll) {
        mIsAll = isAll;
    }

    class ViewHolder {
        private TextView fileName;
        private TextView textProgress;
        private ProgressBar fileProgress;
        private CheckBox downloadIcon;

        public ViewHolder(View view) {
            fileName = (TextView) view.findViewById(R.id.file_name);
            textProgress = (TextView) view.findViewById(R.id.file_size);
            fileProgress = (ProgressBar) view.findViewById(R.id.progressbar);
            downloadIcon = (CheckBox) view.findViewById(R.id.checkbox);
            downloadIcon.setVisibility(View.GONE);
        }
    }

    public void addItem(TaskInfo taskinfo) {
        mDatas.add(taskinfo);
        notifyDataSetInvalidated();
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

    class CheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        int position = 0;
        TaskInfo mTaskInfo;

        public CheckedChangeListener(int position) {
            this.position = position;
            mTaskInfo = (TaskInfo) mDatas.get(position);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                // continue download
                mTaskInfo.setOnDownloading(true);
                MainActivity.mBinder.startTask(mTaskInfo.getTaskID());
            } else {
                //stop download
                mTaskInfo.setOnDownloading(false);
                MainActivity.mBinder.stopTask(mTaskInfo.getTaskID());
            }
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
            //According to listen to the information to find a list of corresponding tasks,
            // the progress of the corresponding task
            for (TaskInfo taskInfo : (ArrayList<TaskInfo>) mDatas) {
                if (taskInfo.getTaskID().equals(sqlDownLoadInfo.getTaskID())) {
                    taskInfo.setDownFileSize(sqlDownLoadInfo.getDownloadSize());
                    taskInfo.setFileSize(sqlDownLoadInfo.getFileSize());
                    ManagerDownloadAdapter.this.notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void onStop(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {

        }

        @Override
        public void onSuccess(SQLDownLoadInfo sqlDownLoadInfo) {
            //According to listen to the information to find a list of corresponding tasks,
            // the progress of the delete task
            for (TaskInfo taskInfo : (ArrayList<TaskInfo>) mDatas) {
                if (taskInfo.getTaskID().equals(sqlDownLoadInfo.getTaskID())) {
//                    mDatas.remove(taskInfo);
                    ManagerDownloadAdapter.this.notifyDataSetChanged();
                    break;
                }
            }

            AppUtils.installApk(mContext, sqlDownLoadInfo.getFilePath());
        }

        @Override
        public void onError(SQLDownLoadInfo sqlDownLoadInfo, String error) {
            //According to listen to the information to find a list of corresponding tasks,
            // the progress of the stop task
            for (TaskInfo taskInfo : (ArrayList<TaskInfo>) mDatas) {
                if (taskInfo.getTaskID().equals(sqlDownLoadInfo.getTaskID())) {
                    taskInfo.setOnDownloading(false);
                    FileHelper.deleteFile(sqlDownLoadInfo.getFileName());
                    taskInfo.setDownFileSize(0);
                    taskInfo.setFileSize(0);
                    ManagerDownloadAdapter.this.notifyDataSetChanged();
                    break;
                }
            }
            Tools.toast(mContext, error);
        }
    }
}
