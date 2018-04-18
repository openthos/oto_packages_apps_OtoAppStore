package org.openthos.appstore.download;

import android.content.Context;
import android.content.SharedPreferences;

import org.openthos.appstore.app.StoreApplication;
import org.openthos.appstore.bean.AppItemInfo;
import org.openthos.appstore.utils.SQLOperator;
import org.openthos.appstore.utils.FileHelper;

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
    public ArrayList<Downloader> mTaskList;
    private SharedPreferences mSharedPreferences;
    private DownloadListener mAlltasklistener;
    private SQLOperator mSQLOperator;

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
        mSQLOperator = new SQLOperator(context);
        ArrayList<AppItemInfo> downloadInfoList = null;
        if (userID == null) {
            downloadInfoList = mSQLOperator.getAllDownloadInfo();
        } else {
            downloadInfoList = mSQLOperator.getUserDownloadInfo(userID);
        }
        if (downloadInfoList.size() > 0) {
            int listSize = downloadInfoList.size();
            for (int i = 0; i < listSize; i++) {
                AppItemInfo downloadInfo = downloadInfoList.get(i);
                Downloader sqlDownloader = new Downloader(
                        context, downloadInfo, mPool, userID, mIsSupportFTP, false, true);
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

    public void addTask(String taskID, String url,
                        String fileName, String packageName, String iconUrl, boolean needUI) {
        if (taskID == null) {
            taskID = fileName;
        }

        AppItemInfo downloadInfoByPkgName = mSQLOperator.getDownloadInfoByPkgName(packageName);
        if (downloadInfoByPkgName != null) {
            mSQLOperator.deleteDownloadInfo(mUserID, taskID);
            deleteTask(taskID);
        }

        AppItemInfo downloadinfo = new AppItemInfo();
        downloadinfo.setUserID(mUserID);
        downloadinfo.setDownFileSize(0);
        downloadinfo.setFileSize(0);
        downloadinfo.setTaskId(packageName);
        downloadinfo.setFileName(fileName);
        downloadinfo.setUrl(url);
        downloadinfo.setPackageName(packageName);
        downloadinfo.setIconUrl(iconUrl);
        downloadinfo.setFilePath(FileHelper.getDownloadUrlPath(url));

        Downloader taskDownloader = new Downloader(
                mContext, downloadinfo, mPool, mUserID, mIsSupportFTP, true, needUI);
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

    public ArrayList<AppItemInfo> getAllInfo() {
        ArrayList<AppItemInfo> appInfolist = new ArrayList<>();
        for (int i = 0; i < mTaskList.size(); i++) {
            Downloader deletedownloader = mTaskList.get(i);
            AppItemInfo sqldownloadinfo = deletedownloader.getDownloadInfo();
            AppItemInfo appInfo = new AppItemInfo();
            appInfo.setFileName(sqldownloadinfo.getFileName());
            appInfo.setOnDownloading(deletedownloader.isDownloading());
            appInfo.setTaskId(sqldownloadinfo.getTaskId());
            appInfo.setFileSize(sqldownloadinfo.getFileSize());
            appInfo.setDownFileSize(sqldownloadinfo.getDownFileSize());
            appInfo.setIconUrl(sqldownloadinfo.getIconUrl());
            appInfo.setFilePath(sqldownloadinfo.getFilePath());
            appInfo.setPackageName(sqldownloadinfo.getPackageName());
            appInfo.setDownloadUrl(sqldownloadinfo.getUrl());
            appInfolist.add(appInfo);
        }
        return appInfolist;
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
            AppItemInfo downloadInfo = deletedownloader.getDownloadInfo();
            if (downloadInfo != null && downloadInfo.getFileSize() != 0 &&
                    downloadInfo.getFileSize() != downloadInfo.getDownFileSize()) {
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
