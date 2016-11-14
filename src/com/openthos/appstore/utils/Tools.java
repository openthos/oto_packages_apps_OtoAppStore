package com.openthos.appstore.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by luojunhuan on 16-10-27.
 */
public class Tools {

    /**
     *
     * @param TAG
     * @param content
     */
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

    public static boolean closeStream(Closeable... stream){
        for (int i = 0; i < stream.length; i++) {
            if (stream[i] != null){
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
}
