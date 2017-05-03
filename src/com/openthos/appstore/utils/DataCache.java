package com.openthos.appstore.utils;

import android.content.Context;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.app.StoreApplication;

public class DataCache {
    public static String loadNetData(Context context, String url) {
        String data = null;
        if (!NetUtils.isConnected(context)) {
            MainActivity.mHandler.sendMessage(MainActivity.mHandler.obtainMessage(
                    Constants.TOAST, context.getString(R.string.check_net_state)));
            data = loadLocalData(context, url);
        } else {
            data = NetUtils.getNetStr(url);
            if (data != null) {
                SPUtils.saveData(context, Constants.SP_CACHE_DATA, url, data);
            } else {
                data = loadLocalData(context, url);
            }
        }
        return data;
    }

    public static String loadLocalData(Context context, String key) {
        return SPUtils.getData(context, Constants.SP_CACHE_DATA, key);
    }
}
