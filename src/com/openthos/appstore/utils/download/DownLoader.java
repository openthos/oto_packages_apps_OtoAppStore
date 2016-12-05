package com.openthos.appstore.utils.download;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.openthos.appstore.bean.SQLDownLoadInfo;
import com.openthos.appstore.utils.AppUtils;
import com.openthos.appstore.utils.Tools;
import com.openthos.appstore.utils.sql.DownloadKeeper;
import com.openthos.appstore.utils.FileHelper;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ThreadPoolExecutor;

public class DownLoader {
    private int TASK_START = 0;
    private int TASK_STOP = 1;
    private int TASK_PROGESS = 2;
    private int TASK_ERROR = 3;
    private int TASK_SUCCESS = 4;

    private boolean mIsSupportBreakpoint = false;
    private Context mContext;
    private String mUserID;
    private DownloadKeeper mDatakeeper;
    private HashMap<String, DownLoadListener> mListenerMap;
    private DownLoadSuccess mDownLoadSuccess;
    private SQLDownLoadInfo mSQLDownLoadInfo;
    private DownLoadThread mDownLoadThread;
    private long mDownFileSize = 0;
    private boolean mOndownload = false;
    private ThreadPoolExecutor mPool;

    public DownLoader(Context context, SQLDownLoadInfo sqlFileInfo, ThreadPoolExecutor pool,
                      String userID, boolean isSupportBreakpoint, boolean isNewTask) {
        mContext = context;
        mIsSupportBreakpoint = isSupportBreakpoint;
        mPool = pool;
        mUserID = userID;
        mDownFileSize = sqlFileInfo.getDownloadSize();
        mDatakeeper = new DownloadKeeper(context);
        mListenerMap = new HashMap<String, DownLoadListener>();
        mSQLDownLoadInfo = sqlFileInfo;
        if (isNewTask) {
            saveDownloadInfo();
        }
    }

    public String getTaskID() {
        return mSQLDownLoadInfo.getTaskID();
    }

    public void start() {
        FileHelper.deleteFile(mSQLDownLoadInfo.getFileName());
        if (mDownLoadThread == null) {
            mOndownload = true;
            handler.sendEmptyMessage(TASK_START);
            mDownLoadThread = new DownLoadThread();
            mPool.execute(mDownLoadThread);
        } else {
            mPool.execute(mDownLoadThread);
        }
    }

    public void stop() {
        if (mDownLoadThread != null) {
            mOndownload = false;
            mDownLoadThread.stopDownLoad();
            mPool.remove(mDownLoadThread);
            mDownLoadThread = null;
        }
    }

    public void setDownLoadListener(String key, DownLoadListener listener) {
        if (listener == null) {
            removeDownLoadListener(key);
        } else {
            mListenerMap.put(key, listener);
        }
    }

    public void removeDownLoadListener(String key) {
        if (mListenerMap.containsKey(key)) {
            mListenerMap.remove(key);
        }
    }

    public void setDownLodSuccesslistener(DownLoadSuccess downloadsuccess) {
        mDownLoadSuccess = downloadsuccess;
    }

    public void destroy() {
        saveDownloadInfo();//last add
        if (mDownLoadThread != null) {
            mDownLoadThread.stopDownLoad();
            mDownLoadThread = null;
        }
//        mDatakeeper.deleteDownLoadInfo(mUserID, mSQLDownLoadInfo.getTaskID());
        File downloadFile = new File(FileHelper.getDefaultFileFromUrl(mSQLDownLoadInfo.getUrl()));
        if (downloadFile.exists()) {
            downloadFile.delete();
        }
    }

    public boolean isDownLoading() {
        return mOndownload;
    }

    public SQLDownLoadInfo getSQLDownLoadInfo() {
        mSQLDownLoadInfo.setDownloadSize(mDownFileSize);
        return mSQLDownLoadInfo;
    }

    public void setSupportBreakpoint(boolean isSupportBreakpoint) {
        mIsSupportBreakpoint = isSupportBreakpoint;
    }

    class DownLoadThread extends Thread {
        private int progress = -1;
        private HttpHandler<File> httpHandler;

        public DownLoadThread() {

        }

