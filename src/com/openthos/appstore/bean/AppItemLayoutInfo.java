package com.openthos.appstore.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AppItemLayoutInfo {
    private String type;
    private String whole;
    private List<AppItemInfo> appItemInfoList;

    public AppItemLayoutInfo(JSONObject obj) throws JSONException {
        type = obj.getString("type");
        whole = obj.getString("whole");
        JSONArray array = obj.getJSONArray("data");
        appItemInfoList = new ArrayList<>();
        AppItemInfo appItemInfo = null;
        for (int i = 0; i < array.length(); i++) {
            appItemInfo = new AppItemInfo(array.getJSONObject(i));
            appItemInfoList.add(appItemInfo);
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWhole() {
        return whole;
    }

    public void setWhole(String whole) {
        this.whole = whole;
    }

    public List<AppItemInfo> getAppItemInfoList() {
        return appItemInfoList;
    }

    public void setAppItemInfoList(List<AppItemInfo> appItemInfoList) {
        this.appItemInfoList = appItemInfoList;
    }
}
