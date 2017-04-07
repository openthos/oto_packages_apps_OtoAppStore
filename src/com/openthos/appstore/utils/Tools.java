package com.openthos.appstore.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.openthos.appstore.app.Constants;

import java.io.Closeable;
import java.io.IOException;
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

    public static boolean closeStream(Closeable... stream) {
        for (int i = 0; i < stream.length; i++) {
            if (stream[i] != null) {
                try {
                    stream[i].close();
                    return true;
                } catch (IOException e) {
                    return false;
                }
            }
        }
        return false;
    }

    public static String transFormFileSize(long fileSize) {
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

    public static long tranLong(String num) {
        try {
            return Long.parseLong(num);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
