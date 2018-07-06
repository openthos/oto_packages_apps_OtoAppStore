package org.openthos.appstore.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.openthos.appstore.app.Constants;
import org.openthos.appstore.bean.AppItemInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SPUtils {
    public static void saveData(Context context, String fileName, String key, String data) {
        SharedPreferences.Editor edit =
                context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit();
        edit.putString(key, data);
        edit.commit();
    }

    public static String getData(Context context, String fileName, String key) {
        return context.getSharedPreferences(
                fileName, Context.MODE_PRIVATE).getString(key, null);
    }

    public static synchronized void saveAllData(Context context, List<AppItemInfo> appItemInfos) {
        SharedPreferences.Editor edit =
                context.getSharedPreferences(Constants.SP_ALL_DATA, Context.MODE_PRIVATE).edit();
        edit.clear();
        String json = null;
        for (AppItemInfo appInfo : appItemInfos) {
            json = "{\"taskId\":" + appInfo.getTaskId() + "," +
                    "\"fileSize\":\"" + appInfo.getFileSize() + "\"," +
                    "\"packageName\":\"" + appInfo.getPackageName() + "\"," +
                    "\"appName\":\"" + appInfo.getAppName() + "\"," +
                    "\"versionName\":\"" + appInfo.getVersionName() + "\"," +
                    "\"versionCode\":\"" + appInfo.getVersionCode() + "\"," +
                    "\"downloadUrl\":\"" + appInfo.getDownloadUrl() + "\"," +
                    "\"iconUrl\":\"" + appInfo.getIconUrl() + "\"," +
                    "\"describle\":\"" + appInfo.getDescrible() + "\"," +
                    "\"type\":\"" + appInfo.getType() + "\"," +
                    "\"company\":\"" + appInfo.getCompany() + "\"," +
                    "\"star\":" + appInfo.getStar() +
                    "}";
            edit.putString(appInfo.getPackageName(), json);
        }
        edit.commit();
    }

    public static List<String> getAllData(Context context) {
        return getSearchData(context, null);
    }

    public static List<String> getSearchData(Context context, String content) {
        List<String> list = new ArrayList<>();
        SharedPreferences sp =
                context.getSharedPreferences(Constants.SP_ALL_DATA, Context.MODE_PRIVATE);
        Map<String, String> all = (Map<String, String>) sp.getAll();
        if (!TextUtils.isEmpty(content)) {
            for (Map.Entry<String, String> entry : all.entrySet()) {
                if (entry.getKey().toLowerCase().contains(content.toLowerCase().trim())) {
                    list.add(entry.getValue());
                }
            }
        } else {
            for (Map.Entry<String, String> entry : all.entrySet()) {
                list.add(entry.getValue());
            }
        }

        return list;
    }

    public static void clearData(Context context, String fileName) {
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit().clear().commit();
    }
}
