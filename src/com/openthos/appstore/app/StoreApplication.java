package com.openthos.appstore.app;

import android.app.Application;
import android.text.TextUtils;

import com.openthos.appstore.bean.AllDataInfo;
import com.openthos.appstore.bean.AppLayoutGridviewInfo;
import com.openthos.appstore.utils.NetUtils;
import com.openthos.appstore.utils.SPUtils;
import com.openthos.appstore.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by luojunhuan on 16-10-25.
 */
public class StoreApplication extends Application {
    public static final String DATE_FORMAT =
            new SimpleDateFormat("yyyyMMdd").format(new Date(System.currentTimeMillis()));

    @Override
    public void onCreate() {
        super.onCreate();
        saveAllData();
    }

    private void saveAllData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String allData = NetUtils.getNetStr(Constants.BASEURL + "/all");
                if (!TextUtils.isEmpty(allData)) {
                    try {
                        AllDataInfo allDataInfo = new AllDataInfo(new JSONObject(allData));
                        if (allDataInfo != null && allDataInfo.getAppList() != null) {
                            List<AppLayoutGridviewInfo> appList = allDataInfo.getAppList();
                            for (int i = 0; i < appList.size(); i++) {
                                AppLayoutGridviewInfo appInfo = appList.get(i);
                                SPUtils.saveAllData(StoreApplication.this, appInfo);
                            }
                        }
                        Tools.printLog("SA", allDataInfo.getAppList().size() + "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}