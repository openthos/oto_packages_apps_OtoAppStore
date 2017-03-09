package com.openthos.appstore.utils;

import android.content.Context;

import com.openthos.appstore.app.Constants;
import com.openthos.appstore.app.StoreApplication;

/**
 * Created by root on 3/7/17.
 */

public class DataCache {
    public static String loadData(Context context, String url) {
        String data = null;
        String saveData = SPUtils.getData(context, Constants.SP_CACHE_DATA, "saveData");
        if (!NetUtils.isConnected(context)) {
            data = SPUtils.getData(context,
                    Constants.SP_CACHE_DATA, url + StoreApplication.DATE_FORMAT);
        } else {
            if (saveData != null && saveData.equals(StoreApplication.DATE_FORMAT)) {
                data = SPUtils.getData(context,
                        Constants.SP_CACHE_DATA, url + StoreApplication.DATE_FORMAT);
            } else {
                SPUtils.clearData(context, Constants.SP_CACHE_DATA);
                SPUtils.saveData(context,
                        Constants.SP_CACHE_DATA, "saveData", StoreApplication.DATE_FORMAT);
            }
        }
        if (data == null) {
            data = NetUtils.getNetStr(context, url);
            SPUtils.saveData(context,
                    Constants.SP_CACHE_DATA, url + StoreApplication.DATE_FORMAT, data);
        }
        return data;
    }
}
