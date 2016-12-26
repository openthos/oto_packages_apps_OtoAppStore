package com.openthos.appstore.utils.download;

import android.content.Context;
import android.content.SharedPreferences;

import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.SQLDownLoadInfo;
import com.openthos.appstore.bean.TaskInfo;
import com.openthos.appstore.utils.sql.DownloadKeeper;
import com.openthos.appstore.utils.FileHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownLoadManager {
    private Context mContext;

    private ArrayList<DownLoader> mTaskList = new ArrayList<DownLoader>();

    private final int MAX_DOWNLOADING_TASK = 5;

    private DownLoader.DownLoadSuccess mDownloadsuccessListener = null;


    private boolean mIsSupportBreakpoint = false;

    private ThreadPoolExecutor mPool;

    private String mUserID = Constants.USER_ID;

    private SharedPreferences mSharedPreferences;

    private DownLoadListener mAlltasklistener;

    private static final int FILE_EXIT = -1;
    private static final int TASK_EXIT = 0;
    private static final int ADD_TASK = 1;
    private static int mCount = 0;

    public DownLoadManager(Context context) {
        mContext = context;
        init(context);
    }

    private void init(Context context) {
        mPool = new ThreadPoolExecutor(
                MAX_DOWNLOADING_TASK, MAX_DOWNLOADING_TASK, 30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(2000));

        mDownloadsuccessListener = new DownLoader.DownLoadSuccess() {
            @Override
            public void onTaskSeccess(String taskID) {
                int taskSize = mTaskList.size();
                for (int i = 0; i < taskSize; i++) {
                    DownLoader deletedownloader = mTaskList.get(i);
                    if (deletedownloader.getTaskID().equals(taskID)) {
                        //                    mTaskList.remove(deletedownloader);
//                        MainActivity.mDownloadStateMap.put(
//                                taskID, Constants.APP_DOWNLOAD_FINISHED);
                        return;
                    }
                }
            }
        };
        mSharedPreferences = mContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        mUserID = mSharedPreferences.getString("UserID", Constants.USER_ID);
        recoverData(mContext, mUserID);
    }

    private void recoverData(Context context, String userID) {
        stopAllTask();
        mTaskList = new ArrayList<DownLoader>();
        DownloadKeeper datakeeper = new DownloadKeeper(context);
        ArrayList<SQLDownLoadInfo> sqlDownloadInfoList = null;
        if (userID == null) {
            sqlDownloadInfoList = datakeeper.getAllDownLoadInfo();
        } else {
            sqlDownloadInfoList = datakeeper.getUserDownLoadInfo(userID);
        }
        if (sqlDownloadInfoList.size() > 0) {
            int listSize = sqlDownloadInfoList.size();
            for (int i = 0; i < listSize; i++) {
                SQLDownLoadInfo sqlDownLoadInfo = sqlDownloadInfoList.get(i);
                DownLoader sqlDownLoader = new DownLoader(
                        context, sqlDownLoadInfo, mPool, userID, mIsSupportBreakpoint, false);
                sqlDownLoader.setDownLodSuccesslistener(mDownloadsuccessListener);
                sqlDownLoader.setDownLoadListener("public", mAlltasklistener);
                mTaskList.add(sqlDownLoader);
            }
        }
    }

    public void setSupportBreakpoint(boolean isSupportBreakpoint) {
        if ((!mIsSupportBreakpoint) && isSupportBreakpoint) {
            int taskSize = mTaskList.size();
            for (int i = 0; i < taskSize; i++) {
                DownLoader downloader = mTaskList.get(i);
                downloader.setSupportBreakpoint(true);
            }
        }
        mIsSupportBreakpoint = isSupportBreakpoint;
    }

    public void changeUser(String userID) {
        mUserID = userID;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("UserID", userID);
        editor.commit();
        FileHelper.setUserID(userID);
        recoverData(mContext, userID);
    }

    public String getUserID() {
        return mUserID;
    }

    public int addTask(String taskID, String url, String fileName, String packageName) {
        return addTask(taskID, url, fileName, packageName, null);
    }

    public int addTask(String taskID, String url,
                       String fileName, String packageName, String filepath) {
        if (taskID == null) {
            taskID = fileName;
        }
        int state = getAttachmentState(taskID, fileName, filepath);
        if (state != ADD_TASK) {
//            return state;
            FileHelper.deleteFile(fileName);
        }

        SQLDownLoadInfo downloadinfo = new SQLDownLoadInfo();
        downloadinfo.setUserID(mUserID);
        downloadinfo.setDownloadSize(0);
        downloadinfo.setFileSize(0);
        downloadinfo.setTaskID(taskID);
        downloadinfo.setFileName(fileName);
        downloadinfo.setUrl(url);
        downloadinfo.setPackageName(packageName);
        if (filepath == null) {
            downloadinfo.setFilePath(FileHelper.getDefaultFile(fileName));
        } else {
            downloadinfo.setFilePath(filepath);
        }
        DownLoader taskDownLoader = new DownLoader(
                mContext, downloadinfo, mPool, mUserID, mIsSupportBreakpoint, true);
        taskDownLoader.setDownLodSuccesslistener(mDownloadsuccessListener);
        if (mIsSupportBreakpoint) {
            taskDownLoader.setSupportBreakpoint(true);
        } else {
            taskDownLoader.setSupportBreakpoint(false);
        }
        taskDownLoader.start();
//        taskDownLoader.setDownLoadListener("" + (mCount++), adapter.getDownLoadListener());
        mTaskList.add(0, taskDownLoader);
        return 1;
    }

    private int getAttachmentState(String taskID, String fileName, String filepath) {
        for (int i = 0; i < mTaskList.size(); i++) {
            DownLoader downloader = mTaskList.get(i);
            if (downloader.getTaskID().equals(taskID)) {
                return TASK_EXIT;
            }
        }
        File file = null;
        if (filepath == null) {
            file = new File(FileHelper.getDefaultFile(fileName));
            if (file.exists() && file.length() != 0) {
                return FILE_EXIT;
            }
        } else {
            file = new File(filepath);
            if (file.exists() && file.length() != 0) {
                return FILE_EXIT;
            }
        }
        return ADD_TASK;
    }

    public void deleteTask(String taskID) {
        for (int i = 0; i < mTaskList.size(); i++) {
            DownLoader deletedownloader = mTaskList.get(i);
            if (deletedownloader.getTaskID().equals(taskID)) {
                deletedownloader.destroy();
                mTaskList.remove(deletedownloader);
                break;
            }
        }
    }

    public ArrayList<String> getAllTaskID() {
        ArrayList<String> taskIDlist = new ArrayList<String>();
        for (int i = 0; i < mTaskList.size(); i++) {
            DownLoader deletedownloader = mTaskList.get(i);
            taskIDlist.add(deletedownloader.getTaskID());
        }
        return taskIDlist;
    }

    public ArrayList<TaskInfo> getAllTask() {
        ArrayList<TaskInfo> taskInfolist = new ArrayList<TaskInfo>();
        for (int i = 0; i < mTaskList.size(); i++) {
            DownLoader deletedownloader = mTaskList.get(i);
            SQLDownLoadInfo sqldownloadinfo = deletedownloader.getSQLDownLoadInfo();
            TaskInfo taskinfo = new TaskInfo();
            taskinfo.setFileName(sqldownloadinfo.getFileName());
            taskinfo.setOnDownloading(deletedownloader.isDownLoading());
            taskinfo.setTaskID(sqldownloadinfo.getTaskID());
            taskinfo.setFileSize(sqldownloadinfo.getFileSize());
            taskinfo.setDownFileSize(sqldownloadinfo.getDownloadSize());
            taskInfolist.add(taskinfo);
        }
        return taskInfolist;
    }

    public void startTask(String taskID) {
        for (int i = 0; i < mTaskList.size(); i++) {
            DownLoader deletedownloader = mTaskList.get(i);
            if (deletedownloader.getTaskID().equals(taskID)) {
                deletedownloader.start();
                break;
            }
        }
    }

    public void stopTask(String taskID) {
        for (int i = 0; i < mTaskList.size(); i++) {
            DownLoader deletedownloader = mTaskList.get(i);
            if (deletedownloader.getTaskID().equals(taskID)) {
                deletedownloader.stop();
                break;
            }
        }
    }

    public void startAllTask() {
        for (int i = 0; i < mTaskList.size(); i++) {
            DownLoader deletedownloader = mTaskList.get(i);
            SQLDownLoadInfo sqlDownLoadInfo = deletedownloader.getSQLDownLoadInfo();
            if (sqlDownLoadInfo != null && sqlDownLoadInfo.getFileSize() != 0 &&
                    sqlDownLoadInfo.getFileSize() != sqlDownLoadInfo.getDownloadSize()) {
                deletedownloader.start();
            }
        }
    }

    public void stopAllTask() {
        for (int i = 0; i < mTaskList.size(); i++) {
            DownLoader deletedownloader = mTaskList.get(i);
            deletedownloader.stop();
        }
    }

    public void setSingleTaskListener(String taskID, DownLoadListener listener) {
        for (int i = 0; i < mTaskList.size(); i++) {
            DownLoader deletedownloader = mTaskList.get(i);
            if (deletedownloader.getTaskID().equals(taskID)) {
                deletedownloader.setDownLoadListener("private", listener);
                break;
            }
        }
    }

    public void setAllTaskListener(DownLoadListener listener) {
        mAlltasklistener = listener;
        DownLoader.setDownLoadListener("" + (++mCount), listener);
//        for (int i = 0; i < mTaskList.size(); i++) {
//            DownLoader deletedownloader = mTaskList.get(i);
//            deletedownloader.setDownLoadListener("public", listener);
//        }
    }

    public void removeDownLoadListener(String taskID) {
        DownLoader downLoader = getDownloader(taskID);
        if (downLoader != null) {
            downLoader.removeDownLoadListener("private");
        }
    }

    public void removeAllDownLoadListener() {
        for (int i = 0; i < mTaskList.size(); i++) {
            DownLoader deletedownloader = mTaskList.get(i);
            deletedownloader.removeDownLoadListener("public");
        }
    }

    public boolean isTaskdownloading(String taskID) {
        DownLoader downLoader = getDownloader(taskID);
        if (downLoader != null) {
            return downLoader.isDownLoading();
        }
        return false;
    }

    private DownLoader getDownloader(String taskID) {
        for (int i = 0; i < mTaskList.size(); i++) {
            DownLoader downloader = mTaskList.get(i);
            if (taskID != null && taskID.equals(downloader.getTaskID())) {
                return downloader;
            }
        }
        return null;
    }

    public TaskInfo getTaskInfo(String taskID) {
        DownLoader downloader = getDownloader(taskID);
        if (downloader == null) {
            return null;
        }
        SQLDownLoadInfo sqldownloadinfo = downloader.getSQLDownLoadInfo();
        if (sqldownloadinfo == null) {
            return null;
        }
        TaskInfo taskinfo = new TaskInfo();
        taskinfo.setFileName(sqldownloadinfo.getFileName());
        taskinfo.setOnDownloading(downloader.isDownLoading());
        taskinfo.setTaskID(sqldownloadinfo.getTaskID());
        taskinfo.setDownFileSize(sqldownloadinfo.getDownloadSize());
        taskinfo.setFileSize(sqldownloadinfo.getFileSize());
        taskinfo.setPackageName(sqldownloadinfo.getPackageName());
        return taskinfo;
    }
}
