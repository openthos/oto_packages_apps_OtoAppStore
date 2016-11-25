package com.openthos.appstore.utils.download;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.openthos.appstore.MainActivity;
import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.SQLDownLoadInfo;
import com.openthos.appstore.utils.AppUtils;
import com.openthos.appstore.utils.SDCardUtils;
import com.openthos.appstore.utils.Tools;
import com.openthos.appstore.utils.sql.DownloadKeeper;
import com.openthos.appstore.utils.FileHelper;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
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

    private final String TEMP_FILEPATH = FileHelper.getTempDirPath();

    private boolean mIsSupportBreakpoint = false;
    private Context mContext;
    private String mUserID;
    private DownloadKeeper mDatakeeper;
    private HashMap<String, DownLoadListener> mListenerMap;
    private DownLoadSuccess mDownLoadSuccess;
    private SQLDownLoadInfo mSQLDownLoadInfo;
    private DownLoadThread mDownLoadThread;
    private long mFileSize = 0;
    private long mDownFileSize = 0;
    private int mDownloadtimes = 0;
    private int mMaxdownloadtimes = 1;
    private boolean mOndownload = false;
    private ThreadPoolExecutor mPool;

    private static final int HTTPURL_CONNECT_TIMEOUT = 5 * 1000;
    private static final int HTTPURL_READ_TIMEOUT = 5 * 1000;
    private static final int BUFFREAD_BYTE_LENGTH = 512 * 1024;

    public DownLoader(Context context, SQLDownLoadInfo sqlFileInfo, ThreadPoolExecutor pool,
                      String userID, boolean isSupportBreakpoint, boolean isNewTask) {
        mContext = context;
        mIsSupportBreakpoint = isSupportBreakpoint;
        mPool = pool;
        mUserID = userID;
        mFileSize = sqlFileInfo.getFileSize();
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
        if (mDownLoadThread == null) {
            mDownloadtimes = 0;
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
        File downloadFile = new File(TEMP_FILEPATH + "/" + mSQLDownLoadInfo.getFileName());
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
        private boolean isdownloading;
        private URL url;
        private RandomAccessFile localFile;
        private HttpURLConnection urlConn;
        private InputStream inputStream;
        private int progress = -1;

        public DownLoadThread() {
            isdownloading = true;
        }

        @Override
        public void run() {
            new HttpUtils().download(mSQLDownLoadInfo.getUrl(),
                    FileHelper.getTempFilePath(AppUtils.getAppName(mSQLDownLoadInfo.getUrl())),
                    false,
                    true,
                    new RequestCallBack<File>() {
                        @Override
                        public void onSuccess(ResponseInfo<File> responseInfo) {
                            handler.sendEmptyMessage(TASK_SUCCESS);
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
                    }
            );
//            while (mDownloadtimes < mMaxdownloadtimes) {
//                try {
//                    if (mDownFileSize == mFileSize && mFileSize > 0) {
//                        mOndownload = false;
//                        Message msg = new Message();
//                        msg.what = TASK_PROGESS;
//                        msg.arg1 = 100;
//                        handler.sendMessage(msg);
//                        mDownloadtimes = mMaxdownloadtimes;
//                        mDownLoadThread = null;
//                        return;
//                    }
//                    url = new URL(mSQLDownLoadInfo.getUrl());
//                    urlConn = (HttpURLConnection) url.openConnection();
//                    urlConn.setDoOutput(true);
//                    urlConn.setDoInput(true);
//                    urlConn.setConnectTimeout(HTTPURL_CONNECT_TIMEOUT);
//                    urlConn.setReadTimeout(HTTPURL_READ_TIMEOUT);
//                    if (mFileSize < 1) {
//                        if (!openConnention()) {
//                            return;
//                        }
//                    } else {
//                        if (new File(TEMP_FILEPATH + "/" +
//                                mSQLDownLoadInfo.getFileName()).exists()) {
//                            localFile = new RandomAccessFile(TEMP_FILEPATH + "/" +
//                                    mSQLDownLoadInfo.getFileName(), "rwd");
//                            localFile.seek(mDownFileSize);
//                            urlConn.setRequestProperty("Range", "bytes=" + mDownFileSize + "-");
//                        } else {
//                            mFileSize = 0;
//                            mDownFileSize = 0;
//                            saveDownloadInfo();
//                            if (!openConnention()) {
//                                return;
//                            }
//                        }
//                    }
//                    inputStream = urlConn.getInputStream();
//                    byte[] buffer = new byte[BUFFREAD_BYTE_LENGTH];
//                    int length = -1;
//                    while ((length = inputStream.read(buffer)) != -1 && isdownloading) {
////                        localFile.write(buffer, 0, length);
//                        mDownFileSize += length;
//                        int nowProgress = (int) ((100 * mDownFileSize) / mFileSize);
//                        if (nowProgress > progress) {
//                            progress = nowProgress;
//                            handler.sendEmptyMessage(TASK_PROGESS);
//                        }
//                    }
//                    if (mDownFileSize == mFileSize) {
//                        boolean renameResult = RenameFile();
//                        if (renameResult) {
//                            handler.sendEmptyMessage(TASK_SUCCESS);
//                            saveDownloadInfo();
//                        } else {
//                            new File(TEMP_FILEPATH + "/" +
//                                    mSQLDownLoadInfo.getFileName()).delete();
//                            //handler.sendEmptyMessage(TASK_ERROR);
//                            Message msg = handler.obtainMessage();
//                            msg.what = TASK_ERROR;
//                            msg.obj = mContext.getString(R.string.remove_file_error);
//                            handler.sendMessage(msg);
//                        }
////                        mDatakeeper.deleteDownLoadInfo(mUserID, mSQLDownLoadInfo.getTaskID());
//                        mDownLoadThread = null;
//                        mOndownload = false;
//                    }
//                    mDownloadtimes = mMaxdownloadtimes;
//                } catch (Exception e) {
//                    if (isdownloading) {
//                        if (mIsSupportBreakpoint) {
//                            mDownloadtimes++;
//                            if (mDownloadtimes >= mMaxdownloadtimes) {
//                                if (mFileSize > 0) {
//                                    saveDownloadInfo();
//                                }
//                                mPool.remove (mDownLoadThread);
//                                mDownLoadThread = null;
//                                mOndownload = false;
//                                //  handler.sendEmptyMessage(TASK_ERROR);
//                                Message msg = handler.obtainMessage();
//                                msg.what = TASK_ERROR;
//                                msg.obj = e.toString();
//                                handler.sendMessage(msg);
//                            }
//                        } else {
//                            mDownFileSize = 0;
//                            mDownloadtimes = mMaxdownloadtimes;
//                            mOndownload = false;
//                            mDownLoadThread = null;
//                            //    handler.sendEmptyMessage(TASK_ERROR);
//                            Message msg = handler.obtainMessage();
//                            msg.obj = e.toString();
//                            msg.what = TASK_ERROR;
//                            handler.sendMessage(msg);
//                        }
//
//                    } else {
//                        mDownloadtimes = mMaxdownloadtimes;
//                    }
//                    e.printStackTrace();
//                } finally {
////                    Tools.closeStream(inputStream, localFile);
////                    try {
////                        if (urlConn != null) {
////                            urlConn.disconnect();
////                        }
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
//                }
//            }
        }

        public void stopDownLoad() {
            isdownloading = false;
            mDownloadtimes = mMaxdownloadtimes;
            if (mFileSize > 0) {
                saveDownloadInfo();
            }
            handler.sendEmptyMessage(TASK_STOP);
        }

        private boolean openConnention() throws Exception {
            long urlfilesize = urlConn.getContentLength();
            if (urlConn.getContentLength() > SDCardUtils.getFreeBytes(TEMP_FILEPATH)) {
                Message message = MainActivity.mHandler.obtainMessage();
                message.what = Constants.TOAST;
                message.obj = mContext.getString(R.string.no_space_avilable);
                MainActivity.mHandler.sendMessage(message);
                return false;
            }
            if (urlfilesize > 0) {
                FileHelper.creatDirFile(TEMP_FILEPATH);
                String fileName = TEMP_FILEPATH + "/" + mSQLDownLoadInfo.getFileName();
                if (FileHelper.creatFile(fileName)) {
                    localFile = new RandomAccessFile(fileName, "rwd");
                    localFile.setLength(urlfilesize);
                    mSQLDownLoadInfo.setFileSize(urlfilesize);
                    mFileSize = urlfilesize;
                    if (isdownloading) {
                        saveDownloadInfo();
                    }
                    return true;
                } else {
                    return false;
                }
            }
            return true;
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

    public boolean RenameFile() {
        File newfile = new File(mSQLDownLoadInfo.getFilePath());
        if (newfile.exists()) {
            newfile.delete();
        }
        File olefile = new File(TEMP_FILEPATH + "/" + mSQLDownLoadInfo.getFileName());

        String filepath = mSQLDownLoadInfo.getFilePath();
        filepath = filepath.substring(0, filepath.lastIndexOf("/"));
        File file = new File(filepath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return olefile.renameTo(newfile);
    }
}
