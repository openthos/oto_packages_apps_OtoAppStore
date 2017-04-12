package com.openthos.appstore.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.openthos.appstore.app.Constants;

import java.text.DecimalFormat;

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
}
