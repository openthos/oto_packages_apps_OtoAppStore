package com.openthos.appstore.download;

import android.content.Context;
import android.content.SharedPreferences;

import com.openthos.appstore.app.StoreApplication;
import com.openthos.appstore.bean.DownloadInfo;
import com.openthos.appstore.bean.TaskInfo;
import com.openthos.appstore.utils.SQLOperator;
import com.openthos.appstore.utils.FileHelper;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownloadManager {
    private static final int MAX_DOWNLOADING_TASK = 5;
    private static final int KEEP_ALIVE_TIME = 30;
    private static final int CACHE_WORK_QUEUE = 2000;

    private Context mContext;
    private String mUserID;
    private int mCount = 0;
    private boolean mIsSupportFTP;
    private ThreadPoolExecutor mPool;
    private ArrayList<Downloader> mTaskList;
    private SharedPreferences mSharedPreferences;
    private DownloadListener mAlltasklistener;

    public DownloadManager(Context context) {
        mContext = context;
        mPool = new ThreadPoolExecutor(
                MAX_DOWNLOADING_TASK, MAX_DOWNLOADING_TASK, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(CACHE_WORK_QUEUE));
        mTaskList = new ArrayList<>();
        mSharedPreferences = mContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        mUserID = mSharedPreferences.getString("UserID", StoreApplication.mUserId);
        recoverData(mContext, mUserID);
    }

    private void recoverData(Context context, String userID) {
        stopAllTask();
        mTaskList = new ArrayList<>();
        SQLOperator datakeeper = new SQLOperator(context);
        ArrayList<DownloadInfo> downloadInfoList = null;
        if (userID == null) {
            downloadInfoList = datakeeper.getAllDownloadInfo();
        } else {
            downloadInfoList = datakeeper.getUserDownloadInfo(userID);
        }
        if (downloadInfoList.size() > 0) {
            int listSize = downloadInfoList.size();
            for (int i = 0; i < listSize; i++) {
                DownloadInfo downloadInfo = downloadInfoList.get(i);
                Downloader sqlDownloader = new Downloader(
                        context, downloadInfo, mPool, userID, mIsSupportFTP, false);
                sqlDownloader.setDownloadListener("public", mAlltasklistener);
                mTaskList.add(sqlDownloader);
            }
        }
    }

    public void setSupportFTP(boolean isSupportFTP) {
        if ((!mIsSupportFTP) && isSupportFTP) {
            int taskSize = mTaskList.size();
            for (int i = 0; i < taskSize; i++) {
                Downloader downloader = mTaskList.get(i);
                downloader.setSupportFTP(true);
            }
        }
        mIsSupportFTP = isSupportFTP;
    }

    public void addTask(String taskID, String url, String fileName, String packageName) {
        addTask(taskID, url, fileName, packageName, null);
    }

    public void addTask(String taskID, String url, String fileName, String packageName,
                        String iconUrl) {
        addTask(taskID, url, fileName, packageName, null, iconUrl);
    }

    public void addTask(String taskID, String url,
                        String fileName, String packageName, String filepath, String iconUrl) {
        if (taskID == null) {
            taskID = fileName;
        }

        DownloadInfo downloadinfo = new DownloadInfo();
        downloadinfo.setUserID(mUserID);
        downloadinfo.setDownloadSize(0);
        downloadinfo.setFileSize(0);
        downloadinfo.setTaskID(taskID);
        downloadinfo.setFileName(fileName);
        downloadinfo.setUrl(url);
        downloadinfo.setPackageName(packageName);
        downloadinfo.setIconUrl(iconUrl);
        if (filepath == null) {
            downloadinfo.setFilePath(FileHelper.getDownloadUrlPath(url));
        } else {
            downloadinfo.setFilePath(filepath);
        }
        Downloader taskDownloader = new Downloader(
                mContext, downloadinfo, mPool, mUserID, mIsSupportFTP, true);
        if (mIsSupportFTP) {
            taskDownloader.setSupportFTP(true);
        } else {
            taskDownloader.setSupportFTP(false);
        }
        taskDownloader.start();
        mTaskList.add(0, taskDownloader);
    }

    public void deleteTask(String taskID) {
        for (int i = 0; i < mTaskList.size(); i++) {
            Downloader deletedownloader = mTaskList.get(i);
            if (deletedownloader.getTaskID().equals(taskID)) {
                deletedownloader.destroy();
                mTaskList.remove(deletedownloader);
                break;
            }
        }
    }

    public ArrayList<TaskInfo> getAllTask() {
        ArrayList<TaskInfo> taskInfolist = new ArrayList<TaskInfo>();
        for (int i = 0; i < mTaskList.size(); i++) {
            Downloader deletedownloader = mTaskList.get(i);
            DownloadInfo sqldownloadinfo = deletedownloader.getDownloadInfo();
            TaskInfo taskinfo = new TaskInfo();
            taskinfo.setFileName(sqldownloadinfo.getFileName());
            taskinfo.setOnDownloading(deletedownloader.isDownloading());
            taskinfo.setTaskID(sqldownloadinfo.getTaskID());
            taskinfo.setFileSize(sqldownloadinfo.getFileSize());
            taskinfo.setDownFileSize(sqldownloadinfo.getDownloadSize());
            taskinfo.setIconUrl(sqldownloadinfo.getIconUrl());
            taskinfo.setFilePath(sqldownloadinfo.getFilePath());
            taskinfo.setPackageName(sqldownloadinfo.getPackageName());
            taskInfolist.add(taskinfo);
        }
        return taskInfolist;
    }

    public void startTask(String taskID) {
        for (int i = 0; i < mTaskList.size(); i++) {
            Downloader deletedownloader = mTaskList.get(i);
            if (deletedownloader.getTaskID().equals(taskID)) {
                deletedownloader.start();
                break;
            }
        }
    }

    public void stopTask(String taskID) {
        for (int i = 0; i < mTaskList.size(); i++) {
            Downloader deletedownloader = mTaskList.get(i);
            if (deletedownloader.getTaskID().equals(taskID)) {
                deletedownloader.stop();
                break;
            }
        }
    }

    public void startAllTask() {
        for (int i = 0; i < mTaskList.size(); i++) {
            Downloader deletedownloader = mTaskList.get(i);
            DownloadInfo downloadInfo = deletedownloader.getDownloadInfo();
            if (downloadInfo != null && downloadInfo.getFileSize() != 0 &&
                    downloadInfo.getFileSize() != downloadInfo.getDownloadSize()) {
                deletedownloader.start();
            }
        }
    }

    public void stopAllTask() {
        for (int i = 0; i < mTaskList.size(); i++) {
            Downloader deletedownloader = mTaskList.get(i);
            deletedownloader.stop();
        }
    }

    public void setAllTaskListener(DownloadListener listener) {
        mAlltasklistener = listener;
        Downloader.setDownloadListener("" + (++mCount), listener);
    }
}
