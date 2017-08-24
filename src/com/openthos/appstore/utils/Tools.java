package com.openthos.appstore.utils;

import android.content.Context;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.Toast;
import com.openthos.appstore.app.Constants;
import com.openthos.appstore.bean.AppItemInfo;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import static android.view.animation.Animation.INFINITE;
import static android.view.animation.Animation.RESTART;
import java.util.Map;

public class Tools {

    public static void printLog(String TAG, String content) {
        Log.i(TAG, content);
    }

    private static Toast mToast;

    public static void toast(Context context, String content) {
        if (mToast == null) {
            mToast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(content);
        }
        mToast.show();
    }

    public static String transformFileSize(long fileSize) {
        float formatSize;
        String unit = "b";
        if ((formatSize = fileSize / ((float) Constants.MB)) >= 1) {
            unit = "Mb";
        } else if ((formatSize = fileSize / (float) Constants.KB) >= 1) {
            unit = "Kb";
        }
        DecimalFormat format = new DecimalFormat("0.00");
        return format.format(formatSize) + unit;
    }

    public static long transformLong(String num){
        try {
            return Long.parseLong(num);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static void setDowningAnimation(ImageButton iv) {
        RotateAnimation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5F, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(2000);
        animation.setFillAfter(false);
        animation.setRepeatCount(INFINITE);
        animation.setRepeatMode(RESTART);
        iv.setAnimation(animation);
    }

    public static AppItemInfo getAppItemInfo(
            JSONObject obj, Map<String, AppItemInfo> appItemInfos) {
        String packageName = null;
        try {
            packageName = obj.getString("packageName");
            if (!appItemInfos.containsKey(packageName)) {
                AppItemInfo appItemInfo = new AppItemInfo(obj);
                appItemInfos.put(packageName, appItemInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            appItemInfos.put(packageName, null);
        }
        return appItemInfos.get(packageName);
   }
}
