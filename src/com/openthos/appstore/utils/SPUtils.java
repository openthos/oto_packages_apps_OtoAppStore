package com.openthos.appstore.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

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
        if (s != null && !TextUtils.isEmpty(s)){
            return s;
        }
        return null;
    }
}