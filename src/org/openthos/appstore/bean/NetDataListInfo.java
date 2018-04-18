package org.openthos.appstore.bean;

import android.content.Context;

import org.openthos.appstore.MainActivity;
import org.openthos.appstore.download.DownloadService;
import org.openthos.appstore.utils.Tools;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NetDataListInfo {
    private int result;
    private String message;
    private List<AppItemInfo> netDataInfoList;
    public NetDataListInfo(JSONObject obj, Context context) throws JSONException {
        result = obj.getInt("result");
        message = obj.getString("message");
        netDataInfoList = new ArrayList<>();
        AppItemInfo appItemInfo = null;
        JSONArray arr = obj.getJSONArray("data");
        for (int i = 0; i < arr.length(); i++) {
            appItemInfo = Tools.getAppItemInfo(arr.getJSONObject(i),
                context instanceof MainActivity ?
                    ((MainActivity)context).mAllAppItemInfos :
                    ((DownloadService)context).mAllAppItemInfos);
            if (appItemInfo == null) {
                continue;
            } else {
                netDataInfoList.add(appItemInfo);
            }
        }
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<AppItemInfo> getNetDataInfoList() {
        return netDataInfoList;
    }

    public void setNetDataInfoList(List<AppItemInfo> netDataInfoList) {
        this.netDataInfoList = netDataInfoList;
    }

    @Override
    public String toString() {
        return "NetDataListInfo{" +
                "result=" + result +
                ", message='" + message + '\'' +
                ", netDataInfoList=" + netDataInfoList +
                '}';
    }
}
