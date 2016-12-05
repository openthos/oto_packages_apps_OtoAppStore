package com.openthos.appstore.utils.download;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class DownLoadService extends Service {
    private static DownLoadManager downLoadManager;

    @Override
    public IBinder onBind(Intent intent) {
        return new AppStoreBinder();
    }

    public static DownLoadManager getDownLoadManager() {
        return downLoadManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downLoadManager = new DownLoadManager(DownLoadService.this);
        downLoadManager.setSupportBreakpoint(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        downLoadManager.stopAllTask();
        downLoadManager = null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (downLoadManager == null) {
            downLoadManager = new DownLoadManager(DownLoadService.this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public class AppStoreBinder extends Binder {
        public void stopTask(String taskId) {
            downLoadManager.stopTask(taskId);
        }

        public void startTask(String taskId) {
            downLoadManager.startTask(taskId);
        }

        public void addTask(String task, String url, String fileName) {
            downLoadManager.addTask(task, url, fileName);
        }

        public void startAllTask() {
            downLoadManager.startAllTask();
        }

        public void stopAllTask() {
            downLoadManager.stopAllTask();
        }
    }
}