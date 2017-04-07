package com.openthos.appstore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.openthos.appstore.app.Constants;
import com.openthos.appstore.utils.AppUtils;
import com.openthos.appstore.utils.SPUtils;

public class AppInstallReceiver extends BroadcastReceiver {

    public AppInstallReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_PACKAGE_ADDED:
            case Intent.ACTION_PACKAGE_REPLACED:
                if (intent.getDataString().substring(8) != null) {
                    SPUtils.saveDownloadState(context,
                            intent.getDataString().substring(8), Constants.APP_HAVE_INSTALLED);
                }
                break;
            case Intent.ACTION_PACKAGE_REMOVED:
                if (intent.getDataString().substring(8) != null) {
                    SPUtils.saveDownloadState(context,
                            intent.getDataString().substring(8), Constants.APP_NOT_INSTALL);
                }
                break;
        }
        try {
            MainActivity.mAppPackageInfo = AppUtils.getAppPackageInfo(context);
            if (MainActivity.mHandler != null) {
                MainActivity.mHandler.sendEmptyMessage(Constants.REFRESH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
