package com.openthos.appstore.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

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
        String s = context.getSharedPreferences(fileName, Context.MODE_PRIVATE).getString(key, "");
        if (s != null && !TextUtils.isEmpty(s)) {
            return s;
        }
        return null;
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
                "\"size\":" + appInfo.getSize() +
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
            if (entry.getKey().toLowerCase().contains(content.toLowerCase().trim())) {
                list.add(entry.getKey());
            }
        }
        return list;
    }
}