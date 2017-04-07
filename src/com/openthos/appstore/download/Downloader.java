package com.openthos.appstore.download;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.DownloadInfo;
import com.openthos.appstore.utils.FileHelper;
import com.openthos.appstore.utils.SPUtils;
import com.openthos.appstore.utils.Tools;
import com.openthos.appstore.utils.SQLOperator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

public class Downloader {
    private static final int TASK_START = 0;
    private static final int TASK_STOP = 1;
    private static final int TASK_PROGESS = 2;
    private static final int TASK_ERROR = 3;
    private static final int TASK_SUCCESS = 4;

    private static final int HTTP_CONNECT_TIME_OUT = 5000;
    private static final int HTTP_READ_TIME_OUT = 5000;
    private static final int BUFFER_READ_BYTE = 512 * 1024;

    private static final int MAX_DOWNLOAD_TIMES = 3;

    private static Map<String, DownloadListener> mListenerMap = new HashMap<>();
    private boolean mIsSupportFTP = false;
    private boolean mOndownload;
    private int mDownloadtimes;
    private long mFileSize;
    private long mDownFileSize;
    private String mUserID;
    private Context mContext;
    private SQLOperator mDatakeeper;
    private DownloadInfo mDownloadInfo;
    private DownloadThread mDownloadThread;
    private ThreadPoolExecutor mPool;

    public Downloader(Context context, DownloadInfo sqlFileInfo, ThreadPoolExecutor pool,
                      String userID, boolean isSupportFTP, boolean isNewTask) {
        mContext = context;
        mIsSupportFTP = isSupportFTP;
        mPool = pool;
        mUserID = userID;
        mFileSize = sqlFileInfo.getFileSize();
        mDownFileSize = sqlFileInfo.getDownloadSize();
        mDatakeeper = new SQLOperator(context);
        mDownloadInfo = sqlFileInfo;
        mOndownload = false;
        if (isNewTask) {
            saveDownloadInfo();
        }
    }

    public String getTaskID() {
        return mDownloadInfo.getTaskID();
    }

    public void start() {
        if (mDownloadThread == null) {
            mDownloadtimes = 0;
            mOndownload = true;
            mHandler.sendEmptyMessage(TASK_START);
            mDownloadThread = new DownloadThread();
            mPool.execute(mDownloadThread);
        }
    }

    public void stop() {
        if (mDownloadThread != null) {
            mOndownload = false;
            mDownloadThread.stopDownload();
            mPool.remove(mDownloadThread);
            mDownloadThread = null;
        }
    }

    public static void setDownloadListener(String key, DownloadListener listener) {
        if (listener == null) {
            removeDownloadListener(key);
        } else {
            mListenerMap.put(key, listener);
        }
    }

    public static void removeDownloadListener(String key) {
        if (mListenerMap.containsKey(key)) {
            mListenerMap.remove(key);
        }
    }

    public void destroy() {
        if (mDownloadThread != null) {
            mDownloadThread.stopDownload();
            mDownloadThread = null;
        }
        mDatakeeper.deleteDownloadInfo(mUserID, mDownloadInfo.getTaskID());
        File downloadFile = FileHelper.getDownloadFile(mDownloadInfo.getFilePath());
        if (downloadFile.exists()) {
            downloadFile.delete();
        }
    }

    public boolean isDownloading() {
        return mOndownload;
    }

    public DownloadInfo getDownloadInfo() {
        mDownloadInfo.setDownloadSize(mDownFileSize);
        return mDownloadInfo;
    }

    public void setSupportFTP(boolean isSupportFTP) {
        mIsSupportFTP = isSupportFTP;
    }

    class DownloadThread extends Thread {
        private boolean isdownloading;
        private File tempFile;
        private URL url;
        private HttpURLConnection urlConn;
        private InputStream inputStream;
        private int progress = -1;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;

        public DownloadThread() {
            isdownloading = true;
            tempFile = FileHelper.getDownloadTempFile(mDownloadInfo.getFilePath());
        }

