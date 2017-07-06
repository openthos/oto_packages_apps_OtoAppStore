package com.openthos.appstore.download;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class DownloadService extends Service {
    private static DownloadManager mDownloadManager;

    @Override
    public IBinder onBind(Intent intent) {
        return new AppStoreBinder();
    }

    public static DownloadManager getDownloadManager() {
        return mDownloadManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadManager = new DownloadManager(DownloadService.this);
        mDownloadManager.setSupportFTP(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDownloadManager.stopAllTask();
        mDownloadManager = null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (mDownloadManager == null) {
            mDownloadManager = new DownloadManager(DownloadService.this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public class AppStoreBinder extends Binder {
        public void stopTask(String taskId) {
            mDownloadManager.stopTask(taskId);
        }

        public void startTask(String taskId) {
            mDownloadManager.startTask(taskId);
        }

        public void addTask(String task, String url, String fileName,
                            String packageName, String iconUrl) {
            mDownloadManager.addTask(task, url, fileName, packageName, iconUrl);
        }

        public void startAllTask() {
            mDownloadManager.startAllTask();
        }

        public void stopAllTask() {
            mDownloadManager.stopAllTask();
        }
    }
}
