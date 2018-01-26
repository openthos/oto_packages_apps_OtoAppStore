package com.openthos.appstore.download;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppItemInfo;
import com.openthos.appstore.utils.FileHelper;
import com.openthos.appstore.utils.SQLOperator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

    private static final int BUFFER_READ_BYTE = 512 * 1024;

    private static final int MAX_NUMBER_OF_DOWNLOAD = 3;

    private static Map<String, DownloadListener> mListenerMap = new HashMap<>();
    private boolean mIsSupportFTP = false;
    private boolean mOndownload;
    private boolean mIsDelete;
    private int mNumberOfDownload;
    private long mFileSize;
    private long mDownFileSize;
    private String mUserID;
    private Context mContext;
    private SQLOperator mDatakeeper;
    private AppItemInfo mDownloadInfo;
    private DownloadThread mDownloadThread;
    private ThreadPoolExecutor mPool;
    private boolean mNeedUI;

    public Downloader(Context context, AppItemInfo sqlFileInfo, ThreadPoolExecutor pool,
                      String userID, boolean isSupportFTP, boolean isNewTask, boolean needUI) {
        mContext = context;
        mIsSupportFTP = isSupportFTP;
        mPool = pool;
        mUserID = userID;
        mFileSize = sqlFileInfo.getFileSize();
        mDownFileSize = sqlFileInfo.getDownFileSize();
        mDatakeeper = new SQLOperator(context);
        mDownloadInfo = sqlFileInfo;
        mOndownload = false;
        mIsDelete = false;
        if (isNewTask) {
            saveDownloadInfo();
        }
        mNeedUI = needUI;
    }

    public String getTaskID() {
        return mDownloadInfo.getTaskId();
    }

    public void start() {
        if (mDownloadThread == null) {
            mNumberOfDownload = 0;
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
        mIsDelete = true;
        if (mDownloadThread != null) {
            mDownloadThread.stopDownload();
            mDownloadThread = null;
        } else {
            mDatakeeper.deleteDownloadInfo(mUserID, mDownloadInfo.getTaskId());
        }
        File downloadFile = FileHelper.getDownloadTempFile(mDownloadInfo.getFilePath());
        if (downloadFile.exists()) {
            downloadFile.delete();
        }
    }

    public boolean isDownloading() {
        return mOndownload;
    }

    public AppItemInfo getDownloadInfo() {
        mDownloadInfo.setDownFileSize(mDownFileSize);
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
            if (!FileHelper.getDownloadUrlFile(mDownloadInfo.getUrl()).exists()) {
                mDownFileSize = 0;
            }
            while (mNumberOfDownload < MAX_NUMBER_OF_DOWNLOAD) {

                try {
                    if (mDownFileSize == mFileSize
                            && mFileSize > 0) {
                        mOndownload = false;
                        mHandler.sendEmptyMessage(TASK_SUCCESS);
                        mNumberOfDownload = MAX_NUMBER_OF_DOWNLOAD;
                        mDownloadThread = null;
                        return;
                    }
                    url = new URL(mDownloadInfo.getUrl());
                    urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.setConnectTimeout(Constants.TIME_FIVE_SECONDS);
                    urlConn.setReadTimeout(Constants.TIME_FIVE_SECONDS);
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
                            openConnention();
                        }
                    }
                    saveDownloadInfo();
                    int responseCode = urlConn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK ||
                            responseCode == HttpURLConnection.HTTP_PARTIAL) {
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
                                mDownloadInfo.setDownFileSize(downloadSize);
                                timeMillis = currentTimeMillis;
                                mHandler.sendEmptyMessage(TASK_PROGESS);
                            }
                        }
                        if (mDownFileSize == mFileSize) {
                            boolean renameResult = RenameFile();
                            if (renameResult) {
                                mDownloadInfo.setDownFileSize(mFileSize);
                                saveDownloadInfo();
                                mHandler.sendEmptyMessage(TASK_SUCCESS);
                            } else {
                                FileHelper.deleteFile(tempFile);
                                mHandler.sendEmptyMessage(TASK_ERROR);
                            }

                            mDownloadThread = null;
                            mOndownload = false;
                        }
                        mNumberOfDownload = MAX_NUMBER_OF_DOWNLOAD;
                    } else if (responseCode == HttpURLConnection.HTTP_CLIENT_TIMEOUT) {
                        MainActivity.mHandler.sendMessage(
                                MainActivity.mHandler.obtainMessage(Constants.TOAST,
                                        mContext.getString(R.string.connect_timeout)));
                    }
                } catch (FileNotFoundException e) {
                    MainActivity.mHandler.sendMessage(
                            MainActivity.mHandler.obtainMessage(Constants.TOAST,
                                    mContext.getString(R.string.file_not_found)));
                    exceptionDeal(e);
                } catch (MalformedURLException e) {
                    exceptionDeal(e);
                } catch (IOException e) {
                    if (e.toString().contains("java.net.UnknownHostException")) {
                        MainActivity.mHandler.sendMessage(
                                MainActivity.mHandler.obtainMessage(Constants.TOAST,
                                        mContext.getString(R.string.network_is_available)));
                    }
                    exceptionDeal(e);
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
                    try {
                        if (bis != null) {
                            bis.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (bos != null) {
                            bos.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void exceptionDeal(Exception e) {
            if (isdownloading) {
                if (mIsSupportFTP) {
                    mNumberOfDownload++;
                    if (mNumberOfDownload >= MAX_NUMBER_OF_DOWNLOAD) {
                        if (mFileSize > 0) {
                            saveDownloadInfo();
                        }
                        mPool.remove(mDownloadThread);
                        mDownloadThread = null;
                        mOndownload = false;
                        mHandler.sendMessage(
                                mHandler.obtainMessage(TASK_ERROR, e.toString()));
                    }
                } else {
                    mNumberOfDownload = MAX_NUMBER_OF_DOWNLOAD;
                    mOndownload = false;
                    mDownloadThread = null;
                    mHandler.sendMessage(mHandler.obtainMessage(TASK_ERROR, e.toString()));
                }

            } else {
                mNumberOfDownload = MAX_NUMBER_OF_DOWNLOAD;
            }
            e.printStackTrace();
        }

        private void stopDownload() {
            isdownloading = false;
            mNumberOfDownload = MAX_NUMBER_OF_DOWNLOAD;
            if (mFileSize > 0) {
                saveDownloadInfo();
            }
            mHandler.sendEmptyMessage(TASK_STOP);
        }

        private void openConnention() throws IOException {
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
            mDownloadInfo.setDownFileSize(mDownFileSize);
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
                if (mIsDelete) {
                    mDatakeeper.deleteDownloadInfo(mUserID, mDownloadInfo.getTaskId());
                } else {
                    saveDownloadInfo();
                }
                mIsDelete = false;
                stopNotice();
            } else if (msg.what == TASK_PROGESS) {
                saveDownloadInfo();
                onProgressNotice();
            } else if (msg.what == TASK_ERROR) {
                mDownloadInfo.setSpeed(0);
                errorNotice((String) msg.obj);
            } else if (msg.what == TASK_SUCCESS) {
                mDownloadInfo.setSpeed(0);
                successNotice();
                saveDownloadInfo();
                if (mNeedUI) {
                    MainActivity.mHandler.sendMessage(MainActivity.mHandler.
                            obtainMessage(Constants.INSTALL_APK, mDownloadInfo));
                } else {
                    DownloadService.mHandler.sendMessage(DownloadService.mHandler.
                            obtainMessage(DownloadService.DOWNLOAD_SUCCESS, mDownloadInfo));
                }
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
