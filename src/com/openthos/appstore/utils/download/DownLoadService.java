package com.openthos.appstore.utils.download;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DownLoadService extends Service {
    private static DownLoadManager downLoadManager;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    //public static DownLoadManager getDownLoadManager() {
    //    return mDownLoadManager;
    //}

    @Override
    public void onCreate() {
        super.onCreate();
        downLoadManager = new DownLoadManager(DownLoadService.this);
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
	if (downLoadManager == null) {
            downLoadManager = new DownLoadManager(DownLoadService.this);
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