        @Override
        public void run() {
            while (mDownloadtimes < MAX_DOWNLOAD_TIMES) {

                try {
                    if (mDownFileSize == mFileSize
                            && mFileSize > 0) {
                        mOndownload = false;
                        mHandler.sendEmptyMessage(TASK_SUCCESS);
                        mDownloadtimes = MAX_DOWNLOAD_TIMES;
                        mDownloadThread = null;
                        return;
                    }
                    url = new URL(mDownloadInfo.getUrl());
                    urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.setConnectTimeout(HTTP_CONNECT_TIME_OUT);
                    urlConn.setReadTimeout(HTTP_READ_TIME_OUT);
                    if (mFileSize < 1) {
                        openConnention();
                    } else {
                        if (tempFile.exists()) {
                            mDownFileSize = tempFile.length();
                            urlConn.setRequestProperty("Range", "bytes=" + mDownFileSize + "-");
                            urlConn.setUseCaches(false);
                        } else {
                            mFileSize = 0;
                            mDownFileSize = 0;
                            saveDownloadInfo();
                            openConnention();
                        }
                    }
                    inputStream = urlConn.getInputStream();
                    bis = new BufferedInputStream(inputStream);

                    bos = new BufferedOutputStream(new FileOutputStream(tempFile, true));
                    byte[] buffer = new byte[BUFFER_READ_BYTE];
                    int length = -1;
                    long timeMillis = System.currentTimeMillis();
                    long downloadSize = mDownFileSize;
                    mFileSize = urlConn.getContentLength() + downloadSize;
                    mDownloadInfo.setFileSize(mFileSize);
                    saveDownloadInfo();

                    while ((length = bis.read(buffer)) != -1 && isdownloading) {
                        bos.write(buffer, 0, length);
                        bos.flush();
                        mDownFileSize += length;
                        long currentTimeMillis = System.currentTimeMillis();
                        int nowProgress = (int) ((100 * mDownFileSize) / mFileSize);
                        if (nowProgress > progress && currentTimeMillis - timeMillis > 1000) {
                            progress = nowProgress;
                            long speed = (mDownFileSize - downloadSize) /
                                    (currentTimeMillis - timeMillis);
                            downloadSize = mDownFileSize;
                            mDownloadInfo.setSpeed(speed);
                            mDownloadInfo.setDownloadSize(downloadSize);
                            timeMillis = currentTimeMillis;
                            mHandler.sendEmptyMessage(TASK_PROGESS);
                        }
                    }
                    if (mDownFileSize == mFileSize) {
                        boolean renameResult = RenameFile();
                        if (renameResult) {
                            mDownloadInfo.setDownloadSize(mFileSize);
                            saveDownloadInfo();
                            mHandler.sendEmptyMessage(TASK_SUCCESS);
                        } else {
                            FileHelper.deleteFile(tempFile);
                            mHandler.sendEmptyMessage(TASK_ERROR);
                        }

                        mDownloadThread = null;
                        mOndownload = false;
                    }
                    mDownloadtimes = MAX_DOWNLOAD_TIMES;
                } catch (Exception e) {
                    if (isdownloading) {
                        if (mIsSupportFTP) {
                            mDownloadtimes++;
                            if (mDownloadtimes >= MAX_DOWNLOAD_TIMES) {
                                if (mFileSize > 0) {
                                    saveDownloadInfo();
                                }
                                mPool.remove(mDownloadThread);
                                mDownloadThread = null;
                                mOndownload = false;
                                mHandler.sendMessage(
                                        mHandler.obtainMessage(TASK_ERROR,e.toString()));
                            }
                        } else {
                            mDownFileSize = 0;
                            mDownloadtimes = MAX_DOWNLOAD_TIMES;
                            mOndownload = false;
                            mDownloadThread = null;
                            mHandler.sendMessage(mHandler.obtainMessage(TASK_ERROR,e.toString()));
                        }

                    } else {
                        mDownloadtimes = MAX_DOWNLOAD_TIMES;
                    }
                    e.printStackTrace();
                } finally {
                    try {
                        if (urlConn != null) {
                            urlConn.disconnect();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Tools.closeStream(bis, bos);
                }
            }
        }

        public void stopDownload() {
            isdownloading = false;
            mDownloadtimes = MAX_DOWNLOAD_TIMES;
            if (mFileSize > 0) {
                saveDownloadInfo();
            }
            mHandler.sendEmptyMessage(TASK_STOP);
        }

        private void openConnention() throws Exception {
            long urlfilesize = urlConn.getContentLength();
            if (urlfilesize > 0) {
                FileHelper.creatFile(tempFile);
                mDownloadInfo.setFileSize(urlfilesize);
                mFileSize = urlfilesize;
                if (isdownloading) {
                    saveDownloadInfo();
                }
            }
        }
    }

    private void saveDownloadInfo() {
        if (mIsSupportFTP) {
            mDownloadInfo.setDownloadSize(mDownFileSize);
            mDownloadInfo.setFileSize(mFileSize);
            mDatakeeper.saveDownloadInfo(mDownloadInfo);
        }
    }

    private void startNotice() {
        if (!mListenerMap.isEmpty()) {
            Collection<DownloadListener> c = mListenerMap.values();
            Iterator<DownloadListener> it = c.iterator();
            while (it.hasNext()) {
                DownloadListener listener = (DownloadListener) it.next();
                listener.onStart(getDownloadInfo());
            }
        }
    }

    private void onProgressNotice() {
        if (!mListenerMap.isEmpty()) {
            Collection<DownloadListener> c = mListenerMap.values();
            Iterator<DownloadListener> it = c.iterator();
            while (it.hasNext()) {
                DownloadListener listener = (DownloadListener) it.next();
                listener.onProgress(getDownloadInfo(), mIsSupportFTP);
            }
        }
    }

    private void stopNotice() {
        if (!mIsSupportFTP) {
            mDownFileSize = 0;
        }
        if (!mListenerMap.isEmpty()) {
            Collection<DownloadListener> c = mListenerMap.values();
            Iterator<DownloadListener> it = c.iterator();
            while (it.hasNext()) {
                DownloadListener listener = (DownloadListener) it.next();
                listener.onStop(getDownloadInfo(), mIsSupportFTP);
            }
        }
    }

    private void errorNotice(String error) {
        if (!mListenerMap.isEmpty()) {
            Collection<DownloadListener> c = mListenerMap.values();
            Iterator<DownloadListener> it = c.iterator();
            while (it.hasNext()) {
                DownloadListener listener = (DownloadListener) it.next();
                listener.onError(getDownloadInfo(), error);
            }
        }
    }

    private void successNotice() {
        if (!mListenerMap.isEmpty()) {
            Collection<DownloadListener> c = mListenerMap.values();
            Iterator<DownloadListener> it = c.iterator();
            while (it.hasNext()) {
                DownloadListener listener = (DownloadListener) it.next();
                listener.onSuccess(getDownloadInfo());
            }
        }
    }

   private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TASK_START) {
                mDownloadInfo.setSpeed(0);
                startNotice();
            } else if (msg.what == TASK_STOP) {
                mDownloadInfo.setSpeed(0);
                saveDownloadInfo();
                stopNotice();
            } else if (msg.what == TASK_PROGESS) {
                onProgressNotice();
            } else if (msg.what == TASK_ERROR) {
                mDownloadInfo.setSpeed(0);
                errorNotice((String) msg.obj);
            } else if (msg.what == TASK_SUCCESS) {
                mDownloadInfo.setSpeed(0);
                SPUtils.saveDownloadState(mContext,
                        mDownloadInfo.getPackageName(), Constants.APP_DOWNLOAD_FINISHED);
                successNotice();
                saveDownloadInfo();
                MainActivity.mHandler.sendMessage(MainActivity.mHandler.
                        obtainMessage(Constants.INSTALL_APK, mDownloadInfo.getFilePath()));
            }
        }
    };

    public boolean RenameFile() {
        File newfile = new File(mDownloadInfo.getFilePath());
        if (newfile.exists()) {
            newfile.delete();
        }
        File olefile = FileHelper.getDownloadTempFile(mDownloadInfo.getFilePath());
        return olefile.renameTo(newfile);
    }
}
