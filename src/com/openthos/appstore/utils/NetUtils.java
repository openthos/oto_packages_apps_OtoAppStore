package com.openthos.appstore.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.R;
import com.openthos.appstore.app.Constants;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by luojunhuan on 16-10-31.
 */
public class NetUtils {
    private NetUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (mConnectivityManager != null) {
                NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if (mNetworkInfo != null) {
                    return mNetworkInfo.isAvailable();
                }
            }
        }
        return false;
    }

    public static String getNetStr(Context context, String path) {
        try {
            if (isConnected(context)) {
                InputStream is = null;
                HttpURLConnection conn = null;
                try {
                    URL url = new URL(Constants.BASEURL + path);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5 * 1000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.connect();
                    int code = conn.getResponseCode();

                    if (code == HttpURLConnection.HTTP_OK) {
                        is = conn.getInputStream();
                        int len = -1;
                        StringBuffer buffer = new StringBuffer();
                        byte[] bytes = new byte[1024];
                        while ((len = is.read(bytes)) != -1) {
                            buffer.append(new String(bytes, 0, len));
                        }
                        return new String(buffer.toString().getBytes("UTF-8"));
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    Tools.closeStream(is);
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            } else {
                Message message = MainActivity.mHandler.obtainMessage();
                message.what = Constants.TOAST;
                message.obj = context.getString(R.string.check_net_state);
                MainActivity.mHandler.sendMessage(message);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}