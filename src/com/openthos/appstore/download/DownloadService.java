package com.openthos.appstore.download;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import com.openthos.appstore.app.StoreApplication;
import com.openthos.appstore.bean.AppItemInfo;
import com.openthos.appstore.bean.NetDataListInfo;
import com.openthos.appstore.utils.NetUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Parcel;
import android.os.RemoteException;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.openthos.seafile.ISeafileService;

public class DownloadService extends Service {
    public Map<String, AppItemInfo> mAllAppItemInfos = new HashMap<>();
    private static DownloadManager mDownloadManager;
    private ArrayList<String> mPackageNameList;
    private ISeafileService mBinder;
    public static Handler mHandler;
    public static final int DOWNLOAD_START = 0;
    public static final int DOWNLOAD_SUCCESS = 1;
    private List<AppItemInfo> mAppItemInfoList;
    private List<String> mDownloadablePackageNames = new ArrayList<>();
    private int mDownloadSuccessCount;
    public static final String DESCRIPTOR = "com.openthos.seafile.ISeafileService";

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
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return super.bindService(service, conn, flags);
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
        mPackageNameList = null;
        if (intent != null) {
            mPackageNameList = intent.getStringArrayListExtra("packageNames");
        }
        if (mPackageNameList != null) {
            filterPackageNameList(mPackageNameList);
            if (mPackageNameList != null) {
                Intent in = new Intent();
                in.setComponent(new ComponentName("com.openthos.seafile",
                            "com.openthos.seafile.SeafileService"));
                bindService(in, new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        mBinder = ISeafileService.Stub.asInterface(service);
                        if (mPackageNameList.size() == 0) {
                            finishDownloadForSeafile();
                        }
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                    }
                }, BIND_AUTO_CREATE);
                mDownloadSuccessCount = 0;
                mDownloadablePackageNames.clear();
                initHandler();
                preDownloadApps();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void filterPackageNameList(ArrayList<String> noFilterPackageNameList) {
        // has installed ?
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> allAppList = packageManager.queryIntentActivities(intent, 0);
        String packageName = null;
        for (int i = 0; i < allAppList.size(); i++) {
            packageName = allAppList.get(i).activityInfo.packageName;
            noFilterPackageNameList.remove(packageName);
        }
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
            mDownloadManager.addTask(task, url, fileName, packageName, iconUrl, true);
        }

        public void startAllTask() {
            mDownloadManager.startAllTask();
        }

        public void stopAllTask() {
            mDownloadManager.stopAllTask();
        }
    }

    public void preDownloadApps() {
        new Thread() {

            @Override
            public void run() {
                String allData = NetUtils.getNetStr("/all");
                if (!TextUtils.isEmpty(allData)) {
                    try {
                        NetDataListInfo netDataInfos = new NetDataListInfo(
                                new JSONObject(allData), DownloadService.this);
                        mAppItemInfoList = netDataInfos.getNetDataInfoList();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (mAppItemInfoList != null) {
                        mHandler.sendEmptyMessage(DOWNLOAD_START);
                    }
                }
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case DownloadService.DOWNLOAD_START:
                        for (AppItemInfo appItemInfo : mAppItemInfoList) {
                            if (mPackageNameList.contains(appItemInfo.getPackageName())) {
                                String packageName = appItemInfo.getPackageName();
                                mDownloadablePackageNames.add(packageName);
                                mDownloadManager.addTask(packageName, StoreApplication.mBaseUrl +
                                        "/" + appItemInfo.getDownloadUrl(),
                                        appItemInfo.getAppName(), packageName,
                                        appItemInfo.getIconUrl(), false);
                            }
                        }
                        break;
                    case DownloadService.DOWNLOAD_SUCCESS:
                        mDownloadSuccessCount++;
                        AppItemInfo appItemInfo = (AppItemInfo) msg.obj;
                        boolean isDone = mDownloadablePackageNames.size() == mDownloadSuccessCount;
                        sendInfoToSeafile(appItemInfo.getPackageName());
                        if (isDone) {
                            finishDownloadForSeafile();
                        }
                        break;
                }
            }
        };
    }

    private void finishDownloadForSeafile() {
       Parcel _data = Parcel.obtain();
       Parcel _reply = Parcel.obtain();
       _data.writeInterfaceToken(DESCRIPTOR);
       try {
           mBinder.asBinder().transact(mBinder.getCodeDownloadFinish(), _data, _reply, 0);
       } catch (RemoteException e) {
           e.printStackTrace();
       } finally {
           _data.recycle();
           _reply.recycle();
       }
    }

    private void sendInfoToSeafile(String appName) {
       Parcel _data = Parcel.obtain();
       Parcel _reply = Parcel.obtain();
       _data.writeString(appName);
       try {
           mBinder.asBinder().transact(mBinder.getCodeSendInto(), _data, _reply, 0);
       } catch (RemoteException e) {
           e.printStackTrace();
       } finally {
           _data.recycle();
           _reply.recycle();
       }
    }
}
