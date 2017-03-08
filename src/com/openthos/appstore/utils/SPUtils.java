package com.openthos.appstore.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppLayoutGridviewInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by luojunhuan on 16-10-31.
 */
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

    public static void saveDownloadState(Context context, String key, int data) {
        SharedPreferences.Editor edit = context.getSharedPreferences(
                Constants.SP_DOWNLOAD_STATE, Context.MODE_PRIVATE).edit();
        edit.putInt(key, data);
        edit.commit();
    }

    public static int getDownloadState(Context context, String key) {
        return context.getSharedPreferences(Constants.SP_DOWNLOAD_STATE,
                Context.MODE_PRIVATE).getInt(key, Constants.APP_NOT_EXIST);
    }

    public static void saveAllData(Context context, AppLayoutGridviewInfo appInfo) {
        SharedPreferences.Editor edit =
                context.getSharedPreferences(Constants.SP_ALL_DATA, Context.MODE_PRIVATE).edit();
        String json = "{\"id\":" + appInfo.getId() + "," +
                "\"packagename\":\"" + appInfo.getAppPackageName() + "\"," +
                "\"name\":\"" + appInfo.getName() + "\"," +
                "\"version\":\"" + appInfo.getVersionName() + "\"," +
                "\"icon\":\"" + appInfo.getIconUrl() + "\"," +
                "\"download\":\"" + appInfo.getDownloadUrl() + "\"," +
                "\"size\":" + appInfo.getFileSize() +
                "}";
        edit.putString(appInfo.getName(), json);
        edit.commit();
    }

    public static List<String> getSearchData(Context context, String content) {
        List<String> list = new ArrayList<>();
        SharedPreferences sp =
                context.getSharedPreferences(Constants.SP_ALL_DATA, Context.MODE_PRIVATE);
        Map<String, ?> all = sp.getAll();
        Set<? extends Map.Entry<String, ?>> entries = all.entrySet();
        for (Map.Entry<String, ?> entry : entries) {
            if (entry.getKey() != null && content != null &&
                    entry.getKey().toLowerCase().contains(content.toLowerCase().trim())) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    public static void clearData(Context context, String fileName) {
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit().clear().commit();
    }
}