        @Override
        public void run() {
            httpHandler = new HttpUtils().download(mSQLDownLoadInfo.getUrl(),
                    FileHelper.getDefaultFileFromUrl(mSQLDownLoadInfo.getUrl()),
                    true,
                    true,
                    new RequestCallBack<File>() {
                        @Override
                        public void onSuccess(ResponseInfo<File> responseInfo) {
                            handler.sendEmptyMessage(TASK_SUCCESS);
                            AppUtils.installApk(mContext, mSQLDownLoadInfo.getFilePath());
                            saveDownloadInfo();
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            Message msg = handler.obtainMessage();
                            msg.what = TASK_ERROR;
                            msg.obj = s;
                            handler.sendMessage(msg);
                            saveDownloadInfo();
                        }

                        @Override
                        public void onLoading(long total, long current, boolean isUploading) {
                            super.onLoading(total, current, isUploading);
                            Tools.printLog("DL", "total " + total + "current " + current);
                            mDownFileSize = current;
                            mSQLDownLoadInfo.setFileSize(total);
                            mSQLDownLoadInfo.setDownloadSize(current);
                            int nowProgress = (int) ((100 * current) / total);
                            if (nowProgress > progress) {
                                progress = nowProgress;
                                handler.sendEmptyMessage(TASK_PROGESS);
                            }
                        }

                        @Override
                        public void onStart() {
                            super.onStart();
                            saveDownloadInfo();
                        }
                    }
            );
        }

        public void stopDownLoad() {
            if (httpHandler != null) {
                httpHandler.cancel();
                httpHandler = null;
            }
            handler.sendEmptyMessage(TASK_STOP);
        }
    }

    private void saveDownloadInfo() {
        if (mIsSupportBreakpoint) {
            mSQLDownLoadInfo.setDownloadSize(mDownFileSize);
            mDatakeeper.saveDownLoadInfo(mSQLDownLoadInfo);
        }
    }

    private void startNotice() {
        if (!mListenerMap.isEmpty()) {
            Collection<DownLoadListener> c = mListenerMap.values();
            Iterator<DownLoadListener> it = c.iterator();
            while (it.hasNext()) {
                DownLoadListener listener = (DownLoadListener) it.next();
                listener.onStart(getSQLDownLoadInfo());
            }
        }
    }

    private void onProgressNotice() {
        if (!mListenerMap.isEmpty()) {
            Collection<DownLoadListener> c = mListenerMap.values();
            Iterator<DownLoadListener> it = c.iterator();
            while (it.hasNext()) {
                DownLoadListener listener = (DownLoadListener) it.next();
                listener.onProgress(getSQLDownLoadInfo(), mIsSupportBreakpoint);
            }
        }
    }

    private void stopNotice() {
        if (!mIsSupportBreakpoint) {
            mDownFileSize = 0;
        }
        if (!mListenerMap.isEmpty()) {
            Collection<DownLoadListener> c = mListenerMap.values();
            Iterator<DownLoadListener> it = c.iterator();
            while (it.hasNext()) {
                DownLoadListener listener = (DownLoadListener) it.next();
                listener.onStop(getSQLDownLoadInfo(), mIsSupportBreakpoint);
            }
        }
    }

    private void errorNotice(String error) {
        if (!mListenerMap.isEmpty()) {
            Collection<DownLoadListener> c = mListenerMap.values();
            Iterator<DownLoadListener> it = c.iterator();
            while (it.hasNext()) {
                DownLoadListener listener = (DownLoadListener) it.next();
                listener.onError(getSQLDownLoadInfo(), error);
            }
        }
    }

    private void successNotice() {
        if (!mListenerMap.isEmpty()) {
            Collection<DownLoadListener> c = mListenerMap.values();
            Iterator<DownLoadListener> it = c.iterator();
            while (it.hasNext()) {
                DownLoadListener listener = (DownLoadListener) it.next();
                listener.onSuccess(getSQLDownLoadInfo());
            }
        }
        if (mDownLoadSuccess != null) {
            mDownLoadSuccess.onTaskSeccess(mSQLDownLoadInfo.getTaskID());
        }
    }

    public interface DownLoadSuccess {
        public void onTaskSeccess(String TaskID);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (msg.what == TASK_START) {
                startNotice();
            } else if (msg.what == TASK_STOP) {
                stopNotice();
            } else if (msg.what == TASK_PROGESS) {
                onProgressNotice();
            } else if (msg.what == TASK_ERROR) {
                errorNotice((String) msg.obj);
            } else if (msg.what == TASK_SUCCESS) {
                successNotice();
            }
        }
    };
}