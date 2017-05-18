package com.openthos.appstore.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.openthos.appstore.app.Constants;
import com.openthos.appstore.app.StoreApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class NetUtils {
    public static boolean isConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (mConnectivityManager != null) {
                NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                    return mNetworkInfo.isConnected();
                }
            }
        }
        return false;
    }

    public static String getNetStr(String urlPath) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(StoreApplication.mBaseUrl + urlPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(Constants.TIME_FIVE_SECONDS);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuffer buffer = new StringBuffer();
                String line = null;
                while ((line=reader.readLine()) != null){
                    buffer.append(line + "\n");
                }
                return buffer.toString();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }
}
